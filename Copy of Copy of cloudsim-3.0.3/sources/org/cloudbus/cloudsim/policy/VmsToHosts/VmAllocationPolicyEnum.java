package org.cloudbus.cloudsim.policy.VmsToHosts;

public enum VmAllocationPolicyEnum {
	random,  firstfit, //组合类型 
	bestfit, roundrobin,  //只有mips类型
	ant,     pso,
	comblff, combmff, //
	
}
