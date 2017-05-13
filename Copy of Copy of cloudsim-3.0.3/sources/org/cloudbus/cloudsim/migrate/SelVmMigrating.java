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
	private List<Host> hotList;
	private List<Host> unHotList;
	private double lossRatio;
	private Map<Vm, Double> lossToVm;
	private Map<Vm, ArrayList<Double>> loadToVm;
	private static Map<Vm, Host> solution;
	private double bestDistance;
	private Host bestHost;

	public SelVmMigrating(Map<Vm, ArrayList<Double>> triUtilToVm) {
		this.hotList = prediction.getHotList();
		lossToVm = new HashMap<Vm, Double>();
		loadToVm = triUtilToVm;
		bestDistance = Double.MAX_VALUE;
		solution=new HashMap<Vm,Host>();
		selectVms();
	}

	private void selectVms() {
		for (Host host : hotList) {
			double[] load=new double[3];
			for (Vm vm : host.getVmList()) {
				load[0] = loadToVm.get(vm).get(0)*vm.getMips()*vm.getNumberOfPes()/host.getTotalMips();
				load[1] = loadToVm.get(vm).get(1)*vm.getBw()/host.getBw();
				load[2] = loadToVm.get(vm).get(2)*vm.getRam()/host.getRam();
				lossRatio = load[0] * load[1] / load[2];// 开销比例
				lossToVm.put(vm, lossRatio);
			}
			List<Map.Entry<Vm, Double>> tempList = new ArrayList<Map.Entry<Vm, Double>>(
					lossToVm.entrySet());
			Collections.sort(tempList, new Comparator<Map.Entry<Vm, Double>>() {
				public int compare(Entry<Vm, Double> o1, Entry<Vm, Double> o2) {
					return o1.getValue() > o2.getValue() ? -1 : 1;
				}
			});
			Vm vmMigrating = tempList.get(0).getKey();// 选择开销比例最大的进行迁移
			bestHost = selectHostMigrate(vmMigrating);
			bestHost.addMigratingInVm(vmMigrating);
			bestHost.vmCreate(vmMigrating);
			bestHost.getVmList().add(vmMigrating);
			solution.put(vmMigrating, bestHost);
			vmMigrating.setCurrentAllocatedSize(vmMigrating.getSize());
			List<Double> list=new ArrayList<Double>();
			list.add(vmMigrating.getCurrentRequestedTotalMips());
			vmMigrating.setCurrentAllocatedMips(list);
		}
	}

	private Host selectHostMigrate(Vm vm) {
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
		SelVmMigrating.solution=solution;
	}
}



