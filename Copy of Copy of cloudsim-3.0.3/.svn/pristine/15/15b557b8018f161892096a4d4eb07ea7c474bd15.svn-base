package org.cloudbus.cloudsim.provisioners;


import org.cloudbus.cloudsim.Vm;

public class RamProvisionerImpl extends RamProvisioner {
	
	public RamProvisionerImpl(int ram) {
		super(ram);
	}
	
	/**
	 * ram表示vm的请求量，即vm.ram
	 */
	@Override
	public boolean allocateRamForVm(Vm vm, int ram) {
		if(isSuitableForVm(vm,ram) ){
			setAvailableRam(getAvailableRam()-ram);
			//vm.setCurrentAllocatedRam(ram);
			return true;
		}
		return false;
	}
	
	@Override
	public int getAllocatedRamForVm(Vm vm) {
		return vm.getCurrentAllocatedRam();
	}

	@Override
	public void deallocateRamForVm(Vm vm) {
		setAvailableRam(getAvailableRam() + vm.getRam());
		//vm.setCurrentAllocatedRam(0);		
	}

	@Override
	public boolean isSuitableForVm(Vm vm, int ram) {
		return getAvailableRam()>=ram;
	}

}
