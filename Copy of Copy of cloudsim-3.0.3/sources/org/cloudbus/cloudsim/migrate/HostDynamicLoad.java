package org.cloudbus.cloudsim.migrate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class HostDynamicLoad {
	private List<Host> hostList;
	private Map<Integer, Integer> solution;
	public HostDynamicLoad(List<Host> hostlist){
		this.hostList=hostlist;
	}
	
	public void run(){
		prediction.predict(hostList);
		SelVmMigrating selVmMigrating=new SelVmMigrating();
		Map<Vm,Host> solu=selVmMigrating.getSolution();
		Iterator<Entry<Vm, Host>> iterator=solu.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Vm, Host> entry=iterator.next();
			solution.put(entry.getKey().getId(), entry.getValue().getId());
		}
	}
	
	public Map<Integer, Integer> getSolution(){
		return solution;
	}
	
	public void setSolution(Map<Integer, Integer> solution){
		this.solution=solution;
	}
}
