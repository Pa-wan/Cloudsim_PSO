/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */
package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.policy.ListAllocationPolicy;
import org.cloudbus.cloudsim.policy.utils.ExtendedHelper;

/**
 * VM Allocation Policy - BW and Compute combined, MFF.
 * When select a host to create a new VM, this policy chooses 
 * the most full host in terms of both compute power and network bandwidth.   
 * 选择资源剩余量最少的物理机作为虚拟机的目标主机
 * @author Jungmin Son
 * @since CloudSimSDN 1.0
 */
public class VmAllocationPolicyCombinedMostFullFirst extends VmAllocationPolicy implements PowerUtilizationMaxHostInterface,ListAllocationPolicy {

	protected final double hostTotalMips;
	protected final double hostTotalRam;
	protected final double hostTotalBw;
	protected final double hostTotalDisk;
	
	protected final int hostTotalPes;
	
	/** The vm table. */
	private Map<String, Host> vmTable;

	private Map<String, Integer> usedPes;//The used pes
	private List<Integer> freePes; //The free pes
	
	private Map<String, Long> usedMips; 
	private List<Long> freeMips;
	private Map<String, Long> usedRam;
	private List<Long> freeRam;
	private Map<String, Long> usedBw;
	private List<Long> freeBw;
	private Map<String, Long> usedDisk;
	private List<Long> freeDisk;

	private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
	
	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @pre $none
	 * @post $none
	 */
	public VmAllocationPolicyCombinedMostFullFirst(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		setFreeMips(new ArrayList<Long>());
		setFreeRam(new ArrayList<Long>());
		setFreeBw(new ArrayList<Long>());
		setFreeDisk(new ArrayList<Long>());
		
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());
			getFreeMips().add((long)host.getTotalMips());
			getFreeRam().add((long)host.getRamProvisioner().getAvailableRam());
			getFreeBw().add((long)host.getBwProvisioner().getAvailableBw());
			getFreeDisk().add((long)host.getStorage());
		}
		
		//初始化而已
		hostTotalPes =  getHostList().get(0).getNumberOfPes();
		hostTotalMips = getHostList().get(0).getTotalMips();
		hostTotalBw =  getHostList().get(0).getBw();
		hostTotalRam =  getHostList().get(0).getRam();
		hostTotalDisk=getHostList().get(0).getStorage();

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
		setUsedMips(new HashMap<String, Long>());
		setUsedRam(new HashMap<String, Long>());
		setUsedBw(new HashMap<String, Long>());
		setUsedDisk(new HashMap<String, Long>());
	}
	
	   private void printLogMsg(String msg) {
	        Log.print("CombMFF_Allocator: " + msg + LINE_SEPARATOR);
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
	
	protected double convertWeightedMetric(
			double mipsPercent, double ramPercent,
			double bwPercent,double diskPercent) {
		double ret = mipsPercent * ramPercent*bwPercent*diskPercent;
		return ret;
	}
	/**
	 * Allocates a host for a given VM.
	 * 
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	@Override
	public boolean allocateHostForVm(Vm vm) {
		if (getVmTable().containsKey(vm.getUid())) { // if this vm was not created
			return false;
		}
		
		int numHosts = getHostList().size();

		// 1. Find/Order the best host for this VM by comparing a metric
		int requiredPes = vm.getNumberOfPes();
		double requiredMips = vm.getCurrentRequestedTotalMips();
		int requiredRam=vm.getCurrentRequestedRam();
		long requiredBw = vm.getCurrentRequestedBw();
		long requiredDisk = vm.getSize();

		boolean result = false;
		
		double[] freeResources = new double[numHosts];
		for (int i = 0; i < numHosts; i++) {
			double mipsFreePercent = (double)getFreeMips().get(i) / this.hostTotalMips; 
			double ramFreePercent = (double)getFreeMips().get(i) / this.hostTotalRam; 
			double bwFreePercent = (double)getFreeBw().get(i) / this.hostTotalBw;
			double diskFreePercent = (double)getFreeBw().get(i) / this.hostTotalDisk;
			
			freeResources[i] = this.convertWeightedMetric(mipsFreePercent,ramFreePercent, bwFreePercent,diskFreePercent);
		}

		for(int tries = 0; result == false && tries < numHosts; tries++) {// we still trying until we find a host or until we try all of them
			double lessFree = Double.POSITIVE_INFINITY;  //正无穷大
			int idx = -1;

			// we want the host with less pes in use
			for (int i = 0; i < numHosts; i++) {
				if (freeResources[i] < lessFree) {
					lessFree = freeResources[i];
					idx = i;
				}
			}
			freeResources[idx] = Double.POSITIVE_INFINITY;
			Host host = getHostList().get(idx);
			

			// Check whether the host can hold this VM or not.
			if( getFreeMips().get(idx) < requiredMips) {
				//System.err.println("not enough MIPS");
				//Cannot host the VM
				continue;
			}
			
			if( getFreeRam().get(idx) < requiredRam) {
				//System.err.println("not enough BW");
				//Cannot host the VM
				continue;
			}
			
			if( getFreeBw().get(idx) < requiredBw) {
				//System.err.println("not enough BW");
				//Cannot host the VM
				continue;
			}

			
			if( getFreeDisk().get(idx) < requiredDisk) {
				//System.err.println("not enough BW");
				//Cannot host the VM
				continue;
			}
			
			result = host.vmCreate(vm);

			if (result) { // if vm were succesfully created in the host
				printLogMsg("Vm created successfuly");
				
				getVmTable().put(vm.getUid(), host);
				getUsedPes().put(vm.getUid(), requiredPes);
				getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
				
				getUsedMips().put(vm.getUid(), (long) requiredMips);
				getFreeMips().set(idx,  (long) (getFreeMips().get(idx) - requiredMips));

				getUsedRam().put(vm.getUid(), (long) requiredRam);
				getFreeRam().set(idx,  (long) (getFreeRam().get(idx) - requiredRam));
				
				getUsedBw().put(vm.getUid(), (long) requiredBw);
				getFreeBw().set(idx,  (long) (getFreeBw().get(idx) - requiredBw));

				getUsedDisk().put(vm.getUid(), (long) requiredDisk);
				getFreeDisk().set(idx,  (long) (getFreeDisk().get(idx) - requiredDisk));
				
                //放入物理机虚拟机映射
                Vector<Integer>value=deployMap.get(host.getId());
                if(value==null)
                	value=new Vector<Integer>();
            	value.add(vm.getId());
            	deployMap.put(host.getId(), value);
            	
				break;
			}
		}
		
		if(!result) {
			System.err.println("VmAllocationPolicy: WARNING:: Cannot create VM!!!!");
		}
		logMaxNumHostsUsed();
		return result;
	}
	
	protected int maxNumHostsUsed=0;
	public void logMaxNumHostsUsed() {
		// Get how many are used
		int numHostsUsed=0;
		for(int freePes:getFreePes()) {
			if(freePes < hostTotalPes) {
				numHostsUsed++;
			}
		}
		if(maxNumHostsUsed < numHostsUsed)
			maxNumHostsUsed = numHostsUsed;
		Log.printLine("Number of online hosts:"+numHostsUsed + ", max was ="+maxNumHostsUsed);
	}
	
	public int getMaxNumHostsUsed() { return maxNumHostsUsed;}

	/**
	 * Releases the host used by a VM.
	 * 
	 * @param vm the vm
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		if (host != null) {
			int idx = getHostList().indexOf(host);
			host.vmDestroy(vm);
			
			Integer pes = getUsedPes().remove(vm.getUid());
			getFreePes().set(idx, getFreePes().get(idx) + pes);
			
			Long mips = getUsedMips().remove(vm.getUid());
			getFreeMips().set(idx, getFreeMips().get(idx) + mips);
			
			Long ram = getUsedRam().remove(vm.getUid());
			getFreeRam().set(idx, getFreeRam().get(idx) + ram);
			
			Long bw = getUsedBw().remove(vm.getUid());
			getFreeBw().set(idx, getFreeBw().get(idx) + bw);
			
			Long disk = getUsedDisk().remove(vm.getUid());
			getFreeMips().set(idx, getFreeDisk().get(idx) + disk);
		}
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vm the vm
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vmId the vm id
	 * @param userId the user id
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
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
	 * Gets the used pes.
	 * 
	 * @return the used pes
	 */
	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	/**
	 * Sets the used pes.
	 * 
	 * @param usedPes the used pes
	 */
	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	/**
	 * Gets the free pes.
	 * 
	 * @return the free pes
	 */
	protected List<Integer> getFreePes() {
		return freePes;
	}

	/**
	 * Sets the free pes.
	 * 
	 * @param freePes the new free pes
	 */
	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}

	protected Map<String, Long> getUsedMips() {
		return usedMips;
	}
	protected void setUsedMips(Map<String, Long> usedMips) {
		this.usedMips = usedMips;
	}
	
	public Map<String, Long> getUsedRam() {
		return usedRam;
	}

	public void setUsedRam(Map<String, Long> usedRam) {
		this.usedRam = usedRam;
	}

	protected Map<String, Long> getUsedBw() {
		return usedBw;
	}
	protected void setUsedBw(Map<String, Long> usedBw) {
		this.usedBw = usedBw;
	}
	
	public Map<String, Long> getUsedDisk() {
		return usedDisk;
	}

	public void setUsedDisk(Map<String, Long> usedDisk) {
		this.usedDisk = usedDisk;
	}

	protected List<Long> getFreeMips() {
		return this.freeMips;
	}
	protected void setFreeMips(List<Long> freeMips) {
		this.freeMips = freeMips;
	}
	
	public List<Long> getFreeRam() {
		return freeRam;
	}

	public void setFreeRam(List<Long> freeRam) {
		this.freeRam = freeRam;
	}

	protected List<Long> getFreeBw() {
		return this.freeBw;
	}
	protected void setFreeBw(List<Long> freeBw) {
		this.freeBw = freeBw;
	}

	public List<Long> getFreeDisk() {
		return freeDisk;
	}

	public void setFreeDisk(List<Long> freeDisk) {
		this.freeDisk = freeDisk;
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.VmAllocationPolicy#optimizeAllocation(double, cloudsim.VmList, double)
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
	 * org.cloudbus.cloudsim.Host)
	 */
	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();
			int idx = getHostList().indexOf(host);
			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			Log.formatLine(
					"%.2f: VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),
					CloudSim.clock());
			return true;
		}

		return false;
	}
	
}

