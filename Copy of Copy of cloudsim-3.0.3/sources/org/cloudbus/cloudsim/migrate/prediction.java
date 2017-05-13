package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Host;

public class prediction {
	private static List<Host> hostList;
	private static List<Host> hotList;
	private static List<Host> unHotList;
	private static double a = 0.6;
	private static List<double[][]> loadToHost;// 计算得到的5次预测负载

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
				for (int k = 0; k < 3; k++) {
					temp[j][k] = calcuPreLoad(hostList.get(j), Eload[j][k]);
				}
			}
			loadToHost.add(temp);
		}
		int[] cnt = new int[size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 5; j++) {
				if (loadToHost.get(j)[i][0] < 0.5
						&& loadToHost.get(j)[i][1] < 0.5
						&& loadToHost.get(j)[i][2] < 0.5) {
				} else {
					cnt[i]++;
				}
			}
			if (cnt[i] >= 4) {
				hotList.add(hostList.get(i));
			} else {
				unHotList.add(hostList.get(i));
			}
		}
	}

	private static double calcuPreLoad(Host host, double lastCalLoad) {
		double calcuLoad = 0;
		double Uload = host.getLoad();
		calcuLoad = lastCalLoad + a * (Uload - lastCalLoad);
		return calcuLoad;
	}

	public static List<Host> getHotList() {
		return hotList;
	}

	public void setHotList(List<Host> hotlist) {
		prediction.hotList = hotlist;
	}

	public static List<Host> getunHotList() {
		return unHotList;
	}

	public void setunHotList(List<Host> unHotlist) {
		prediction.unHotList = unHotlist;
	}
}
