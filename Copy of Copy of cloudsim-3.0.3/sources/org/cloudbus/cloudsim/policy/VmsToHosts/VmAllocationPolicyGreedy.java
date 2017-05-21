package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.hust.pso2.Utils;

/**
 * 使用贪心算法，并能够保证得到一个较优质的解
 * 
 * @author Administrator
 * 
 */
public class VmAllocationPolicyGreedy extends VmAllocationPolicy {

	private Map<String, Integer> vmToHost;
	private Map<Integer, ArrayList<Integer>> vmsInHost;
	private double banlanceDegree;// 总体不均衡度

	public VmAllocationPolicyGreedy(List<? extends Host> list) {
		super(list);
		new HashMap<String, Host>();
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(
			List<? extends Vm> vmList) {
		vmToHost = new HashMap<String, Integer>();
		vmsInHost = new HashMap<Integer, ArrayList<Integer>>();
		ArrayList<Host> fithostlist = new ArrayList<Host>();
		Comparator<Host> comparator = new Comparator<Host>() {
			public int compare(Host host1, Host host2) {
				// 按照负载从小到大排序
				if(Double.compare(host1.getAvailableMips()/host1.getTotalMips(), host2.getAvailableMips()/host2.getTotalMips())!=0){
				return Double.compare(host2.getAvailableMips()/host2.getTotalMips(), host1.getAvailableMips()/host1.getTotalMips());}
				else if(Double.compare(host1.getRamProvisioner().getAvailableRam()/host1.getRam(), host2.getRamProvisioner().getAvailableRam()/host2.getRam())!=0){
					return Double.compare(host2.getRamProvisioner().getAvailableRam()/host2.getRam(), host1.getRamProvisioner().getAvailableRam()/host1.getRam());
				}
				else {
					return  Double.compare(host2.getBwProvisioner().getAvailableBw()/host2.getBw(), host1.getBwProvisioner().getAvailableBw()/host1.getBw());
				}
			}
		};
		for (Vm vm : vmList) {
			fithostlist.clear();
			for (Host host : getHostList()) {
				if (Utils.isSuitable(vm, host)) {
					fithostlist.add(host);// 将符合条件的物理主机放入数组中
				}
			}
			if (fithostlist.size() == 0)
				System.out.println(vm.getId() + "号虚拟机无合适物理机可以放置");
			else {
				Collections.sort(fithostlist, comparator);
				// 选择当前负载最小的主机放置该虚拟机
				Host besHost = fithostlist.get(0);
				besHost.getVmList().add(vm);
				besHost.vmCreate(vm);
				vm.setHost(fithostlist.get(0));// 将主机和虚拟机建立对应关系
				Utils.updateVmResource(vm);// 更新主机资源
				vmToHost.put(vm.getUid(), besHost.getId());
				if (!vmsInHost.containsKey(besHost.getId())) {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(vm.getId());
					vmsInHost.put(besHost.getId(), list);
				} else {
					vmsInHost.get(besHost.getId()).add(vm.getId());
				}
			}
			// System.out.println();
		}
		Object map = vmToHost;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add((Map<String, Object>) map);
		calcuMd();
		Iterator<Entry<Integer, ArrayList<Integer>>> itor = vmsInHost
				.entrySet().iterator();
		while (itor.hasNext()) {
			Entry<Integer, ArrayList<Integer>> entry = itor.next();
			System.out.print(entry.getKey() + ":size="
					+ entry.getValue().size() + "   ");
			for (Integer i : entry.getValue())
				System.out.print(i + "  ");
			System.out.println();
		}
		return list;
	}

	private void calcuMd() {
		double[] utilAvg = new double[getHostList().size()];
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (Host host : getHostList()) {
			double uCPU=(host.getTotalMips()-host.getAvailableMips())/host.getTotalMips();
			double uRam=host.getRamProvisioner().getUsedRam()/(host.getRam()+0.0);
			double uBw=host.getBwProvisioner().getUsedBw()/(host.getBw()+0.0);
			double load=Math.sqrt(uCPU*uCPU+uRam*uRam+uBw*uBw);
			utilAvg[host.getId()] =load;
		}
		banlanceDegree = StandardDiviation(utilAvg);
		System.out.println("banlanceDegree=" + banlanceDegree);
	}

	/**
	 * 求标准差
	 * 
	 * @param x
	 * @return
	 */
	private static double StandardDiviation(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {// 求和
			sum += x[i];
		}
		double dAve = sum / m;// 求平均值
		double dVar = 0;
		for (int i = 0; i < m; i++) {// 求方差
			dVar += (x[i] - dAve) * (x[i] - dAve);
		}
		return Math.sqrt(dVar / m);
	}

	public Host getHost(Vm vm) {
		// We must recover the Host which hosting Vm
		int id = vmToHost.get(vm.getUid());
		for (Host host : getHostList()) {
			if (host.getId() == id)
				return host;
		}
		return null;
	}

	public Host getHost(int vmId, int userId) {
		// We must recover the Host which hosting Vm
		String Uid = userId + "-" + vmId;
		int id = vmToHost.get(Uid);
		for (Host host : getHostList()) {
			if (host.getId() == id)
				return host;
		}
		return null;
	}

	public boolean allocateHostForVm(Vm vm, Host host) {
		return false;
	}

	public boolean allocateHostForVm(Vm vm) {
		return false;
	}

	@Override
	public void deallocateHostForVm(Vm v) {
		// get the host and remove the vm
	}

	public static Object optimizeAllocation() {
		return null;
	}

}
