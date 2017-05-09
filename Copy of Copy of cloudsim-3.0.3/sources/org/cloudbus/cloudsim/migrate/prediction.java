package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class prediction {
	private static List<Host> hostList;
	private List<Vm> vmList;
	private static List<Host> hotList;
	private static double a = 0.6;
	private static Map<Host, ArrayList<Double>> loadToHost;// 计算得到的预测负载

	public static void main(String[] args) {
		hostList = new ArrayList<Host>();
		loadToHost = new HashMap<Host, ArrayList<Double>>();
		hotList = new ArrayList<Host>();
		for (Host host : hostList) {
			ArrayList<Double> tempLoad = new ArrayList<Double>();
			double ELoad = host.getLoad() + 0.1;
			for (int i = 0; i < 5; i++) {
				double temp = calcuPreLoad(host, ELoad);
				tempLoad.add(temp);
				ELoad = temp;
			}
			loadToHost.put(host, tempLoad);
		}
		Iterator<Entry<Host, ArrayList<Double>>> itor = loadToHost.entrySet()
				.iterator();
		while (itor.hasNext()) {
			Entry<Host, ArrayList<Double>> entry = itor.next();
			int cnt = 0;
			for (double i : entry.getValue()) {
				if (i >= 0.9)
					cnt++;
			}
			if (cnt >= 4)
				hotList.add(entry.getKey());
		}
	}

	private static double calcuPreLoad(Host host, double lastCalLoad) {
		double calcuLoad = 0;
		double Uload = host.getLoad();
		calcuLoad = lastCalLoad + a * (Uload - lastCalLoad);
		return calcuLoad;
	}
	
	public List<Host> getHotList(){
		return hotList;
	}

	public void setHotList(List<Host> hotList) {
		this.hotList = hotList;
	}
}
