package org.cloudbus.cloudsim.policy.utils;

import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class ExtDatacenter extends Datacenter{
    private static final String LINE_SEPARATOR = ExtHelper.getLineSeparator();
   
    private void printLogMsg(String msg) {
        Log.print("Extended_datacenter: " + msg + LINE_SEPARATOR);
    }
    
    @Override
    public void processEvent(SimEvent ev) {
        if (ev.getTag() == ExtDatacenterBrocker.ALLOCATE_VM_LIST_TAG) { //若是多虚拟机映射多物理机
            VmAllocationPolicy policy = getVmAllocationPolicy(); //多对多形式
            @SuppressWarnings("unchecked")
			List<Vm> vmList = (List<Vm>)ev.getData(); //获取虚拟机
          //  List<Host> pmList_origin=getHostList();  //获取物理机列机
            
            printLogMsg("Before allocate------Number of Vm is "+vmList.size()+" , Number Of Host is "+ getHostList().size());
           
            boolean result = (policy.optimizeAllocation(vmList)!=null);  //调用多虚拟机映射多物理机函数
            
            if (result) {
            	
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
        } else { //若是多对多的映射
            super.processEvent(ev);
        }
    }

	public ExtDatacenter(String name,DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,schedulingInterval);
	}

}
