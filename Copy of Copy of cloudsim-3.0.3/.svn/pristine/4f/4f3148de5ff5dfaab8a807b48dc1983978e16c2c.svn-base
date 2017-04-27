package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.policy.ListAllocationPolicy;
import org.cloudbus.cloudsim.policy.utils.ExtendedHelper;
import org.cloudbus.cloudsim.policy.utils.HelpUtils;

import org.cloudbus.cloudsim.hust.base.PhysicalMachine;
import org.cloudbus.cloudsim.hust.base.VirtualMachine;

//使用最佳适应算法进行虚拟机部署
public class VmAllocationPolicyBestFit extends VmAllocationPolicy implements ListAllocationPolicy {
    /** The vm table. */
    private Map<String, Host> vmTable;
    public static Random random=new Random();
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
	public static PhysicalMachine []remain_pms; //物理机剩余性能
	public static VirtualMachine  []origin_vms; //物理机原始性能
	PhysicalMachine [] normalized_pm; //归一化的物理机性能
	VirtualMachine [] normalized_vm;  //归一化的虚拟机性能
	
    int lastAllocatedHost;
    
	public VmAllocationPolicyBestFit(List<? extends Host> list) {
        super(list);
        lastAllocatedHost=0;
        setVmTable(new HashMap<String, Host>());
	}
	
    private void printLogMsg(String msg) {
        Log.print("BF_Allocator: " + msg + LINE_SEPARATOR);
    }
    

	@SuppressWarnings("static-access")
	@Override
	public boolean allocateHostForVmList(List<Vm> vmsToAllocate) {
		 printLogMsg("Allocating host for "+vmsToAllocate.size()+" vms ");
		 //遍历每一个虚拟机
		 int count=0,size=vmsToAllocate.size();
		 //封装虚拟机对象
		 this.origin_vms=HelpUtils.createVirtualMachineFromStart(vmsToAllocate);
		 //虚拟机性能归一化
		 this.normalized_vm=HelpUtils.getNormalized_Vm(this.origin_vms);
		 
		 for(Vm vm:vmsToAllocate){     
			 //拿到物理机的剩余性能
			 List<Host> pmList_current=getHostList();  //获取物理机列表
			 //对剩余资源进行归一化 [注意保证每次获取的资源都是最新的]
			  this.remain_pms=HelpUtils.createPhysicalMachineByCurrentTime(pmList_current);
			  this.normalized_pm=HelpUtils.getNormalized_Pm(this.remain_pms); //这个地方已经出错

			 if(allocateHostForVm(vm))
				    count++;
			 else{
				 allocateHostForVmByRoundRobin(vm);
				 count++;
			 }
		 }
		 return count==size;
	}


	@Override
	public boolean allocateHostForVm(Vm vm) {
	    @SuppressWarnings("unused")
		int hostIndex,hostSize; //物理机编号
        int vmIndex=vm.getId(); //虚拟机编号
        double min=1; //保存负载不均衡度值
        double matchDistance=0; //匹配距离
        int resultIndex=-1;
        hostSize=getHostList().size();
        
        for(Host host:getHostList()){
        	if(host.isSuitableForVm(vm)){
        		hostIndex=host.getId();
        		matchDistance=HelpUtils.getMatchDistance(this.normalized_pm[hostIndex], this.normalized_vm[vmIndex]);
            	if(matchDistance<min){
            		min=matchDistance;
            		resultIndex=hostIndex;
            	}
        	}
        }
	        //将虚拟机放置在最优物理机上
	     boolean result = getHostList().get(resultIndex).vmCreate(vm); //该方法会对物理机资源进行更新
	     
        if(result) { //可以放置
        	 lastAllocatedHost=resultIndex;
            printLogMsg("Vm:"+vm.getId()+ " Allocated on " + resultIndex);
            getVmTable().put(vm.getUid(),  getHostList().get(resultIndex));
            
            //放入物理机虚拟机映射
            Vector<Integer>value=deployMap.get(resultIndex);
            if(value==null)
            	value=new Vector<Integer>();
        	value.add(vm.getId());
        	deployMap.put(resultIndex, value);
            
            return true;
        }
        
        return false;
    }
    
    
    public boolean allocateHostForVmByRoundRobin(Vm vm) {
    	int currentIndex=lastAllocatedHost;
    	List<Host> hostList=getHostList();
    	int hostSize=hostList.size();
    	boolean result=true;
    	
    	do{
    		printLogMsg("Try to allocate vm="+ vm.getId() +" on  host=" +currentIndex);
    		result=allocateHostForVm(vm,hostList.get(currentIndex));
    		if(result){
    			break;
    		}
    		lastAllocatedHost=(lastAllocatedHost+1)%hostSize;
    		currentIndex=lastAllocatedHost;
    	}while(result==false);
      
    	return result;
  }


	@Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            //the host is appropriate, we track it
        	 printLogMsg("Vm created successfuly");
	            getVmTable().put(vm.getUid(), host);
	            
	            //放入物理机虚拟机映射
	            Vector<Integer>value=deployMap.get(host.getId());
	            if(value==null)
	            	value=new Vector<Integer>();
	        	value.add(vm.getId());
	        	deployMap.put(host.getId(), value);
	        	
	            return true;
        }
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
