package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.Arrays;
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

public class VmAllocationPolicyRandom extends VmAllocationPolicy implements ListAllocationPolicy {
    /** The vm table. */
    private Map<String, Host> vmTable;
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
	
	
	public VmAllocationPolicyRandom(List<? extends Host> list) {
        super(list);
        setVmTable(new HashMap<String, Host>());
	}
	
	   private void printLogMsg(String msg) {
	        Log.print("RAND_Allocator: " + msg + LINE_SEPARATOR);
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

	        Random random = new Random();
	        int visitedHosts = 0; //初始化访问计数器
	        boolean visited[] = new boolean[hostList.size()];
	        Arrays.fill(visited,0,hostSize,false);//初始化设置没有访问
	        
	        while (visitedHosts < hostSize) { //访问次数<主机数[相当于产生互不相同的随机数]
	           
	        	int randomHostNum = random.nextInt(hostSize); //产生随机数
	            
	            //在发现目标物理主机前，一直使用随机数，同时保证只对主机访问一次，使用标志数组进行标记
	            if (visited[randomHostNum]) //同一个物理机，不进行第二次访问【下面这些步骤略过，跑到while循环，继续产生随机数】
	                continue;
	           
	            //当前编号的物理机没有被访问
	            printLogMsg("Try to allocate host: "+randomHostNum);
	            boolean res = tryToAllocateVmToHost(hostList.get(randomHostNum), vm);
	            if(res) {  //可以部署，直接返回true
	                return true;
	            } else { //当前物理机没法部署，计数器累计，设置应经访问
	                visitedHosts++; //访问主机数+1
	                visited[randomHostNum] = true;//设置该物理机已经被访问
	            }
	        }
	        return false;  //没有找到合适的物理机
	    }
	    
	    //该函数与上面实现相同
	    public boolean allocateHostForVm_modify(Vm vm) {
	        printLogMsg("Allocate host for vm");
	    	List<Host> hostList=getHostList();
	    	int hostSize=hostList.size();
	    	
	    	if(hostSize == 0) {
	            return tryToAllocateVmToHost(hostList.get(0),vm);
	        }
	    	
	    	//产生与物理机数量相同的随机数数组
	    	int [] NoRepeatHostIndexArray=HelpUtils.createNoRepeatNumber(hostSize);
	    	
	    	int visiter=0;
	    	boolean flag=false;
	    	for(;visiter<hostSize;visiter++){
	    		flag=tryToAllocateVmToHost(hostList.get(NoRepeatHostIndexArray[visiter]),vm);
	    		if(flag){
	    			return true; //找到，直接退出
	    		}
	    	}
	    	return flag;  //没有找到

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
