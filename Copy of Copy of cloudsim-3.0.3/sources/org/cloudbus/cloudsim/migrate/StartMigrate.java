package org.cloudbus.cloudsim.migrate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.ui.GlobalObject;

public class StartMigrate implements Runnable {
	private List<Host> hostList;
	private CountDownLatch latch;
	private List<double[][]> triLoadInHost; // 主机预测前三个周期的利用率
	private double[][] triUtilToHost; // 主机三个指标的利用率矩阵
	private static Map<Vm, ArrayList<Double>> triUtilToVm;
	private DecimalFormat dft = new DecimalFormat("###.##");
	private int cond;
	private Lock lock;
	private Condition condition;
	private Semaphore A;
	private Semaphore B;

	// private Map<Vm, ArrayList<Double>>
	public StartMigrate(Semaphore a, Semaphore b, List<Host> hostlist) {
		super();
		this.hostList = hostlist;
		// this.cond = cond;
		// this.condition = condition;
		// this.lock = lock;
		this.A = a;
		this.B = b;
		triLoadInHost = new ArrayList<double[][]>();
		triUtilToVm = new HashMap<Vm, ArrayList<Double>>();
		triUtilToHost = new double[hostList.size()][3];
		for (int i = 0; i < 2; i++) {
			HostDynamicLoad();
			triLoadInHost.add(getUtilToHost());
		}
	}

	public void run() {
		int cnt = 0;
		String indent = "		";
		try {
			A.acquire();
			FileUtil.writeMethod1("主机编号" + indent + "CPU" + indent + "内存"
					+ indent + "带宽");
			while (true) {
				// System.out.println(cnt++);
				GlobalObject.getjTextField1().setText(String.valueOf(cnt++));
				HostDynamicLoad();
				for (Host host : hostList) {
					double uCPU = (host.getTotalMips() - host
							.getAvailableMips()) / host.getTotalMips();
					double uRam = host.getRamProvisioner().getUsedRam()
							/ (host.getRam() + 0.0);
					double uBw = host.getBwProvisioner().getUsedBw()
							/ (host.getBw() + 0.0);
					// System.out.println(host.getId()+"： "+uCPU+" "+uRam+" "+uBw);
					FileUtil.writeMethod2(host.getId() + indent
							+ dft.format(uCPU) + indent + dft.format(uRam)
							+ indent + dft.format(uBw));
				}
				FileUtil.writeMethod2("============vm migrating status============\n虚拟机编号\t源主机\t目的主机");
				triLoadInHost.add(getUtilToHost());
				Prediction.predict(hostList, triLoadInHost);
				SelVmMigrating.selectVms(triUtilToVm);
				for (Host host : hostList) {
					double uCPU = (host.getTotalMips() - host
							.getAvailableMips()) / host.getTotalMips();
					double uRam = host.getRamProvisioner().getUsedRam()
							/ (host.getRam() + 0.0);
					double uBw = host.getBwProvisioner().getUsedBw()
							/ (host.getBw() + 0.0);
					// System.out.println(host.getId()+"： "+uCPU+" "+uRam+" "+uBw);
					FileUtil.writeMethod2(host.getId() + indent
							+ dft.format(uCPU) + indent + dft.format(uRam)
							+ indent + dft.format(uBw));
				}
				B.release();
				Thread.sleep(3000);
				triLoadInHost.remove(0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static Map<Vm, ArrayList<Double>> getTriUtilToVm() {
		return triUtilToVm;
	}

}
