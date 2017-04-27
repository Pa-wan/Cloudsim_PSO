/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * 与选择剩余资源最多的物理机一样
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class VmAllocationPolicyBalance extends VmAllocationPolicy {

	/** 保存虚拟机ID和物理机的映射 The vm table. */
	private Map<String, Host> vmTable;

	/** 保存虚拟机ID和分配PE数的映射 PE The used pes. */
	private Map<String, Integer> usedPes;

	/** 空闲的PE的PE个数 The free pes. */
	private List<Integer> freePes;

	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @pre $none
	 * @post $none
	 */
	public VmAllocationPolicyBalance(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		//将物理机列表中的剩余PE数，依次加入剩余PE列表
		for (Host host : getHostList()) {
			//host.getNumberOfPes()获取的是物理机的PE个数
			getFreePes().add(host.getNumberOfPes());

		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
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
        int requiredPes = vm.getNumberOfPes();
        int freeramh1=0;
        int freeramh2=0;
        int freepesh1=0;
        int freepesh2=0;
        boolean result = false;
        int tries = 0;
        List<Integer> freePesTmp = new ArrayList<Integer>();
        for (Integer freePes : getFreePes()) {
            freePesTmp.add(freePes);
        }

        List <Host> hostList =getHostList();


        if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
            do {// we still trying until we find a host or until we try all of them

                int idx = -1;

                // we want the host with less pes in use
                    Host h1=hostList.get(0);
                    int j=0;
                    idx=0;
                    for (int i = 1; i < freePesTmp.size(); i++) {
                        Host h2=hostList.get(i);
                        freeramh1=h1.getRamProvisioner().getAvailableRam();
                        freeramh2=h2.getRamProvisioner().getAvailableRam();
                        freepesh1=freePesTmp.get(j);
                        freepesh2=freePesTmp.get(i);
                       
                        double diffram=0.0,diffpes=0.0;
                        if(freeramh2!=0 || freeramh1!=0){
                            diffram= (1.0*(freeramh2-freeramh1)/(freeramh2+freeramh1));
                        }else
                            Log.printLine( " fault in ram " );
                        
                        
                        if(freepesh2!=0 || freepesh1!=0){
                            diffpes=(1.0*(freepesh1-freepesh2)/(freepesh1+freepesh2)) ;
                        }else
                            Log.printLine( " fault in pes ");

                       
                        if(diffram==diffpes || diffpes>diffram){
                            idx=j;  
                        }else{
                            h1=h2;
                            j=i;
                            idx=i;
                            break;
                        }
                    }

                Host host = getHostList().get(idx);
                result = host.vmCreate(vm);

                if (result) { // if vm were succesfully created in the host
                    Log.printLine( " vm " +  "created" + "host" + idx);
                    getVmTable().put(vm.getUid(), host);
                    getUsedPes().put(vm.getUid(), requiredPes);
                    getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
                    result = true;
                    break;
                } else {
                    freePesTmp.set(idx, Integer.MIN_VALUE);
                }
                tries++;
            } while (!result && tries < getFreePes().size());

        }

        return result;
    }

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
		int idx = getHostList().indexOf(host);
		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
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
