package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Host;

public class Prediction {
	private static List<Host> hostList;
	private static List<Host> hotList;
	private static List<Host> unHotList;
	private static double a = 0.6;
	private static List<double[][]> loadToHost;// 计算得到的5次预测负载
	private static double Qmax =0.8;

	public static void predict(List<Host> hostlist,
			List<double[][]> triLoadToHost) {
		hotList = new ArrayList<Host>();
		unHotList = new ArrayList<Host>();
		hostList = hostlist;
		int size = hostList.size();
		loadToHost=new ArrayList<double[][]>();
		double[][] Eload = new double[size][3];// 取预测前三个周期的负载平均值为预测初值
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 3; j++)
				Eload[i][j] = (triLoadToHost.get(0)[i][j]
						+ triLoadToHost.get(1)[i][j] + triLoadToHost.get(2)[i][j]) / 3;
		}
		double[][] temp = new double[size][3];
		for (int i = 0; i < 5; i++) {// 预测后5个周期的负载
			for (int j = 0; j < size; j++) {
				Host host = hostList.get(j);
				temp[j][0] = calcuPreLoad(
						(host.getTotalMips() - host.getAvailableMips())
								/ host.getTotalMips(), Eload[j][0]);
				temp[j][1] = calcuPreLoad(host.getRamProvisioner().getUsedRam()
						/ (host.getRam() + 0.0), Eload[j][1]);
				temp[j][2] = calcuPreLoad(host.getBwProvisioner().getUsedBw()
						/ (host.getBw() + 0.0), Eload[j][2]);
			}
			loadToHost.add(temp);
		}
		int[] cnt = new int[size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 5; j++) {
				if (loadToHost.get(j)[i][0] <Qmax
						&& loadToHost.get(j)[i][1] < Qmax
						&& loadToHost.get(j)[i][2] < Qmax) {
				} else {
					cnt[i]++;
				}
			}
			if (cnt[i] >= 4) {
				hotList.add(hostList.get(i));
				if(hotList.size()==0)
					Qmax=Qmax-0.1;
			} else {
				unHotList.add(hostList.get(i));
			}
		}
	}

	private static double calcuPreLoad(double Uload, double lastCalLoad) {
		double calcuLoad = 0;
		calcuLoad = lastCalLoad + a * (Uload - lastCalLoad);
		return calcuLoad;
	}

	public static List<Host> getHotList() {
		return hotList;
	}

	public void setHotList(List<Host> hotlist) {
		Prediction.hotList = hotlist;
	}

	public static List<Host> getunHotList() {
		return unHotList;
	}

	public void setunHotList(List<Host> unHotlist) {
		Prediction.unHotList = unHotlist;
	}
}
