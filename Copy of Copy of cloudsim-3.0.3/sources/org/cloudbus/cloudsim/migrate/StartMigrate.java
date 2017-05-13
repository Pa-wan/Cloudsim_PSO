package org.cloudbus.cloudsim.migrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class StartMigrate implements Runnable{
	private List<Host> hostList;
	private List<double[][]> triLoadInHost; //主机预测前三个周期的利用率
	private double[][] triUtilToHost; //主机三个指标的利用率矩阵
	private Map<Vm, ArrayList<Double>> triUtilToVm;
//	private Map<Vm, ArrayList<Double>> 
	public StartMigrate(List<Host> hostlist){
		super();
		this.hostList=hostlist;
		triLoadInHost=new ArrayList<double[][]>() ;
		triUtilToVm=new HashMap<Vm,ArrayList<Double>>();
		triUtilToHost=new double[hostList.size()][3];
		for(int i=0;i<3;i++){
			HostDynamicLoad();
			triLoadInHost.add(getUtilToHost());
		}
	}
	
	public void run() {
		
		while (true) {
			HostDynamicLoad();
			prediction.predict(hostList,triLoadInHost);
			new SelVmMigrating(triUtilToVm);
			try { 
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void HostDynamicLoad() {
		for(Host  host:hostList){
			ArrayList<Double> temp1=new ArrayList<Double>();
			new ArrayList<Double>();
			for(Vm vm:host.getVmList()){
				double[] utilVm=new double[3];
				 utilVm[0]=Math.random();
				 utilVm[1]=Math.random();
				 utilVm[2]=Math.random();
				temp1.add(utilVm[0]);
				temp1.add(utilVm[1]);
				temp1.add(utilVm[2]);
				triUtilToVm.put(vm, temp1);
				triUtilToHost[host.getId()][0]+=utilVm[0]*vm.getNumberOfPes()*vm.getMips()/host.getTotalMips();
				triUtilToHost[host.getId()][1]+=utilVm[1]*vm.getBw()/host.getBw();
				triUtilToHost[host.getId()][2]+=utilVm[2]*vm.getRam()/host.getRam();
			}
		}
	}
	
	public double[][] getUtilToHost(){
		return triUtilToHost;
	}

}

