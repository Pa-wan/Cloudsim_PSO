package org.cloudbus.cloudsim.hust.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;


//虚拟机资源类
public class VirtualMachine extends Resource implements Cloneable {

	public VirtualMachine(){};
	
	public VirtualMachine(double cpu, double memory, double bandwidth, double disksize) {
		super(cpu, memory, bandwidth, disksize);
		// TODO Auto-generated constructor stub
	}

	public VirtualMachine(double [] array){
		this(array[0],array[1],array[2],array[3]);
	}

	/**
	 * 归一化，消去不同单位带来的量纲的差异
	 */
	// 找出一组向量的同一维中的最大值和最小值，类型为资源类的子类
	public static double[][] findMaxMin(VirtualMachine[] machine) {
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
	
	public static  double getVmLoad(VirtualMachine vm){
		return 0;
	}

	// 重写类中的equals方法
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj) {
			return true;
		}
		if (obj instanceof VirtualMachine) {
			VirtualMachine other = (VirtualMachine) obj;
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
		return super.clone();
	}

	public Object deepClone() throws IOException, OptionalDataException, ClassNotFoundException {// 将对象写到流里
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(this);// 从流里读出来
		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream oi = new ObjectInputStream(bi);
		return (oi.readObject());
	}

	// 自定义输出
//	public String toString() {
//		String result = "[处理机=" + Utils.round(this.getCpu(),2,BigDecimal.ROUND_HALF_UP) + "  " + "内存=" + Utils.round(this.getMemory(),2,BigDecimal.ROUND_HALF_UP) + " " + "带宽="
//				+ Utils.round(this.getBandwidth(),2,BigDecimal.ROUND_HALF_UP) + " " + "磁盘=" + Utils.round(this.getDisk(),2,BigDecimal.ROUND_HALF_UP)+"]";
//		return result;
//	}
	
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
