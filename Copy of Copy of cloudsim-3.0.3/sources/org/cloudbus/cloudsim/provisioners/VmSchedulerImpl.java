package org.cloudbus.cloudsim.provisioners;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;

public class VmSchedulerImpl extends VmScheduler{

	public VmSchedulerImpl(List<? extends Pe> pelist) {
		super(pelist);
	}

	@Override
	public boolean allocatePesForVm(Vm vm, List<Double> mipsShare) {
		double total=0;
		for(double mips:mipsShare){
			total+=mips;
		}
		if(getAvailableMips()>=total){
			setAvailableMips(getAvailableMips()-total);
//			List<Double> list=new ArrayList<Double>();
//			list.add(total);
//			vm.setCurrentAllocatedMips(list);
			return true;
		}
		return false;
	}

	@Override
	public void deallocatePesForVm(Vm vm) {
		setAvailableMips(getAvailableMips()+vm.getNumberOfPes()*vm.getMips());
		//vm.setCurrentAllocatedMips(null);
	}
	

}
