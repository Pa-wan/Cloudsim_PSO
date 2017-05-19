package org.cloudbus.cloudsim.hust.pso2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.policy.VmsToHosts.Main;
import org.cloudbus.cloudsim.policy.utils.ExtHelper;

import com.sun.org.apache.bcel.internal.generic.LoadClass;

public class Particle {
	private int bestLoad;// 最佳负载区间物理机数量
	public double bestfitness; // 粒子本身的最优解
	// private Map<String,Integer> vmTohost;//每个粒子每次循环输出一个方案

	private double util[][];// 利用率矩阵
	private int[] pos;// 粒子的位置，数组的维度表示虚拟机的个数
	private int[] v;
	private double fitness;
	private int[] pbest; // 粒子的历史最好的位置
	public static int[] gbest; // 所有粒子找到的最好位置
	private double pbest_fitness;// 粒子的历史最优适应值
	private int dims;
	private double w;
	private double c1;
	private double c2;
	private static Random rnd;
	private Map<String, Integer> vmTohost;// 每个粒子每次迭代产生的放置方案

	int size;// 单个虚拟机可以放置的主机数量
	private List<Host> fitList;//
	private List<Host> hostlist;
	private List<Vm> vmlist;
	public int count;// 最差适应度值次数
	private double utilAvg[];// 单个物理机平均利用率向量
	public static int runtimes;
	private int cnt;

	public Particle(List<Vm> vms, List<Host> hosts) {
		hostlist = ExtHelper.createHostList(hosts.size()); // 创建物理机
		vmlist = ExtHelper.createVmList(Main.brokerId, vms.size());
		this.dims = vmlist.size();
		cnt = 0;
		pos = new int[dims];
		v = new int[dims];
		pbest = new int[dims];
		gbest = new int[dims];
		fitness = 1;
		pbest_fitness = Double.MAX_VALUE;
		vmTohost = new HashMap<String, Integer>();
		utilAvg = new double[hostlist.size()];
		util = new double[hostlist.size()][3];
	}

	public void init() {

		fitList = hostlist;
		rnd = new Random();
		for (Vm vm : vmlist) {
			updateVmList(vm);
			int size = fitList.size();
			if (size != 0) {
				int idx = rnd.nextInt(size);
				Host host = fitList.get(idx);
				pos[vm.getId()] = idx;
				// 对于每个粒子，在计算位置和速度过程中，只把vm加入host的属性列表中，而不更新主机资源
				// 在对粒子进行适应度值计算时在更新资源
				fitList.get(idx).getVmList().add(vm);
				vmTohost.put(vm.getUid(), host.getId());
				pbest[vm.getId()] = pos[vm.getId()];
				v[vm.getId()] = rnd.nextInt(fitList.size()) - pos[vm.getId()];
				// updateResource(vm, host);
			}
		}
		calcuMd();
	}

	/**
	 * 将vm分配的资源返还到host,为下次循环准备，主要目的在于只分配一次vmList,hostList防止堆溢出
	 */
	private void reset() {
		bestLoad = 0;
		vmTohost.clear();
		for (Host host : hostlist) {
			host.vmDestroyAll();
		}
		for (Vm vm : vmlist) {
			Utils.resetVmResource(vm);
		}
	}

	/**
	 * 产生问题的可行解，并计算该解的度量指标
	 */
	public void run() {
		reset();
		updatePos();
		calcuMd();
	}

	private void updateResource(Vm vm, Host host) {
		host.vmCreate(vm);// 更新资源，CurrentAllocatedMips,CurrentAllocatedSize
		vm.setCurrentAllocatedSize(vm.getSize());
		List<Double> list = new ArrayList<Double>();
		list.add(vm.getCurrentRequestedTotalMips());
		vm.setCurrentAllocatedMips(list);
	}

	/**
	 * 求标准差
	 * 
	 * @param x
	 * @return
	 */
	private static double StandardDiviation(double[] x) {
		int m = x.length;
		double sum = 0;
		for (int i = 0; i < m; i++) {// 求和
			sum += x[i];
		}
		double dAve = sum / m;// 求平均值
		double dVar = 0;
		for (int i = 0; i < m; i++) {// 求方差
			dVar += (x[i] - dAve) * (x[i] - dAve);
		}
		return Math.sqrt(dVar / m);
	}

	/**
	 * 计算总体均衡程度
	 */
	private void calcuMd() {
		double load = 0;
		// 在对物理机进行均衡度计算时才更新每个物理机的资源状态
		for (Host host : hostlist) {
			for (Vm vm : host.getVmList())
				updateResource(vm, host);
			// VMPlacement.updateHost(host);// 根据主机中vmlist编号更新主机资源
//			util[host.getId()][0] = (host.getTotalMips() - host
//					.getAvailableMips()) / host.getTotalMips();
//			util[host.getId()][1] = host.getRamProvisioner().getUsedRam()
//					/ (host.getRam() + 0.0);
//			util[host.getId()][2] = host.getBwProvisioner().getUsedBw()
//					/ (host.getBw() + 0.0);
//				load=(1/(1-util[host.getId()][0]))*(1/(1-util[host.getId()][1]))*(1/(1-util[host.getId()][2]));
			load=host.getLoad();
			utilAvg[host.getId()] = load;
		}
		fitness = StandardDiviation(utilAvg);
		if (fitness < pbest_fitness) {
			for (Vm vm : vmlist) {
				pbest[vm.getId()] = pos[vm.getId()];
			}
			pbest_fitness = fitness;
		}
		// reset();// 每个粒子评估结束之后还原主机资源，以确保下一个粒子能正确计算负载

	}

	/**
	 * 在每次循环结束时，先调用reset方法，后调用updatePos方法
	 */
	private void updatePos() {// 更新位置和速度
		// 线性减少w，正态函数动态调整c1，c2
		w = 0.9 - 0.5 / 100 * cnt;
		c1=c2=2;
		for (Vm vm : vmlist) {
			updateVmList(vm);
			size = fitList.size();
			v[vm.getId()] = (int) (w * v[vm.getId()] + c1 * rnd.nextDouble()
					* (pbest[vm.getId()] - pos[vm.getId()]) + c2
					* rnd.nextDouble() * (gbest[vm.getId()] - pos[vm.getId()]));
			// 限制速度和位置
			if (v[vm.getId()] > size - pos[vm.getId()] - 1) {
				v[vm.getId()] = size - pos[vm.getId()] - 1;
			}
			if (v[vm.getId()] < -pos[vm.getId()]) {
				v[vm.getId()] = -pos[vm.getId()];
			}
			pos[vm.getId()] = pos[vm.getId()] + v[vm.getId()];
			fitList.get(pos[vm.getId()]).getVmList().add(vm);// 第i个vm放入第pos[i]个host
			vmTohost.put(vm.getUid(), fitList.get(pos[vm.getId()]).getId());
//			updateResource(vm, fitList.get(pos[vm.getId()]));
		}
		cnt++;
	}

	/**
	 * 更新每个虚拟机可以匹配的主机列表
	 */
	private void updateVmList(Vm vm) {
		fitList = new ArrayList<Host>();
		for (Host host : hostlist) {
			if (Utils.isSuitable(vm, host)) {
				fitList.add(host);// 将符合条件的物理主机放入数组中
			}
		}
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public int[] getPos() {
		return pos;
	}

	public void setPos(int[] pos) {
		this.pos = pos;
	}

	public Map<String, Integer> getVmTohost() {
		return vmTohost;
	}

	public void setVmTohost(Map<String, Integer> vmTohost) {
		this.vmTohost = vmTohost;
	}

	public int getBestLoad() {
		return bestLoad;
	}

	public void setBestLoad(int bestLoad) {
		this.bestLoad = bestLoad;
	}
}
