package org.cloudbus.cloudsim.policy;

import org.cloudbus.cloudsim.Vm;

import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * 为一组虚拟机选择目标物理机
 */
public interface ListAllocationPolicy {
	//多对多形式的，保持主键的有序性
	TreeMap<Integer, Vector<Integer>>deployMap=new  TreeMap<Integer, Vector<Integer>>();
    public boolean allocateHostForVmList(List<Vm> vmsToAllocate);
    
    
}
