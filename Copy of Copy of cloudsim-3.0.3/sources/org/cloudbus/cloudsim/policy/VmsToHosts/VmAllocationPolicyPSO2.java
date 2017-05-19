package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.hust.pso2.PSO;
import org.cloudbus.cloudsim.hust.pso2.Utils;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class VmAllocationPolicyPSO2 extends VmAllocationPolicy{
	private Map<String, Integer> vmToHost;
	private static Map<Integer,ArrayList<Integer>> vmsInHost;

	public VmAllocationPolicyPSO2(List<? extends Host> list) {
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
		int particleNum=100,gen=400;
		//double w=1,c1=3,c2=0.3;
		List<Vm> vms=(List<Vm>) vmList;
		PSO pso=new PSO(particleNum,gen,vms,getHostList());
		pso.solve();
		vmToHost=pso.getBestSolution().getVmTohost();
		vmsInHost=new HashMap<Integer, ArrayList<Integer>>();
		//在pso程序中，由于减少vmList，hostList的重复创建，每次循环结束重置，二者状态信息不能保存，序列化太复杂
		//解决方法：按照放置方案重新部署一次即可,最终解
		Iterator<Entry<String,Integer>> it=vmToHost.entrySet().iterator();
		while(it.hasNext()){
			Entry<String,Integer> entry=it.next();
			Host host=pso.getHostById().get(entry.getValue());
			Vm vm=pso.getVmByUid().get(entry.getKey());
			if(!vmsInHost.containsKey(host.getId())){
				ArrayList<Integer> list=new ArrayList<Integer>();
				list.add(vm.getId());
				vmsInHost.put(host.getId(), list);
			}else{
				vmsInHost.get(host.getId()).add(vm.getId());
			}
			host.vmCreate(vm);//更新资源，CurrentAllocatedMips,CurrentAllocatedSize
			host.getVmList().add(vm);
			vm.setCurrentAllocatedSize(vm.getSize());
			Utils.updateVmResource(vm);
		}
		Object map=vmToHost;
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		list.add((Map<String, Object>)map);
		
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

	@Override
	public void deallocateHostForVm(Vm vm) {
		
	}

	@Override
	public Host getHost(Vm vm) {
		int id=vmToHost.get(vm.getUid());
		for(Host host:getHostList()){
			if(host.getId()==id)
				return host;
		}
		return null;
	}

	@Override
	public Host getHost(int vmId, int userId) {
		String Uid=userId + "-" + vmId;
		int id=vmToHost.get(Uid);
		for(Host host:getHostList()){
			if(host.getId()==id)
				return host;
		}
		return null;
	}
	
}
