package org.cloudbus.cloudsim.policy.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;

import org.cloudbus.cloudsim.hust.base.PhysicalMachine;
import org.cloudbus.cloudsim.hust.base.VirtualMachine;

public class HelpUtils {
	
	private static final String LINE_SEPARATOR = ExtendedHelper.getLineSeparator();
	
	public static PhysicalMachine [] createPhysicalMachineByCurrentTime( List<? extends Host> hostList){
		int pmIndex,len=hostList.size();
		PhysicalMachine [] createResult=new PhysicalMachine[len];
		double cpu,memory,bandwidth,disk;
		for(Host ele:hostList){
			pmIndex=ele.getId(); 
			cpu=0;
			for(Pe lee:ele.getPeList()){
				cpu+=lee.getPeProvisioner().getAvailableMips();
			}
			memory=ele.getRamProvisioner().getAvailableRam();
			bandwidth=ele.getBwProvisioner().getAvailableBw();
			disk=ele.getStorage();
			createResult[pmIndex]=new PhysicalMachine(cpu,memory,bandwidth,disk);
		}
		return createResult;
	}
	
	
	public static VirtualMachine [] createVirtualMachineFromStart( List<Vm> vmList){
		int vmIndex,len=vmList.size();
		VirtualMachine [] createResult=new VirtualMachine[len];
		double cpu,memory,bandwidth,disk;
		for(Vm ele:vmList){
			vmIndex=ele.getId();
			cpu=ele.getMips();
			memory=ele.getRam();
			bandwidth=ele.getBw();
			disk=ele.getSize();
			createResult[vmIndex]=new VirtualMachine(cpu,memory,bandwidth,disk);
		}
		return createResult;
	}
	
	/***
	 * 计算公式,负载不均衡度值在[0,1]之间
	 * @param origin_pm  物理机原始性能
	 * @param remain_pm  物理机剩余性能
	 * @return   负载不均衡度度
	 */
		public static double getLoadDegree(PhysicalMachine [] origin_pm,PhysicalMachine []remain_pm) {
			// 【1】求出每种资源R的平均资源利用率
			double sum_usedCpu, sum_usedMemory, sum_usedBandwidth, sum_usedDisk; // 每一类资源的总的剩余性能
			double aveCpu, aveMemory, aveBandwidth, aveDisk; // 每一类资源的平均利用率
			sum_usedCpu = sum_usedMemory = sum_usedBandwidth = sum_usedDisk = 0.0; // 剩余资源累加计数
			int index, size;
			size = origin_pm.length;

			for (index = 0; index < size; index++) {
				PhysicalMachine origin_resource, current_resource;
				origin_resource = origin_pm[index]; // 取出物理机原始性能
				current_resource = remain_pm[index];// 取出物理机原始性能
				sum_usedCpu += (origin_resource.getCpu() - current_resource.getCpu())/origin_resource.getCpu();
				sum_usedMemory += (origin_resource.getMemory() - current_resource.getMemory())/origin_resource.getMemory();
				sum_usedBandwidth += (origin_resource.getBandwidth() - current_resource.getBandwidth())/origin_resource.getBandwidth();
				sum_usedDisk += (origin_resource.getDisk() - current_resource.getDisk())/origin_resource.getDisk();
			}
			aveCpu = sum_usedCpu /size; // CPU资源的平均利用率
			aveMemory = sum_usedMemory / size;// 内存的平均利用率
			aveBandwidth = sum_usedBandwidth / size;// 带宽资源的平均利用率
			aveDisk = sum_usedDisk / size;// 硬盘资源的平均利用率

			// 【2】再次遍历,求出负载不均衡度
			double per_usedCpu, per_usedMemory, per_usedBandwidth, per_usedDisk; // 每一类资源的剩余性能
			per_usedCpu = per_usedMemory = per_usedBandwidth = per_usedDisk = 0;
			double currsum = 0;
			int bestload=0;
			for (index = 0; index < size; index++) {
				PhysicalMachine origin_resource, current_resource;
				origin_resource = origin_pm[index]; // 取出物理机原始性能
				current_resource = remain_pm[index]; // 取出物理机原始性能
				per_usedCpu = (origin_resource.getCpu() - current_resource.getCpu())/origin_resource.getCpu();
				per_usedMemory = (origin_resource.getMemory() - current_resource.getMemory())/origin_resource.getMemory();
				per_usedBandwidth = (origin_resource.getBandwidth() - current_resource.getBandwidth())/origin_resource.getBandwidth();
				per_usedDisk = (origin_resource.getDisk() - current_resource.getDisk())/origin_resource.getDisk();
				currsum += (Math.abs(per_usedCpu - aveCpu) + Math.abs(per_usedMemory - aveMemory)
						+ Math.abs(per_usedBandwidth - aveBandwidth) + Math.abs(per_usedDisk - aveDisk));
//				currsum += (Math.pow(per_usedCpu - aveCpu,2) + Math.pow(per_usedMemory - aveMemory,2)
//				+ Math.pow(per_usedBandwidth - aveBandwidth,2) + Math.pow(per_usedDisk - aveDisk,2));
				if(Math.abs(per_usedCpu - aveCpu)<=0.05&&Math.abs(per_usedMemory - aveMemory)<=0.05&&
						Math.abs(per_usedBandwidth - aveBandwidth)<=0.05&&Math.abs(per_usedDisk - aveDisk)<=0.05)
					bestload++;
			}
			System.out.println(currsum/size+",  "+bestload);
			return currsum / size; // 求出负载不均衡度
		}
		
		/**
		 * 物理机性能的归一化
		 * @param physicalMachine  物理机
		 * @return  归一化的物理机性能
		 */
		public static  PhysicalMachine [] getNormalized_Pm(PhysicalMachine []physicalMachine){
			// 获取物理主机列表中各物理主机性能参数的最大值与最小值
			double[][] arrayA = PhysicalMachine.findMaxMin(physicalMachine);

			int size_pm, index;
			size_pm = physicalMachine.length;
			
			PhysicalMachine [] result_Pm=new PhysicalMachine [size_pm];

			// 进行归一化处理，将处理前和处理后的结果放在一个映射表中，方便记录原始数据和变化后的数据
			// 主机性能归一化
			for (index = 0; index < size_pm; index++) {
				PhysicalMachine ele = physicalMachine[index];
				double normalizedCPU =(arrayA[0][0] - arrayA[1][0])==0?1: (ele.getCpu() - arrayA[1][0]) / (arrayA[0][0] - arrayA[1][0]);
				double normalizedMEM = (arrayA[0][1] - arrayA[1][1])==0?1:(ele.getMemory() - arrayA[1][1]) / (arrayA[0][1] - arrayA[1][1]);
				double normalizedBandwidth = (arrayA[0][2] - arrayA[1][2])==0?1:(ele.getBandwidth() - arrayA[1][2]) / (arrayA[0][2] - arrayA[1][2]);
				double normalizedDisksize = (arrayA[0][3] - arrayA[1][3])==0?1:(ele.getDisk() - arrayA[1][3]) / (arrayA[0][3] - arrayA[1][3]);
				result_Pm[index] = new PhysicalMachine(normalizedCPU, normalizedMEM, normalizedBandwidth,
						normalizedDisksize);
			}
			return result_Pm;
		  }
		
		/**
		 * 虚拟机性能的归一化
		 * @param physicalMachine  虚拟机
		 * @return  归一化的虚拟机性能
		 */
		  public static  VirtualMachine [] getNormalized_Vm(VirtualMachine [] virtualMachine ){
				// 获取物理主机列表中各虚拟机性能参数的最大值与最小值
				double[][] arrayB = VirtualMachine.findMaxMin(virtualMachine);

				int size_vm, index;
				size_vm =virtualMachine.length;
				
				VirtualMachine [] result_Pm=new VirtualMachine [size_vm];

				// 进行归一化处理，将处理前和处理后的结果放在一个映射表中，方便记录原始数据和变化后的数据
				// 虚拟机性能归一化
				for (index = 0; index < size_vm; index++) {
					VirtualMachine ele =virtualMachine[index];
					double normalizedCPU =(arrayB[0][0] - arrayB[1][0])==0?1: (ele.getCpu() - arrayB[1][0]) / (arrayB[0][0] - arrayB[1][0]);
					double normalizedMEM = (arrayB[0][1] - arrayB[1][1])==0?1:(ele.getMemory() - arrayB[1][1]) / (arrayB[0][1] - arrayB[1][1]);
					double normalizedBandwidth = (arrayB[0][2] - arrayB[1][2])==0?1:(ele.getBandwidth() - arrayB[1][2]) / (arrayB[0][2] - arrayB[1][2]);
					double normalizedDisksize = (arrayB[0][3] - arrayB[1][3])==0?1:(ele.getDisk() - arrayB[1][3]) / (arrayB[0][3] - arrayB[1][3]);
					result_Pm[index] = new VirtualMachine(normalizedCPU, normalizedMEM, normalizedBandwidth,
							normalizedDisksize);
				}
				return result_Pm;
		}
		 
		/**
		 * 
		 * @param pm_normalized 归一化的物理机资源
		 * @param vm_normalized 归一化的虚拟机资源
		 * @return  匹配距离越小 ，匹配距离越小，越能满足性能需求
		 */
		public static double getMatchDistance(PhysicalMachine pm_normalized,VirtualMachine vm_normalized){
			double sum = 0; // 累加
			double absdiff_1 = Math.abs(pm_normalized.getCpu() - vm_normalized.getCpu());
			double absdiff_2 = Math.abs(pm_normalized.getMemory() - vm_normalized.getMemory());
			double absdiff_3 = Math.abs(pm_normalized.getBandwidth() - vm_normalized.getBandwidth());
			double absdiff_4 = Math.abs(pm_normalized.getDisk() - vm_normalized.getDisk());
			sum += Math.pow(absdiff_1, 2.0) + Math.pow(absdiff_2, 2.0) + Math.pow(absdiff_3, 2.0)
					+ Math.pow(absdiff_4, 2.0);
			return Math.sqrt(sum)/2;
		}
		
		/**
		 * 产生互不相同的随机数
		 * @param number
		 * @return
		 */
		public static int []createNoRepeatNumber(int number){
			 int values[] = new int[number];   
		        int temp1,temp2,temp3;   
		        Random random = new Random();   
		        
		        for(int i = 0;i < values.length;i++){
		            values[i] = i;
		        }
		        
		        //随机交换values.length次   
		        for(int i = 0;i < values.length;i++){   
		            temp1 = Math.abs(random.nextInt()) % (values.length); //随机产生一个位置   
		            temp2 = Math.abs(random.nextInt()) % (values.length); //随机产生另一个位置   
		            
		            if(temp1 != temp2){
		                temp3 = values[temp1];   
		                values[temp1] = values[temp2];   
		                values[temp2] = temp3;
		            } 
		        }   
			return values;
		}
		
	/***
	 * 
	 * @param value  待四舍五入的值
	 * @param nums  保留小数点的位数
	 * @return
	 */
	 public static  double roundDoubleValue(double value,int nums){
		 return new BigDecimal(value).setScale(nums,BigDecimal.ROUND_HALF_UP).doubleValue();
	 }
		
	
	 public static void visitTreeMap(TreeMap<Integer,Vector<Integer>> deployMap,String policyName){
		 System.out.print(LINE_SEPARATOR+"--------------------使用"+policyName+"策略-------------------------");
	    	System.out.print(LINE_SEPARATOR+"--------------------物理机虚拟机映射表-------------------------");
	    	for(Entry<Integer, Vector<Integer>> entry:deployMap.entrySet()){
	    		System.out.print(LINE_SEPARATOR+"hostId="+entry.getKey()+"-------------"+"vmList="+entry.getValue());
	    	}
	    	System.out.print(LINE_SEPARATOR);
	    }
	 
	 public static void visitHashMap(HashMap<Integer,Vector<Integer>> deployMap){
	    	System.out.print(LINE_SEPARATOR+"--------------------物理机部署信息-------------------------");
	    	for(Entry<Integer, Vector<Integer>> entry:deployMap.entrySet()){
	    		System.out.print(LINE_SEPARATOR+entry.getKey()+"-------------"+entry.getValue());
	    	}
	    }
	 
	 
	 public static void printOrigin_Pms(PhysicalMachine[] pms){
		 System.out.print("物理机原始资源"+LINE_SEPARATOR);
		 for(PhysicalMachine ele:pms)
			 System.out.print(ele+LINE_SEPARATOR);
	 }
	 
	 public static void printReamin_Pms(PhysicalMachine[] pms){
		 System.out.print("物理机剩余资源"+LINE_SEPARATOR);
		 for(PhysicalMachine ele:pms)
			 System.out.print(ele+LINE_SEPARATOR);
	 }
	 
	 public static void printOrigin_Vms(VirtualMachine[] pms){
		 System.out.print("虚拟机原始资源"+LINE_SEPARATOR);
		 for(VirtualMachine ele:pms)
			 System.out.print(ele+LINE_SEPARATOR);
	 }

}
