package org.cloudbus.cloudsim.hust.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;


//物理机资源类
public class PhysicalMachine extends Resource implements Cloneable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	PhysicalMachine pm;
	
	public PhysicalMachine(){};
	
	public PhysicalMachine(double camputingCapablity, double memory, double bandwidth, double disksize) {
		super(camputingCapablity, memory, bandwidth, disksize);
	}
	
	
	public PhysicalMachine(double [] array){
		this(array[0],array[1],array[2],array[3]);
	}
	
	public PhysicalMachine(PhysicalMachine pm) throws CloneNotSupportedException{
		super();
		this.pm=(PhysicalMachine) pm.clone();
	}
	
	/**
	 * 性能作差
	 * @param origin_pm
	 * @param curr_pm
	 * @return
	 */
	public PhysicalMachine diffOperate(PhysicalMachine origin_pm,PhysicalMachine curr_pm) {
		double[] array = new double[4];
		array=diffHelpArray(origin_pm, curr_pm);
		return new PhysicalMachine(array);
	}
	
	public PhysicalMachine updatePm(PhysicalMachine origin_pm,double []array){
		origin_pm.setCpu(array[0]);
		origin_pm.setMemory(array[1]);
		origin_pm.setBandwidth(array[2]);
		origin_pm.setDisk(array[3]);
		return origin_pm;
	}
	
	/**
	 * 恢复物理机上资源
	 * @param first  物理机剩余资源
	 * @param second  移走的虚拟机资源
	 * @return 
	 */
	public static PhysicalMachine add(PhysicalMachine first, VirtualMachine second){
		PhysicalMachine result=new PhysicalMachine(
				first.getCpu()+second.getCpu(),first.getMemory()+second.getMemory(),
				first.getBandwidth()+second.getBandwidth(),first.getDisk()+second.getDisk());
		return result;
	}
	
	/**
	 * gengxin物理机上资源
	 * @param first  物理机剩余资源
	 * @param second  移走的虚拟机资源
	 * @return 
	 */
	public static PhysicalMachine sub(PhysicalMachine first, VirtualMachine second){
		PhysicalMachine result=new PhysicalMachine(
				first.getCpu()-second.getCpu(),first.getMemory()-second.getMemory(),
				first.getBandwidth()-second.getBandwidth(),first.getDisk()-second.getDisk());
		return result;
	}
	
	
	/**
	 * 产生中间结果
	 * @param origin_pm
	 * @param curr_pm
	 * @return
	 */
	public  static double [] diffHelpArray(PhysicalMachine origin_pm,PhysicalMachine curr_pm){
		double[] array = new double[4];
		array[0] = origin_pm.getCpu() - curr_pm.getCpu();
		array[1] = origin_pm.getMemory() - curr_pm.getMemory();
		array[2] = origin_pm.getBandwidth() - curr_pm.getBandwidth();
		array[3] = origin_pm.getDisk() - curr_pm.getDisk();
		return array;
	}
	
	/**
	 * 通过已经使用的资源量和原始资源量，求出资源利用率向量
	 * @return
	 */
	public double [] getUtilArrayByOriginAndUsed(PhysicalMachine origin_pm,PhysicalMachine used_pm){
		double [] pm_origin_array=pm2Array(origin_pm);
		double [] pm_used_array=pm2Array(used_pm);		
		return getUtilArrayHelper(pm_origin_array,pm_used_array);
	}
	
	/**
	 * 帮助函数，求数组中相同下标的元素对应的比值
	 * @param origin
	 * @param used
	 * @return
	 */
	public double [] getUtilArrayHelper(double []origin,double []used){
		double []result=new double [4];
		int index,size;
		size=result.length;
		for(index=0;index<size;index++){
			result[index]=used[index]/origin[index];
		}
		return result;
	}
	
	/**
	 * 将物理机性能向量退化为double类型数组
	 * @param pm
	 * @return
	 */
	public static double [] pm2Array(PhysicalMachine pm){
		double []array=new double[4];
		array[0]=pm.getCpu();
		array[1]=pm.getMemory();
		array[2]=pm.getBandwidth();
		array[3]=pm.getDisk();
		return array;
	}
	

	/**
	 * 归一化，消去不同单位带来的量纲的差异
	 */
	// 找出一组向量的同一维中的最大值和最小值，类型为资源类的子类
	public static double[][] findMaxMin(PhysicalMachine[] machine) {
		double[][] result = new double[2][4];// 每一列对应性能指标中的最大值与最小值
		double cpuMax, cpuMin, memMax, memMin, bandwidthMax, bandwidthMin, diskMax, diskMin;

		cpuMax = memMax = bandwidthMax = diskMax = -Double.MAX_VALUE;
		cpuMin = memMin = bandwidthMin = diskMin = Double.MAX_VALUE;
		for (Resource ele : machine) {
			cpuMax = Math.max(cpuMax, ele.getCpu());
			memMax = Math.max(memMax, ele.getMemory());
			bandwidthMax = Math.max(bandwidthMax, ele.getBandwidth());
			diskMax = Math.max(diskMax, ele.getDisk());

			cpuMin = Math.min(cpuMin, ele.getCpu());
			memMin = Math.min(memMin, ele.getMemory());
			bandwidthMin = Math.min(bandwidthMin, ele.getBandwidth());
			diskMin = Math.min(diskMin, ele.getDisk());
		}
		result[0][0] = cpuMax;
		result[0][1] = memMax;
		result[0][2] = bandwidthMax;
		result[0][3] = diskMax;
		result[1][0] = cpuMin;
		result[1][1] = memMin;
		result[1][2] = bandwidthMin;
		result[1][3] = diskMin;

		return result;
	}

	public int getIndex(PhysicalMachine[] array, PhysicalMachine target) {
		int index, size, resultIndex = -1;
		size = array.length;
		for (index = 0; index < size; index++) {
			if (array[index].equals(target)) {
				resultIndex = index;
				break;
			}
		}
		return resultIndex;
	}
	
	public static double getPmLoad(PhysicalMachine pm){
		return 0;
	}

	// 重写类中的equals方法
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj) {
			return true;
		}
		if (obj instanceof PhysicalMachine) {
			PhysicalMachine other = (PhysicalMachine) obj;
			boolean first = (other.getCpu() == this.getCpu());
			boolean second = (other.getMemory() == this.getMemory());
			boolean third = (other.getBandwidth() == this.getBandwidth());
			boolean fifth = (other.getDisk() == this.getDisk());
			return first && second && third && fifth;
		}
		return false;
	}

	@Override // 总的来说，使用深度克隆，但是由于构造函数中并没有涉及到复杂对象的复制[如类对象由其他类对象构成，
	// 此时需要一个成员对象一个成员对象的克隆]，故直接使用浅克隆即可
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Object o = null;
		try {
			o = super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e.toString());
		}
		return o;
	}

	public Object deepClone() throws IOException, OptionalDataException, ClassNotFoundException {// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(this);// 从流里读出来
		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream oi = new ObjectInputStream(bi);
		return (oi.readObject());
	}
	

	public String toString() {
		String result = "["+ 
	        "处理机=" + this.getCpu() + "  " + 
	        "内存=" + this.getMemory() + " " + 
	        "带宽="+ this.getBandwidth() + " " +
	        "磁盘=" +this.getBandwidth()+""
	        		+ "]";
		return result;
	}
	
	

}
