package org.cloudbus.cloudsim.provisioners;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Vm;

public class PeProvisionerImpl extends PeProvisioner {

	public PeProvisionerImpl(double mips) {
		super(mips);
	}

	@Override
	public boolean allocateMipsForVm(Vm vm, double mips) {
		if(getAvailableMips()>=mips){
			setAvailableMips(getAvailableMips()-mips);
			List<Double> allocatedMips = new ArrayList<Double>();

			allocatedMips.add(mips);
			vm.setCurrentAllocatedMips(allocatedMips);
			return true;
		}
		return false;
	}

	@Override
	public boolean allocateMipsForVm(String vmUid, double mips) {
		return false;
	}

	@Override
	public boolean allocateMipsForVm(Vm vm, List<Double> mips) {
		int totalMipsToAllocate = 0;
		for (double _mips : mips) {
			totalMipsToAllocate += _mips;
		}
		return allocateMipsForVm(vm,totalMipsToAllocate);
	}

	@Override
	public List<Double> getAllocatedMipsForVm(Vm vm) {
		return vm.getCurrentAllocatedMips();
	}

	@Override
	public double getTotalAllocatedMipsForVm(Vm vm) {
		return vm.getCurrentRequestedTotalMips();
	}

	@Override
	public double getAllocatedMipsForVmByVirtualPeId(Vm vm, int peId) {
		return 0;
	}

	@Override
	public void deallocateMipsForVm(Vm vm) {
		setAvailableMips(getAvailableMips()+vm.getCurrentRequestedTotalMips());
		vm.setCurrentAllocatedMips(null);
	}

}
