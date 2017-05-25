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
	private static List<Host> hotList;
	private static List<Host> unHotList;
	private static double lossRatio;
	private static Map<Vm, Double> lossToVm;
	private static Map<Vm, ArrayList<Double>> loadToVm;
	private static Map<Integer, List<Integer>> solution;
	private static double bestDistance;
	private static Host bestHost;

	public static void selectVms(Map<Vm, ArrayList<Double>> triUtilToVm) {
		hotList = Prediction.getHotList();
		lossToVm = new HashMap<Vm, Double>();
		loadToVm = triUtilToVm;
		bestDistance = Double.MAX_VALUE;
		solution=new HashMap<Integer, List<Integer>>();
		for (Host host : hotList) {
			double[] load=new double[3];
			lossToVm.clear();
			for (Vm vm : host.getVmList()) {
				load[0] = loadToVm.get(vm).get(0)*vm.getMips()*vm.getNumberOfPes()/host.getTotalMips();
				load[1] = loadToVm.get(vm).get(1)*vm.getBw()/host.getBw();
				load[2] = loadToVm.get(vm).get(2)*vm.getRam()/host.getRam();
				lossRatio = load[0] * load[1] / load[2];// 迁移开销比
				lossToVm.put(vm, lossRatio);
			}
			bestHost=null;
			List<Map.Entry<Vm, Double>> tempList = new ArrayList<Map.Entry<Vm, Double>>(
					lossToVm.entrySet());
			Collections.sort(tempList, new Comparator<Map.Entry<Vm, Double>>() {
				public int compare(Entry<Vm, Double> o1, Entry<Vm, Double> o2) {
					return o1.getValue() > o2.getValue() ? -1 : 1;
				}
			});
			Vm vmMigrating = tempList.get(0).getKey();// 选择开销比例最大的进行迁移
			List<Integer> temp=new ArrayList<>();
			temp.add(host.getId());
			bestHost = selectHostMigrate(vmMigrating);			
			if (bestHost == null) {
				
			} else {
				FileUtil.writeMethod2(vmMigrating.getId()+"\t"+host.getId()+"\t"+bestHost.getId());
				temp.add(bestHost.getId());
				bestHost.addMigratingInVm(vmMigrating);
				bestHost.vmCreate(vmMigrating);
				bestHost.getVmList().add(vmMigrating);
				solution.put(vmMigrating.getId(), temp);
				vmMigrating.setCurrentAllocatedSize(vmMigrating.getSize());
				List<Double> list = new ArrayList<Double>();
				list.add(vmMigrating.getCurrentRequestedTotalMips());
				vmMigrating.setCurrentAllocatedMips(list);
				host.vmDestroy(vmMigrating);
			}
			
		}
	}

	private static Host selectHostMigrate(Vm vm) {
		unHotList = Prediction.getunHotList();
		double tempdistance = Double.MAX_VALUE;
		bestDistance=Double.MAX_VALUE;
		Host selHost=null;
		for (Host host : unHotList) {
			if (Utils.isSuitable(vm, host)) {		
				tempdistance = Math.sqrt(Math.pow((host.getRamProvisioner()
						.getAvailableRam() - vm.getRam()) / host.getRam(), 2)
						+ Math.pow(
								(host.getBwProvisioner().getAvailableBw() - vm
										.getBw()) / host.getBw(), 2)
						+ Math.pow((host.getAvailableMips() - vm.getMips()*vm.getNumberOfPes())
								/ host.getTotalMips(), 2));
			}
			if (tempdistance < bestDistance) {
//				if(迁移后主机不超载){
					bestDistance = tempdistance;
					selHost = host;
//				}
			}
		}
		return selHost;
	}
	
	public static Map<Integer, List<Integer>> getSolution(){
		return solution;
	}
	
	public void setSolution(Map<Integer, List<Integer>> solution){
		SelVmMigrating.solution=solution;
	}
}



