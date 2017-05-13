package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.hust.pso2.Utils;

public class SelVmMigrating {
	private List<Vm> vmList;
	private List<Host> hotList;
	private List<Host> unHotList;
	private double lossRatio;
	private Map<Vm, Double> lossToVm;
	private Map<Vm, ArrayList<Double>> loadToVm;
	private static Map<Vm, Host> solution;
	private double bestDistance;
	private Host bestHost;

	public SelVmMigrating() {
		this.hotList = prediction.getHotList();
		lossToVm = new HashMap<Vm, Double>();
		loadToVm = new HashMap<Vm, ArrayList<Double>>();
		bestDistance = Double.MAX_VALUE;
		solution=new HashMap<Vm,Host>();
		selectVms();
	}

	private void selectVms() {
		for (Host host : hotList) {
			ArrayList<Double> load = new ArrayList<>();
			for (Vm vm : host.getVmList()) {
				double vmCPU = vm.getCurrentRequestedTotalMips();
				double vmBw = vm.getCurrentAllocatedBw() / vm.getBw();
				double vmRam = vm.getCurrentAllocatedRam() / vm.getRam();
				load.add(vmCPU);
				load.add(vmBw);
				load.add(vmRam);
				lossRatio = vmCPU * vmBw / vmRam;// 开销比例
				lossToVm.put(vm, lossRatio);
				loadToVm.put(vm, load);
			}
			List<Map.Entry<Vm, Double>> tempList = new ArrayList<Map.Entry<Vm, Double>>(
					lossToVm.entrySet());
			Collections.sort(tempList, new Comparator<Map.Entry<Vm, Double>>() {
				public int compare(Entry<Vm, Double> o1, Entry<Vm, Double> o2) {
					return o1.getValue() > o2.getValue() ? 1 : -1;
				}
			});
			Vm vmMigrating = tempList.get(0).getKey();// 选择开销比例最小的进行迁移
			bestHost = selectHostMigratein(vmMigrating);
			bestHost.addMigratingInVm(vmMigrating);
			bestHost.vmCreate(vmMigrating);
			solution.put(vmMigrating, bestHost);
		}
	}

	private Host selectHostMigratein(Vm vm) {
		unHotList = prediction.getunHotList();
		double tempdistance = Double.MAX_VALUE;
		for (Host host : unHotList) {
			if (Utils.isSuitable(vm, host)) {
				tempdistance = Math.sqrt(Math.pow((host.getRamProvisioner()
						.getAvailableRam() - vm.getRam()) / host.getRam(), 2)
						+ Math.pow(
								(host.getBwProvisioner().getAvailableBw() - vm
										.getBw()) / host.getBw(), 2)
						+ Math.pow((host.getAvailableMips() - vm.getMips()
								/ host.getTotalMips()), 2));
			}
			if (tempdistance < bestDistance) {
//				if(迁移后主机不超载){
					bestDistance = tempdistance;
					bestHost = host;
//				}
			}
		}
		return bestHost;
	}
	
	public static Map<Vm,Host> getSolution(){
		return solution;
	}
	
	public void setSolution(Map<Vm, Host> solution){
		this.solution=solution;
	}
}



