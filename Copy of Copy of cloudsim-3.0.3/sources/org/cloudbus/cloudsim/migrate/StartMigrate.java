package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class StartMigrate implements Runnable {
	private List<Host> hostList;
	private List<double[][]> triLoadInHost; // 主机预测前三个周期的利用率
	private double[][] triUtilToHost; // 主机三个指标的利用率矩阵
	private static Map<Vm, ArrayList<Double>> triUtilToVm;

	// private Map<Vm, ArrayList<Double>>
	public StartMigrate(List<Host> hostlist) {
		super();
		this.hostList = hostlist;
		triLoadInHost = new ArrayList<double[][]>();
		triUtilToVm = new HashMap<Vm, ArrayList<Double>>();
		triUtilToHost = new double[hostList.size()][3];
		for (int i = 0; i < 2; i++) {
			HostDynamicLoad();
			triLoadInHost.add(getUtilToHost());
		}
	}

	public void run() {

		while (true) {
			HostDynamicLoad();
			triLoadInHost.add(getUtilToHost());
			Prediction.predict(hostList, triLoadInHost);
			SelVmMigrating.selectVms(triUtilToVm);
			try {
				Thread.sleep(3000);
				triLoadInHost.remove(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void HostDynamicLoad() {
		Random rnd = new Random();
		for (Host host : hostList) {
			ArrayList<Double> temp1 = new ArrayList<Double>();
			new ArrayList<Double>();
			for (Vm vm : host.getVmList()) {
				double[] utilVm = new double[3];
				utilVm[0] = (double) (rnd.nextInt(50) + 50) / 100;			
				temp1.add(utilVm[0]);
				utilVm[1] = (double) (rnd.nextInt(50) + 50) / 100;
				temp1.add(utilVm[1]);
				utilVm[2] = (double) (rnd.nextInt(50) + 50) / 100;
				temp1.add(utilVm[2]);
				triUtilToVm.put(vm, temp1);
				triUtilToHost[host.getId()][0] += utilVm[0]
						* vm.getNumberOfPes() * vm.getMips()
						/ host.getTotalMips();
				triUtilToHost[host.getId()][1] += utilVm[1] * vm.getBw()
						/ host.getBw();
				triUtilToHost[host.getId()][2] += utilVm[2] * vm.getRam()
						/ host.getRam();
			}
		}
	}

	public double[][] getUtilToHost() {
		return triUtilToHost;
	}
	
	public static Map<Vm, ArrayList<Double>> getTriUtilToVm(){
		return triUtilToVm;
	}

}
