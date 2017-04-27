package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public interface VmAllocationPolicyFactory {
	public VmAllocationPolicy create(List<? extends Host> list);
}
