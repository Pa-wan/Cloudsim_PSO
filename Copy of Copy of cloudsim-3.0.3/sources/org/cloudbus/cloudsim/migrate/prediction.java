package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;

import com.sun.org.apache.bcel.internal.generic.AALOAD;

public class prediction {
private static List<Host> hotList;
	private static List<Host> unHotList;
	private static double a = 0.6;
	private static List<Map<Host, ArrayList<Double>>> loadToHost;// 计算得到的预测负载

	public static void predict(List<Map<Host, ArrayList<Double>>> triLoadToHost) {
		hotList = new ArrayList<Host>();
		unHotList=new ArrayList<Host>();
		double[] Eload=new double[3];
			Iterator<Entry<Host, ArrayList<Double>>> it1 = triLoadToHost.get(0).entrySet().iterator();
			Iterator<Entry<Host, ArrayList<Double>>> it2 = triLoadToHost.get(1).entrySet().iterator();
			Iterator<Entry<Host, ArrayList<Double>>> it3 = triLoadToHost.get(2).entrySet().iterator();
			while(it.hasNext()){
				Entry<Host, ArrayList<Double>> entry = it.next();
				Eload[0]+=entry.getValue().get(0);
				Eload[1]+=entry.getValue().get(1);
				Eload[2]+=entry.getValue().get(2);
			}
		
		while (it.hasNext()){    
			
			ArrayList<Double> tempLoad = new ArrayList<Double>();
			double Eload=0;
			for(double  load:entry.getValue()){
				 Eload +=load; 
			}
			Eload/=3;//预测负载初始值，设定为预测前三周期负载的平均值
			for (int i = 0; i < 5; i++) {
				double temp = calcuPreLoad(entry.getKey(), Eload);
				tempLoad.add(temp);
				Eload = temp;
			}
			loadToHost.put(entry.getKey(), tempLoad);
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
			else {
				unHotList.add(entry.getKey());
			}
		}
	}

	private static double calcuPreLoad(Host host, double lastCalLoad) {
		double calcuLoad = 0;
		double Uload = host.getLoad();
		calcuLoad = lastCalLoad + a * (Uload - lastCalLoad);
		return calcuLoad;
	}
	
	public static List<Host> getHotList(){
		return hotList;
	}

	public void setHotList(List<Host> hotlist) {
		this.hotList = hotlist;
	}
	
	public static List<Host> getunHotList(){
		return unHotList;
	}
	
	public void setunHotList(List<Host> unHotlist){
		this.unHotList=unHotlist;
	}
}
