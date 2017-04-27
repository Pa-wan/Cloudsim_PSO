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

public class VmAllocationPolicyFirstFit extends VmAllocationPolicy implements ListAllocationPolicy {
    /** The vm table. */
    private Map<String, Host> vmTable;
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
	
	
	public VmAllocationPolicyFirstFit(List<? extends Host> list) {
        super(list);
        setVmTable(new HashMap<String, Host>());
	}
	
    private void printLogMsg(String msg) {
        Log.print("FF_Allocator: " + msg + LINE_SEPARATOR);
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


    @Override
    public boolean allocateHostForVm(Vm vm) {
        int idx = 0;
        for (Host host : getHostList()) {
            idx++;
            if(host.isSuitableForVm(vm)) {
                boolean result = host.vmCreate(vm);
                if(result) {
                    printLogMsg("Vm:"+vm.getId()+ " Allocated on " + host.getId());
                    getVmTable().put(vm.getUid(), host);
                    
                    //放入物理机虚拟机映射表
                    Vector<Integer>value=deployMap.get(host.getId());
                    if(value==null)
                    	value=new Vector<Integer>();
                	value.add(vm.getId());
                	deployMap.put(host.getId(), value);
                    
                    return true;
                } else {
                    printLogMsg("Vm creation failed on " + idx + " Lets try another host");
                    continue;
                }
            }
        }
        return false;
    }

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		// TODO Auto-generated method stub
		return false;
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
		
	
    /**
     * Sets the vm table.
     *
     * @param vmTable the vm table
     */
    protected void setVmTable(Map<String, Host> vmTable) {
        this.vmTable = vmTable;
    }
    /**
     * Gets the vm table.
     *
     * @return the vm table
     */
    public Map<String, Host> getVmTable() {
        return vmTable;
    }
    
    public TreeMap<Integer, Vector<Integer>> getDeployMap() {
		return deployMap;
	}
    
	
}
