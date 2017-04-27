package org.cloudbus.cloudsim.policy.utils;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class ExtendedDatacenterBrocker extends PowerDatacenterBroker {
    public static final int VM_ALLOCATION_MODE_STANDART = 0;
    public static final int VM_ALLOCATION_MODE_LIST = 1;
    private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
    public static final int ALLOCATE_VM_LIST_TAG = 900;
    private int vmAllocationMode_;
    Logger logger;
    
    private void printLogMsg(String msg) {
    	Log.print("Extended_brocker: " + msg +LINE_SEPARATOR);
    }
    
    public ExtendedDatacenterBrocker(String name) throws Exception {
        super(name);
        vmAllocationMode_ = VM_ALLOCATION_MODE_STANDART;
    }
    public ExtendedDatacenterBrocker(String name, int vmAllocationMode) throws Exception {
        super(name);
        this.vmAllocationMode_ = vmAllocationMode;
    }

    @Override
    protected void createVmsInDatacenter(int datacenterId) {
        printLogMsg("Trying to allocate "+getVmList().size()+" vms  in datacenter "+datacenterId);
        if (vmAllocationMode_ == VM_ALLOCATION_MODE_LIST) { //多虚拟机映射多物理机
            List<Vm> vmsToAllocate = new ArrayList<Vm>();
            for (Vm vm: getVmList()){
                if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
                    vmsToAllocate.add(vm);
                }
            }
            if (vmsToAllocate.size() > 0) {
            	printLogMsg("sending msg to "+vmsToAllocate.size()+ " vms");
                sendNow(datacenterId, ALLOCATE_VM_LIST_TAG, vmsToAllocate);
                getDatacenterRequestedIdsList().add(datacenterId);
                setVmsRequested(vmsToAllocate.size());
                setVmsAcks(0);
            }
        } else {
            super.createVmsInDatacenter(datacenterId); //单虚拟机映射多物理机
        }
    }

}
