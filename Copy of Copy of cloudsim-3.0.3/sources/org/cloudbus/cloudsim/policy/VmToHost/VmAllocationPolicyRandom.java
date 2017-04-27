package org.cloudbus.cloudsim.policy.VmToHost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.hust.pso2.Utils;
import org.cloudbus.cloudsim.policy.ConstantConfig;
import org.cloudbus.cloudsim.policy.VmsToHosts.Main;

public class VmAllocationPolicyRandom extends VmAllocationPolicy {
	private Map<String, Integer> vmToHost;
	private Map<Integer,ArrayList<Integer>> vmsInHost;
	private List<Vm> vmList;

	public VmAllocationPolicyRandom(List<? extends Host> list) {
		super(list);
	}

	@Override
	public boolean allocateHostForVm(Vm vm) {
		return false;
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		return false;
	}

	/**
	 * 算法的主要调用逻辑以及结果的获取
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		List<Vm> vms=(List<Vm>) vmList;
		this.vmList=vms;
		int count=0;
		vmToHost=new HashMap<String, Integer>();
		vmsInHost=new HashMap<Integer, ArrayList<Integer>>();
		for (Vm vm :vms) {
			int[] randomIndex = Utils.getRandomSequence(getHostList().size());
			boolean flag=false;
			for (int i = 0; i < randomIndex.length; i++) {
				Host host=getHostList().get(randomIndex[i]);
				if(Utils.isSuitable(vm,host)){
					host.vmCreate(vm);
					Utils.updateVmResource(vm);
					vmToHost.put(vm.getUid(), host.getId());
					if(!vmsInHost.containsKey(host.getId())){
						ArrayList<Integer> list=new ArrayList<Integer>();
						list.add(vm.getId());
						vmsInHost.put(host.getId(), list);
					}else{
						vmsInHost.get(host.getId()).add(vm.getId());
					}
					flag=true;
					break;//找到合适主机
				}
			}
			if(flag){
				count++;
			}else{
				break;//不能全部放置
			}
		}
		if(count!=vms.size()){//资源还原
			Iterator<Entry<String,Integer>> iter=vmToHost.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,Integer> entry=iter.next();
				Host host=getHostById(entry.getValue());
				Vm vm=getVmByUid(entry.getKey());
				host.vmDestroy(vm);
				Utils.resetVmResource(vm);
			}
			return null;
		}else{
			//全部部署完成，记录虚拟机部署日志
			String fileName="G:/VmPlacedLog.txt";
			File file=new File(fileName);
			if(!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			try {
				BufferedWriter writer=new BufferedWriter(new FileWriter(fileName,true));
				Iterator<Entry<String,Integer>> iter=vmToHost.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String,Integer> entry=iter.next();
					String str="Vm #"+entry.getKey()+" has been created in Host #"+entry.getValue()+"\r\n";
					writer.write(str);
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Object map=vmToHost;
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		list.add((Map<String, Object>)map);
		//calcuMd();
		return list;
	}
	
	private void calcuMd(){
		Map<Integer,Double> std=new HashMap<Integer, Double>();
		double avg[]=new double[ConstantConfig.resNum];
		double sumCpu=0,sumMem=0,sumBw=0,sumStorage=0;
		double outerStd=0;double innerStd=0;
		List<Host> hostList=getHostList();
		Map<Integer, double[]> util=new HashMap<Integer, double[]>();
		Map<Integer, Double> utilAvg=new HashMap<Integer, Double>();
		int bestLoad=0;
		double banlanceDegree=0;
		
		for(Host host:hostList){
			util.put(host.getId(), new double[ConstantConfig.resNum]);
			double temp=0;
			util.get(host.getId())[0]=(host.getTotalMips()-host.getAvailableMips())/host.getTotalMips();
			util.get(host.getId())[1]=host.getRamProvisioner().getUsedRam()/(host.getRam()+0.0);
			util.get(host.getId())[2]=host.getBwProvisioner().getUsedBw()/(host.getBw()+0.0);
			util.get(host.getId())[3]=(Main.storageMap.get(host.getId())-host.getStorage())/(Main.storageMap.get(host.getId())+0.0);
			
			sumCpu+=util.get(host.getId())[0];
			sumMem+=util.get(host.getId())[1];
			sumBw+=util.get(host.getId())[2];
			sumStorage+=util.get(host.getId())[3];
			
//			boolean flag=false;
//			for(int i=0;i<4;i++){
//				if(!(util.get(host.getId())[i]<=0.9&&util.get(host.getId())[i]>=0.1)){
//					flag=true;
//					break;
//				}
//			}
//			if(!flag){
//				bestLoad++;
//			}
			
			double tempAvg=(util.get(host.getId())[0]+util.get(host.getId())[1]+util.get(host.getId())[2]+util.get(host.getId())[3])/4;
			utilAvg.put(host.getId(), tempAvg);
			for(int i=0;i<4;i++){
				temp+=Math.pow(util.get(host.getId())[i]-utilAvg.get(host.getId()), 2);
			}
			std.put(host.getId(), Math.sqrt(temp/4));
			innerStd+=std.get(host.getId());
		}
		
		avg[0]=sumCpu/(hostList.size());
		avg[1]=sumMem/(hostList.size());
		avg[2]=sumBw/(hostList.size());
		avg[3]=sumStorage/(hostList.size());
		for(Host host:hostList){
			boolean flag=false;
			for(int i=0;i<4;i++){
				if(!(Math.abs(util.get(host.getId())[i]-avg[i])<=0.1)){
					flag=true;
					break;
				}
			}
			if(!flag){
				bestLoad++;
			}
			
			double temp=0;
			for(int i=0;i<4;i++){
				temp+=Math.pow(util.get(host.getId())[i]-avg[i], 2);
				//temp+=Math.abs(util.get(host.getId())[i]-avg[i]);
			}
			outerStd+=Math.sqrt(temp);
			//outerStd+=temp;
		}
		banlanceDegree=(innerStd+outerStd)/hostList.size();//改进评价指标，引入内部资源方差
		//banlanceDegree=outerStd/hostList.size();
		System.out.println("banlanceDegree="+banlanceDegree);
	}
	
	private Host getHostById(Integer id){
		for(Host host:getHostList()){
			if(host.getId()==id)
				return host;
		}
		return null;
	}
	
	private Vm getVmByUid(String uid){
		for(Vm vm:vmList){
			if(vm.getUid().equals(uid))
				return vm;
		}
		return null;
	}

	@Override
	public void deallocateHostForVm(Vm vm) {
		
	}

	@Override
	public Host getHost(Vm vm) {
		int id=vmToHost.get(vm.getUid());
		for(Host host:getHostList()){
			if(host.getId()==id)
				return host;
		}
		return null;
	}

	@Override
	public Host getHost(int vmId, int userId) {
		String Uid=userId + "-" + vmId;
		int id=vmToHost.get(Uid);
		for(Host host:getHostList()){
			if(host.getId()==id)
				return host;
		}
		return null;
	}
	

}
