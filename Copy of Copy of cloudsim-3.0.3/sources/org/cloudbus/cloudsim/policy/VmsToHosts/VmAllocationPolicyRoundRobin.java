package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.hust.pso2.Utils;
import org.cloudbus.cloudsim.policy.ListAllocationPolicy;
import org.cloudbus.cloudsim.policy.utils.ExtendedHelper;

public class VmAllocationPolicyRoundRobin extends VmAllocationPolicy implements ListAllocationPolicy {

	private Map<String, Integer> vmToHost;
	private Map<Integer, ArrayList<Integer>> vmsInHost;
	private double banlanceDegree;// 总体不均衡度
    /** The vm table. */
    private Map<String, Host> vmTable;
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
    int lastAllocatedHost;
    
	public VmAllocationPolicyRoundRobin(List<? extends Host> list) {
        super(list);
        setVmTable(new HashMap<String, Host>());
        lastAllocatedHost = -1; //We want to start from 0 idx
	}
	
    private void printLogMsg(String msg) {
        Log.print("RR_Allocator: " + msg + LINE_SEPARATOR);
    }

	@Override
	public boolean allocateHostForVmList(List<Vm> vmsToAllocate) {
		return false;
	}
	
    boolean tryToAllocateVmToHost(Host host, Vm vm) {
        if(host.isSuitableForVm(vm)) {
            boolean result = host.vmCreate(vm);
            if(result) {
                printLogMsg("Vm created successfuly");
                getVmTable().put(vm.getUid(), host);
                host.getVmList().add(vm);
                Utils.updateVmResource(vm);
				vmToHost.put(vm.getUid(), host.getId());
				if (!vmsInHost.containsKey(host.getId())) {
					ArrayList<Integer> list = new ArrayList<Integer>();
					list.add(vm.getId());
					vmsInHost.put(host.getId(), list);
				} else {
					vmsInHost.get(host.getId()).add(vm.getId());
				}
                //放入物理机虚拟机映射
                Vector<Integer>value=deployMap.get(host.getId());
                if(value==null)
                	value=new Vector<Integer>();
            	value.add(vm.getId());
            	deployMap.put(host.getId(), value);
            	
                return true;
            } else {
                printLogMsg("Vm creation failed");
            }
        }
        return false;
    }
    
    @Override
    public boolean allocateHostForVm(Vm vm) {
    	List<Host> hostList=getHostList();
    	int hostSize=hostList.size();
    	
    	if(hostSize == 0) {
            return tryToAllocateVmToHost(hostList.get(0),vm);
        }

        int currentIndex = 0; //初始化
        
        //当前虚拟机选择的物理机是上一个虚拟机选择物理机的下一个
        if(lastAllocatedHost != hostSize-1) 
            currentIndex = lastAllocatedHost + 1;
       
        //没有轮询一周【若轮询一周，形成轮询闭环，仍然无法找到目标物理机】
        while (currentIndex != lastAllocatedHost) {
            printLogMsg("Try to allocate vm="+ vm.getId() +" on  host=" +currentIndex);
            //若能够部署，直接退出，保存上次使用的物理机下标
            if(tryToAllocateVmToHost(hostList.get(currentIndex), vm)) {
                lastAllocatedHost = currentIndex; //保存本次访问物理机，作为下一个物理机轮序的根据
                return true;
            }
            currentIndex=(currentIndex+1)%hostSize; //在没有部署成功时，继续进行轮询
        }//轮询一周没有发现目标主机，部署失败
        
        return false; //部署失败
    }

	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		int count=0, size=vmList.size();
		vmToHost = new HashMap<String, Integer>();
		vmsInHost = new HashMap<Integer, ArrayList<Integer>>();
		for(Vm vm:vmList){
			if(allocateHostForVm(vm)){
				count++;
			}	
		}
		if(count==size){
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
			}
		return null;
	}

    @Override
    public void deallocateHostForVm(Vm vm) {

    }

    @Override
    public Host getHost(Vm vm){
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
	public boolean allocateHostForVm(Vm vm, Host host) {
		// TODO Auto-generated method stub
		return false;
	}
	
    protected void setVmTable(Map<String, Host> vmTable) {
        this.vmTable = vmTable;
    }

    public Map<String, Host> getVmTable() {
        return vmTable;
    }
    
    public TreeMap<Integer, Vector<Integer>> getDeployMap() {
		return deployMap;
	}

}
