package org.cloudbus.cloudsim.provisioners;


import org.cloudbus.cloudsim.Vm;

public class BwProvisionerImpl extends BwProvisioner{

	public BwProvisionerImpl(long bw) {
		super(bw);
	}

	@Override
	public boolean allocateBwForVm(Vm vm, long bw) {
		if(isSuitableForVm(vm, bw)){
			setAvailableBw(getAvailableBw()-bw);
			//vm.setCurrentAllocatedBw(bw);
			return true;
		}
		return false;
	}

	@Override
	public long getAllocatedBwForVm(Vm vm) {
		return vm.getCurrentAllocatedBw();
	}

	@Override
	public void deallocateBwForVm(Vm vm) {
		setAvailableBw(getAvailableBw()+vm.getBw());
		//vm.setCurrentAllocatedBw(0);
	}

	@Override
	public boolean isSuitableForVm(Vm vm, long bw) {
		return getAvailableBw()>=bw;
	}

}
