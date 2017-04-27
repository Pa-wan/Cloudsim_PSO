package org.cloudbus.cloudsim.hust.base;

//资源类
public class Resource {
	private double cpu; // 计算能力
	private double memory;// 内存
	private double bandwidth;// 带宽
	private double disk; // 磁盘容量

	public Resource(double cpu, double memory, double bandwidth, double disk) {
		this.cpu = cpu;
		this.memory = memory;
		this.bandwidth = bandwidth;
		this.disk = disk;
	}

	public Resource(){};
	
	public double getCpu() {
		return this.cpu;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public double getMemory() {
		return memory;
	}

	public void setMemory(double memory) {
		this.memory = memory;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public double getDisk() {
		return disk;
	}

	public void setDisk(double disk) {
		this.disk = disk;
	}

	// 重写类中的equals方法
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj) {
			return true;
		}
		if (obj instanceof Resource) {
			Resource other = (Resource) obj;
			boolean first = (other.cpu == this.cpu);
			boolean second = (other.memory == this.memory);
			boolean third = (other.bandwidth == this.bandwidth);
			boolean fifth = (other.disk == this.disk);
			return first && second && third && fifth;
		}
		return false;
	}

}
