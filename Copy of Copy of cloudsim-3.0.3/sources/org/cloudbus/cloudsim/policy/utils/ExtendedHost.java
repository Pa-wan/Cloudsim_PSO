package org.cloudbus.cloudsim.policy.utils;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.List;


public class ExtendedHost extends PowerHostUtilizationHistory {
    private long initialStorage;
    private static final boolean isVmOwnsPes = false;
    public ExtendedHost(int id,
                        RamProvisioner ramProvisioner,
                        BwProvisioner bwProvisioner,
                        long storage,
                        List<? extends Pe> peList,
                        VmScheduler vmScheduler,
                        PowerModel powerModel) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler, powerModel);
        initialStorage = storage;
    }
    public boolean isUsed(){
        if(getRam() != getRamProvisioner().getAvailableRam() ||
                getInitialStorage() != getStorage() ||
                getNumberOfFreePes() != getNumberOfPes())
            return true;
        return false;
    }
    public long getInitialStorage(){
        return initialStorage;
    }

    @Override
    public boolean vmCreate(Vm vm) {
        if (getNumberOfFreePes() < vm.getNumberOfPes()) {
            Log.printLine("Host has less PE's than vm needed.");
            return false;
        }
        if (getStorage() < vm.getSize()) {
            Log.printLine("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
                    + " failed by storage");
            return false;
        }

        if (!getRamProvisioner().allocateRamForVm(vm, vm.getCurrentRequestedRam())) {
            Log.printLine("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
                    + " failed by RAM");
            return false;
        }

        if (!getBwProvisioner().allocateBwForVm(vm, vm.getCurrentRequestedBw())) {
            Log.printLine("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
                    + " failed by BW");
            getRamProvisioner().deallocateRamForVm(vm);
            return false;
        }

        if (!getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
            Log.printLine("[VmScheduler.vmCreate] Allocation of VM #" + vm.getId() + " to Host #" + getId()
                    + " failed by MIPS");
            getRamProvisioner().deallocateRamForVm(vm);
            getBwProvisioner().deallocateBwForVm(vm);
            return false;
        }
        if (isVmOwnsPes) {
            int pesToAllocate = vm.getNumberOfPes();
            for (int i = 0; (i < getNumberOfPes()) && (pesToAllocate > 0); i++) {
                if (getPeList().get(i).getStatus() == Pe.FREE) {
                    getPeList().get(i).setStatus(Pe.BUSY);
                    pesToAllocate--;
                }
            }
            if (pesToAllocate != 0) {
                throw new RuntimeException("Cannot allocate enough PEs");
            }
        }
        setStorage(getStorage() - vm.getSize());
        getVmList().add(vm);
        vm.setHost(this);
        return true;
    }

    @Override
    public void vmDestroy(Vm vm) {
        if (vm != null) {
            vmDeallocate(vm);
            getVmList().remove(vm);
            vm.setHost(null);
            if (isVmOwnsPes) {
                int pesToDeAllocate = vm.getNumberOfPes();
                for (int i = 0; (i < getNumberOfPes()) && (pesToDeAllocate > 0); i++) {
                    if (getPeList().get(i).getStatus() == Pe.BUSY) {
                        getPeList().get(i).setStatus(Pe.FREE);
                        pesToDeAllocate--;
                    }
                }
            }
        }
    }
    @Override
    public boolean isSuitableForVm(Vm vm) {
        //Smolyak; Vms don't share PEs between each other;
        return (getNumberOfFreePes() >= vm.getNumberOfPes()
                && getVmScheduler().getPeCapacity() >= vm.getCurrentRequestedMaxMips()
                && getVmScheduler().getAvailableMips() >= vm.getCurrentRequestedTotalMips()
                && getRamProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedRam())
                && getBwProvisioner().isSuitableForVm(vm, vm.getCurrentRequestedBw()));
    }
}
