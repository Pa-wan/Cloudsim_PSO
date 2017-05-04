package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.policy.ListAllocationPolicy;
import org.cloudbus.cloudsim.policy.utils.ExtendedHelper;

public class VmAllocationPolicyRoundRobin extends VmAllocationPolicy implements ListAllocationPolicy {

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
		 printLogMsg("Allocating host for "+vmsToAllocate.size()+" vms ");
		 //遍历每一个虚拟机
		 int count=0,size=vmsToAllocate.size();
		 for(Vm vm:vmsToAllocate){
			 if(allocateHostForVm(vm))
				 count++;
		 }
		 
		 if(count==size)
			 return true;
		return false;
	}
	
    boolean tryToAllocateVmToHost(Host host, Vm vm) {
        if(host.isSuitableForVm(vm)) {
            boolean result = host.vmCreate(vm);
            if(result) {
                printLogMsg("Vm created successfuly");
                getVmTable().put(vm.getUid(), host);
                
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
        printLogMsg("Allocate host for vm");
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
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = getVmTable().remove(vm.getUid());
        if (host != null) {
            host.vmDestroy(vm);
        }
    }

    @Override
    public Host getHost(Vm vm){
        return getVmTable().get(vm.getUid());
    }

    @Override
    public Host getHost(int vmId, int userId) {
        return getVmTable().get(Vm.getUid(userId, vmId));
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
