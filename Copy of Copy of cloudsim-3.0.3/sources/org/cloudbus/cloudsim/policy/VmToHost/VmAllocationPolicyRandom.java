package org.cloudbus.cloudsim.policy.VmToHost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.hust.pso2.Utils;

public class VmAllocationPolicyRandom extends VmAllocationPolicy {
	private Map<String, Integer> vmToHost;
	private Map<Integer, ArrayList<Integer>> vmsInHost;
	private double banlanceDegree;// 总体不均衡度
	public VmAllocationPolicyRandom(List<? extends Host> list) {
		super(list);
	}

	@Override
	public boolean allocateHostForVm(Vm vm) {
		return false;
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		return false;
	}

	/**
	 * 算法的主要调用逻辑以及结果的获取
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(
			List<? extends Vm> vmList) {
		vmToHost = new HashMap<String, Integer>();
		vmsInHost = new HashMap<Integer, ArrayList<Integer>>();
		// 匹配可以放置该vm的物理机
		for (Vm vm : vmList) {
			ArrayList<Host> fithostlist = new ArrayList<Host>();
			for (Host host : getHostList()) {
				if (Utils.isSuitable(vm, host)) {
					fithostlist.add(host);// 将符合条件的物理主机放入数组中
				}
			}
			if (fithostlist.size() == 0)
				System.out.println(vm.getId() + "号虚拟机无合适物理机可以放置");
			else {
				// 从满足条件的主机中随机获取一个物理机编号
				int index = (int) (Math.random() * fithostlist.size());
				Host valueHost = fithostlist.get(index);
				valueHost.vmCreate(vm);
				valueHost.getVmList().add(vm);
				Utils.updateVmResource(vm);
				vmToHost.put(vm.getUid(), valueHost.getId());
				if (!vmsInHost.containsKey(valueHost.getId())) {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(vm.getId());
					vmsInHost.put(valueHost.getId(), list);
				} else {
					vmsInHost.get(valueHost.getId()).add(vm.getId());
				}
			}
		}
		Object map = vmToHost;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.add((Map<String, Object>) map);
		calcuMd();
		Iterator<Entry<Integer, ArrayList<Integer>>> itor=vmsInHost.entrySet().iterator();
		while(itor.hasNext()){
			Entry<Integer, ArrayList<Integer>> entry=itor.next();
			System.out.print(entry.getKey()+":size="+entry.getValue().size()+"   ");
			for(Integer i:entry.getValue())
			  System.out.print(i+"  ");
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
	@Override
	public void deallocateHostForVm(Vm vm) {

	}

	@Override
	public Host getHost(Vm vm) {
		int id = vmToHost.get(vm.getUid());
		for (Host host : getHostList()) {
			if (host.getId() == id)
				return host;
		}
		return null;
	}

	@Override
	public Host getHost(int vmId, int userId) {
		String Uid = userId + "-" + vmId;
		int id = vmToHost.get(Uid);
		for (Host host : getHostList()) {
			if (host.getId() == id)
				return host;
		}
		return null;
	}

}
