package org.cloudbus.cloudsim.hust.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.cloudbus.cloudsim.hust.base.PhysicalMachine;
import org.cloudbus.cloudsim.hust.base.VirtualMachine;


public class Utils {

	// 从指定的数组中随机取出一个非0数字,假设为:10101
	public static int getRandomIndex(int[] vms_deploy_index) {
		int count = getNumOf0(vms_deploy_index);
		int[] indexFlag = new int[count];
		int cnt = 0;
		for (int index = 0; index < vms_deploy_index.length; index++) {
			if (vms_deploy_index[index] == 0) {
				indexFlag[cnt++] = index;
			}
		}
		//bug
		int randomIndex = GetRandomIndex(count);
		return indexFlag[randomIndex];
	}
	
	public boolean JudgeToEnd(int []array){
		int index,size;
		size=array.length;
		boolean flag=false;
		for(index=0;index<size;index++){
			if(array[index]==0){
				flag=true;
				break;
			}
		}
		return flag;
	}

	public static int getNumOf0(int[] array) {
		int count = 0;
		for (int index = 0; index < array.length; index++) {
			if (array[index] == 0)
				count++;
		}
		return count;
	}

	// 只随机获取下标
	public static int GetRandomIndex(int size) {
		Random random = new Random();
		int result = random.nextInt(size);
		return result;
	}

	// 判断物理机上的剩余资源量能否满足蚂蚁需求
	public double[] getDiffArray(PhysicalMachine physicalMachine, VirtualMachine virtualMachine) {
		double[] array = new double[4];
		array[0] = physicalMachine.getCpu() - virtualMachine.getCpu();
		array[1] = physicalMachine.getMemory() - virtualMachine.getMemory();
		array[2] = physicalMachine.getBandwidth() - virtualMachine.getBandwidth();
		array[3] = physicalMachine.getDisk() - virtualMachine.getDisk();
		return array;
	}
	
	

	// 不直接判断，资源列表中使用计算中间结果
	public static  boolean canDeployByArray(double[] array) {
		boolean flag = true;
		for (double ele : array) {
			if (ele < 0) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 直接判断
	public boolean canDeployDirectly(PhysicalMachine physicalMachine, VirtualMachine virtualMachine) {
		double[] array = getDiffArray(physicalMachine, virtualMachine);
		return canDeployByArray(array);
	}

	// java中交换数组元素中的值
	public void swap(int[] data, int i, int j) {
		if (i == j) {
			return;
		}
		data[i] = data[i] + data[j];
		data[j] = data[i] - data[j];
		data[i] = data[i] - data[j];
	}

	// 产生互不相同的随机数的关键
	public void shuffle(int[] data) {
		for (int i = 0; i < data.length - 1; i++) {
			int j = (int) (data.length * Math.random());
			swap(data, i, j);
		}
	}

	public void fillIntArray(int[] data) {
		int index;
		for (index = 0; index < data.length; index++)
			data[index] = index;
	}

	public void fill_pm_machine(PhysicalMachine[] array, int size) {
		int index;
		PhysicalMachine pm = new PhysicalMachine(0, 0, 0, 0);
		for (index = 0; index < array.length; index++)
			array[index] = pm;
	}

	// 使用指定的元素来为一维数组赋值
	public void fillArrays(int[] array, int init) {
		int index;
		for (index = 0; index < array.length; index++)
			array[index] = init;
	}

	// 使用指定的元素来为二维数组赋值
	public void fillTwoDimArray(double[][] array, int rows, int cols, double num) {
		int indexI, indexJ;
		for (indexI = 0; indexI < rows; indexI++)
			for (indexJ = 0; indexJ < cols; indexJ++)
				array[indexI][indexJ] = num;
	}

	public static void printIntArray(int[] array) {
		for (int ele : array)
			System.out.print(ele + " ");
		System.out.println();
	}

	public static void printDoubleArray(double[] p) {
		for (double ele : p)
			System.out.print(ele + " ");
	}

	public static void printDoubleMatrix(double[][] pheromone) {
		int i, j;
		for (i = 0; i < pheromone.length; i++) {
			for (j = 0; j < pheromone[i].length; j++) {
				System.out.print(pheromone[i][j] + " ");
			}
			System.out.println();
		}
	}
	
    /**
     * 对double数据进行取精度.
     * For example: 
     * double value = 100.345678; 
     * double ret = round(value,4,BigDecimal.ROUND_HALF_UP);
     * ret为100.3457 
     * @param value
     *            double数据.
     * @param scale
     *            精度位数(保留的小数位数).
     * @param roundingMode
     *            精度取值方式.
     * @return 精度计算后的数据.
     */
    public static double round(double value, int scale, int roundingMode) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(scale, roundingMode);
        double d = bd.doubleValue();
        bd = null;
        return d;
    }
    
    /**
     * 获取每台物理机上每类资源的利用率
     * @param orgin  物理机上原始的资源量
     * @param curr   物理机的当前资源量
     * @return       集群中每只物理机的各类资源的利用率
     */
    public static double[][]utilityForAllPhysicalMachine(PhysicalMachine []orgin,PhysicalMachine []curr){
    	int row,col,indexPm;
    	row=orgin.length;
    	col=4;
    	double [][]array=new double[row][col];
    	for(indexPm=0;indexPm<row;indexPm++){
    		array[indexPm][0]=(orgin[indexPm].getCpu()-curr[indexPm].getCpu())/orgin[indexPm].getCpu();
    		array[indexPm][1]=(orgin[indexPm].getMemory()-curr[indexPm].getMemory())/orgin[indexPm].getMemory();
    		array[indexPm][2]=(orgin[indexPm].getBandwidth()-curr[indexPm].getBandwidth())/orgin[indexPm].getBandwidth();
    		array[indexPm][3]=(orgin[indexPm].getDisk()-curr[indexPm].getDisk())/orgin[indexPm].getDisk();
    	}
    	return array;
    }
    
    /**
     * 获取集群中物理机的各类资源的利用率
     * @param orgin  物理机上原始的资源量
     * @param curr   物理机的当前资源量
     * @return       将物理机的所有资源当做一个整体进行看待
     */
    public double []utilityForDataCenterBigDecimal(PhysicalMachine []orgin,PhysicalMachine []curr){
    	double []result=new double[4];
    	int index,size;
    	size=orgin.length;
    	BigDecimal totalcpu,totalmemory,totalbandwidth,totaldisk;
    	BigDecimal remainecpu,remainememory,remainebandwidth,remainedisk;
    	//初始化
    	totalcpu=totalmemory=totalbandwidth=totaldisk=new BigDecimal(0.0);
    	remainecpu=remainememory=remainebandwidth=remainedisk=new BigDecimal(0.0);
    	
    	for(index=0;index<size;index++){
    		//计算原始各类资源的资源综合
    		totalcpu=totalcpu.add(new BigDecimal(orgin[index].getCpu()));
    		totalmemory=totalmemory.add(new BigDecimal(orgin[index].getMemory()));
    		totalbandwidth=totalbandwidth.add(new BigDecimal(orgin[index].getBandwidth()));
    		totaldisk=totaldisk.add(new BigDecimal(orgin[index].getDisk()));
    		//计算剩余资源的资源综合
    		remainecpu=remainecpu.add(new BigDecimal(curr[index].getCpu()));
    		remainememory=remainememory.add(new BigDecimal(curr[index].getMemory()));
    		remainebandwidth=remainebandwidth.add(new BigDecimal(curr[index].getBandwidth()));
    		remainedisk=remainedisk.add(new BigDecimal(curr[index].getDisk()));
    	}
    	
    	//计算各类资源的利用率
    	result[0]=(totalcpu.subtract(remainecpu)).divide(totalcpu).doubleValue();
    	result[1]=(totalmemory.subtract(remainememory)).divide(totalmemory).doubleValue();
    	result[2]=(totalbandwidth.subtract(remainebandwidth)).divide(totalbandwidth).doubleValue();
    	result[3]=(totaldisk.subtract(remainedisk)).divide(totaldisk).doubleValue();
    	
    	return result;
    }
    
    /**
     * 同样的物理机资源和虚拟机资源条件下，获得结果必然一样的
     * @param orgin
     * @param curr
     * @return
     */
    public static double []utilityForDataCenter(PhysicalMachine []orgin,PhysicalMachine []curr){
    	double []result=new double[4];
    	int index,size;
    	size=orgin.length;
    	double totalcpu,totalmemory,totalbandwidth,totaldisk;
    	double remainecpu,remainememory,remainebandwidth,remainedisk;
    	//初始化
    	totalcpu=totalmemory=totalbandwidth=totaldisk=0;
    	remainecpu=remainememory=remainebandwidth=remainedisk=0;
    	
    	for(index=0;index<size;index++){
    		//计算原始各类资源的资源综合
    		totalcpu+=orgin[index].getCpu();
    		totalmemory+=orgin[index].getMemory();
    		totalbandwidth+=orgin[index].getBandwidth();
    		totaldisk+=orgin[index].getDisk();
    		
    		//计算剩余资源的资源综合
    		remainecpu+=curr[index].getCpu();
    		remainememory+=curr[index].getMemory();
    		remainebandwidth+=curr[index].getBandwidth();
    		remainedisk+=curr[index].getDisk();
    	}
    	
    	//计算各类资源的利用率
    	result[0]=(totalcpu-remainecpu)/totalcpu;
    	result[1]=(totalmemory-remainememory)/totalmemory;
    	result[2]=(totalbandwidth-remainebandwidth)/totalbandwidth;
    	result[3]=(totaldisk-remainedisk)/totaldisk;
    	
    	return result;
    }
    
    /**
     * 代码运行内存分析
     * @param args
     */
    public void getCodeRunningMemory(String []args){
    	//获取程序运行内存
		Runtime run = Runtime.getRuntime();
		
		//将程序的初始参数作为命令行传入
		
		// 获取开始时内存使用量
		long startMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " startMemory:" + startMem );
		
		//获取结束时内存使用量
		long endMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " endMemory:" + endMem );
		System.out.println("memory difference:" + (endMem-startMem));
    }
    
    public static void printPM_OriginArray(PhysicalMachine []pms){
    	System.out.println("--------------------物理机原始资源信息-------------------------");
    	for(PhysicalMachine pm:pms)
    		System.out.println(pm);
    }
    
    public static void printPM_RemainArray(PhysicalMachine []pms){
    	System.out.println("--------------------物理机剩余资源信息-------------------------");
    	for(PhysicalMachine pm:pms)
    		System.out.println(pm);
    }
    
    public static void printVM_OriginArray(VirtualMachine []pms){
    	System.out.println("--------------------虚拟机原始资源信息-------------------------");
    	for(VirtualMachine pm:pms)
    		System.out.println(pm);
    }
    
    public static double [] PmToArray(PhysicalMachine pm){
    	double [] array=new double[4];
    	array[0]=pm.getCpu();
    	array[1]=pm.getMemory();
    	array[2]=pm.getBandwidth();
    	array[3]=pm.getDisk();
    	return array;
    }
    
    public static double [] VmToArray(VirtualMachine pm){
    	double [] array=new double[4];
    	array[0]=pm.getCpu();
    	array[1]=pm.getMemory();
    	array[2]=pm.getBandwidth();
    	array[3]=pm.getDisk();
    	return array;
    }
    
    public static void visitDeployMap(HashMap<Integer,ArrayList<Integer>> deployMap){
    	System.out.println("--------------------物理机部署信息-------------------------");
    	for(Entry<Integer, ArrayList<Integer>> entry:deployMap.entrySet()){
    		System.out.println(entry.getKey()+"-------------"+entry.getValue());
    	}
    }
    
    public static void printUtilInfo(PhysicalMachine []origin_pm,PhysicalMachine [] pms){
	    double []array=utilityForDataCenter(origin_pm, pms);
	    System.out.println("\n--------集群中物理机的资源利用率 cpu,memory,bandwidth,disk---------");
	    printDoubleArray(array);
	    System.out.println();
    }
}
