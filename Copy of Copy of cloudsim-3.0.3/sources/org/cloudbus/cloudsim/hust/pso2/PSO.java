package org.cloudbus.cloudsim.hust.pso2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;

public class PSO {
	private Particle[] pars;//粒子
	private int pcount;//粒子数量
	private List<Vm> vmlist;//虚拟机列表
	private List<Host> hostlist;//物理机列表
	public static double[] gbest;//粒子全局最优位置
	public static int MAX_GEN; // 运行代数
	private Map<String,ArrayList<Host>> allowed;//可以满足该虚拟机的物理机候选表
	private Map<Integer,Host> hostById;
	private Map<String,Vm> vmByUid;
	private Solution bestSolution;
	private double bestLoad;
	
	private double global_best;// 全局最优适应度值
	private double global_worst;
	private static int dim;// 维度
	private static int Imax;// 最差纪录次数阈值
	private int runtime; //迭代次数
	
	public PSO(int num, int runtimes, List<Vm> vmList, List<Host> hostList){
		this.vmlist = vmList;
		dim = vmList.size();
		this.hostlist = hostList;
		this.pcount = num;
		this.runtime = runtimes;
		Particle.runtimes = runtimes;
		global_best = Double.MAX_VALUE;
		// int index = -1;// 拥有最好位置的粒子编号
		Imax = 3;
		pars = new Particle[pcount + 1];// 初始化多一个粒子，不参与位置速度的更新，只用作暂存中间数据
		// 类的静态成员的初始化
		init();
	}
	
	public void init(){
		hostById=new HashMap<Integer, Host>();
		vmByUid=new HashMap<String, Vm>();
		 for(Host host:hostlist)
			  hostById.put(host.getId(), host);
		 for(Vm vm:vmlist)
			 vmByUid.put(vm.getUid(), vm);
		System.out.println("======init start=====");
		for (int i = 0; i < pcount; i++) {
			pars[i] = new Particle(vmlist, hostlist);
			pars[i].init();
		}
		pars[pcount] = new Particle(vmlist, hostlist);
		pars[pcount].init();
		System.out.println("======init end=====");
	 }

	public void solve(){
		int δ = 2;	
		int i;
		System.out.println("=========run start========");
		for (i = 0; i < runtime; i++) {
			double c1 = 0.5 + (4.5 - 0.5) / (Math.sqrt(2 * Math.PI) * δ)
					* Math.exp(-(i / runtime) * (i / runtime) / (2 * δ * δ));
			double c2 = 2.5 + (0.5 - 2.5) / (Math.sqrt(2 * Math.PI) * δ)
					* Math.exp(-(i / runtime) * (i / runtime) / (2 * δ * δ));
			Particle tempbest = null; // 当前迭代中最优粒子
			Particle tempworst = null;// 当前迭代中最差粒子
			global_worst = 0;
			// Particle.w=0.9-0.5/runtimes*cnt;
			// 每个粒子更新位置和适应值
			for (int j = 0; j < pcount; j++) {
//				pars[j].setC(c1, c2);
				if (global_best > pars[j].getFitness()) {
					global_best = pars[j].getFitness();
					tempbest = pars[j];
					bestSolution=new Solution(0,global_best,pars[j].getVmTohost());
					System.out.println(i + " -> " + global_best + "    ");
					//System.out.println();
				}
				if (global_worst < pars[j].getFitness()) {
					global_worst = pars[j].getFitness();
					tempworst = pars[j];
				}// 寻找每次迭代中适应度最差的粒子
			}
			
			// 发现更好的解
			if (tempbest != null) {
				for (Vm vm : vmlist) {
					Particle.gbest[vm.getId()] = tempbest.getPos()[vm.getId()];
				}
			}
		
			for (Vm vm : vmlist) {
				for (int j = 0; j < pcount; j++) {
					pars[pcount].getPos()[vm.getId()] += pars[j].getPos()[vm.getId()];
				}
				pars[pcount].getPos()[vm.getId()] = pars[pcount].getPos()[vm.getId()]
						/ pcount;
			}// 计算粒子群位置的平均值存在在附加的粒子中
			if (tempworst != null)
				tempworst.count++;
			for (int j = 0; j < pcount; j++) {
				if (pars[j].count == Imax) {// 如果粒子最差纪录次数达到预设的次数，则对粒子进行进化
					// pars[i].updateParticle(pars[pcount]);
					for (Vm vm : vmlist) {
						pars[j].getPos()[vm.getId()] = pars[pcount].getPos()[vm.getId()];
					}
				}
				pars[j].count = 0;
			}
			ForkJoinPool forkJoinPool = new ForkJoinPool();
			try {
				forkJoinPool.submit(new ParallelParticle(0, pcount, this));
				forkJoinPool.shutdown();
				forkJoinPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("=========run end========");
		System.out.println("i="+i);
	}

	public Particle[] getParticles() {
		return pars;
	}

	public void setParticles(Particle[] particles) {
		this.pars = particles;
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	public void setBestSolution(Solution bestSolution) {
		this.bestSolution = bestSolution;
	}

	public Map<Integer, Host> getHostById() {
		return hostById;
	}

	public void setHostById(Map<Integer, Host> hostById) {
		this.hostById = hostById;
	}

	public Map<String, Vm> getVmByUid() {
		return vmByUid;
	}

	public void setVmByUid(Map<String, Vm> vmByUid) {
		this.vmByUid = vmByUid;
	}

}
