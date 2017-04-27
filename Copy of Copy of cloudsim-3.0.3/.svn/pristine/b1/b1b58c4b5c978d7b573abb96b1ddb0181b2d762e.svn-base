package org.cloudbus.cloudsim.policy.utils;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.policy.ListAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerDatacenter;

import org.cloudbus.cloudsim.hust.base.PhysicalMachine;
import org.cloudbus.cloudsim.hust.base.VirtualMachine;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;


public class ExtendedDatacenter extends PowerDatacenter {
    Logger logger;
    private int maximumUsedHostsCount = 0;
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
    public static TreeMap<Integer, Vector<Integer>>mydeployMap;
    
	public static PhysicalMachine []origin_pms; //物理机和虚拟机原始性能
	public static VirtualMachine [] origin_vms;
	
	public static PhysicalMachine []remain_pms; //物理机剩余性能
	
	public static double loadDegree; //负载不均衡度
    
    public int getMaximumUsedHostsCount() {
        return maximumUsedHostsCount;
    }
   
    private void printLogMsg(String msg) {
        Log.print("Extended_datacenter: " + msg + LINE_SEPARATOR);
    }
    
    public ExtendedDatacenter(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    }

    @Override
    public void processEvent(SimEvent ev) {
        if (ev.getTag() == ExtendedDatacenterBrocker.ALLOCATE_VM_LIST_TAG) { //若是多虚拟机映射多物理机
            ListAllocationPolicy policy = (ListAllocationPolicy)getVmAllocationPolicy(); //多对多形式
            @SuppressWarnings("unchecked")
			List<Vm> vmList = (List<Vm>)ev.getData(); //获取虚拟机
            List<Host> pmList_origin=getHostList();  //获取物理机列机
        	
        	
        	//物理虚拟机初始型能
            origin_pms=HelpUtils.createPhysicalMachineByCurrentTime(pmList_origin);
            origin_vms=HelpUtils.createVirtualMachineFromStart(vmList);
//			HelpUtils.printOrigin_Pms(origin_pms); //test code
//			HelpUtils.printOrigin_Vms(origin_vms);

            
            printLogMsg("Before allocate------Number of Vm is "+vmList.size()+" , Number Of Used Host is "+ (getHostList().size() - getUnusedHostsCount()));
           
            boolean result = policy.allocateHostForVmList(vmList);  //调用多虚拟机映射多物理机函数
            
            //无论是否部署完成，都输出部署表
            setMydeployMap(ListAllocationPolicy.deployMap); 
           
            if (result) {
//                setMydeployMap(ListAllocationPolicy.deployMap); //所有虚拟机均能够部署，设置部署表
            	
            	for (Vm vm: vmList){ //遍历虚拟机列列表
                    int[] data = new int[3];
                    data[0] = getId();
                    data[1] = vm.getId(); //虚拟机编号

                    if (result) { //映射完毕
                        data[2] = CloudSimTags.TRUE; //设置完成标志
                    } else {
                        data[2] = CloudSimTags.FALSE;
                    }
                    send(vm.getUserId(), CloudSim.getMinTimeBetweenEvents(), CloudSimTags.VM_CREATE_ACK, data);
                }

                getVmList().addAll(vmList); //加入虚拟机
                for (Vm vm: vmList) {
                    if (vm.isBeingInstantiated()) { //正在被创建
                        vm.setBeingInstantiated(false); //设置没有创建完成
                    }
                    //进行更新
                    vm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(vm).getVmScheduler()
                            .getAllocatedMipsForVm(vm));
                }
            }
            
            //物理机的 剩余资源【必须等待上面过程结束，得到的物理机性能才是更新过的
            List<Host> pmList_remain=getHostList();  //获取物理机列表
		    remain_pms=HelpUtils.createPhysicalMachineByCurrentTime(pmList_remain);
//			HelpUtils.printReamin_Pms(remain_pms); //test code
		    
		    //计算负载不均衡度度
		    setLoadDegree(HelpUtils.getLoadDegree(origin_pms,remain_pms));
            
            int used = getHostList().size() - getUnusedHostsCount(); //获取使用物理机数量
            if (used > maximumUsedHostsCount) { //更新物理机最大使用量
                maximumUsedHostsCount = used;
            }
//            printLogMsg("Used hosts: "+ used);
        } else { //若是多对多的映射
            super.processEvent(ev);
            int used = getHostList().size() - getUnusedHostsCount();
            if (used > maximumUsedHostsCount) {
                maximumUsedHostsCount = used;
            }
//            printLogMsg("Used hosts: "+ used);
        }
    }
    
    
    public static  TreeMap<Integer, Vector<Integer>> getMydeployMap() {
		return mydeployMap;
	}
	
    @SuppressWarnings("static-access")
	public void setMydeployMap(TreeMap<Integer, Vector<Integer>> mydeployMap) {
		this.mydeployMap = mydeployMap;
	}
    
	
	public static double getLoadDegree() {
		return loadDegree;
	}
	@SuppressWarnings("static-access")
	public void setLoadDegree(double loadDegree) {
		this.loadDegree = loadDegree;
	}
	
	
	public static VirtualMachine[] getOrigin_vms() {
		return origin_vms;
	}

	public static void setOrigin_vms(VirtualMachine[] origin_vms) {
		ExtendedDatacenter.origin_vms = origin_vms;
	}

	int getUnusedHostsCount() {
        int count = 0;
        List<ExtendedHost> hostList= getHostList();
        for (ExtendedHost host: hostList) {
            if(!host.isUsed()) {
                count++;
            }
        }
        return count;
    }
}
