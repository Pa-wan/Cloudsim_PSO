package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class StartMigrate extends Thread{
	private List<Host> hostList;
	private List<Map<Host, ArrayList<Double>>> triLoadInHost; //主机预测前三个周期的利用率
	private Map<Host, ArrayList<Double>> triUtilToHost; //主机三个指标的利用率
	private Map<Vm, ArrayList<Double>> triUtilToVm;
//	private Map<Vm, ArrayList<Double>> 
	public StartMigrate(List<Host> hostlist){
		this.hostList=hostlist;
		triUtilToHost=new HashMap<Host, ArrayList<Double>>();
		for(int i=0;i<3;i++){
			HostDynamicLoad();
			triLoadInHost.add(getUtilToHost());
		}
	}
	
	public void run() {
		
		while (true) {
			HostDynamicLoad();
			prediction.predict(triLoadInHost);
			SelVmMigrating selVmMigrating = new SelVmMigrating();
			try { 
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void HostDynamicLoad() {
		for(Host  host:hostList){
			double[] utilHost=new double[3];
			ArrayList<Double> temp1=new ArrayList<Double>();
			ArrayList<Double> temp2=new ArrayList<Double>();
			for(Vm vm:host.getVmList()){
				double[] utilVm=new double[3];
				 utilVm[0]=Math.random();
				 utilVm[1]=Math.random();
				 utilVm[2]=Math.random();
				temp1.add(utilVm[0]);
				temp1.add(utilVm[1]);
				temp1.add(utilVm[2]);
				triUtilToVm.put(vm, temp1);
				utilHost[0]+=utilVm[0]*vm.getNumberOfPes()*vm.getMips()/host.getTotalMips();
				utilHost[1]+=utilVm[1]*vm.getBw()/host.getBw();
				utilHost[2]+=utilVm[2]*vm.getRam()/host.getRam();
			}
			triUtilToHost.put(host, temp2);
		}
	}
	
	public Map<Host, ArrayList<Double>> getUtilToHost(){
		return triUtilToHost;
	}

}

