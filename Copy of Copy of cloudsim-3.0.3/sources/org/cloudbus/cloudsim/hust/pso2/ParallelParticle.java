package org.cloudbus.cloudsim.hust.pso2;

import java.util.concurrent.RecursiveAction;

public class ParallelParticle extends RecursiveAction  {
	//public class ParallelParticle   {
	private static final long serialVersionUID = 1L;
	public static final int thre=20;//阈值
	private int start;
	private int end;
	private PSO pso;
	
	public ParallelParticle(int start, int end, PSO pso) {
		this.start = start;
		this.end = end;
		this.pso = pso;
	}
	
	protected void compute() {
		if ((end - start) <= thre) {
			for (int i = start; i <end; i++) {
				pso.getParticles()[i].run();
			}
		}else{
			int middle = (start + end) / 2;
			ParallelParticle pso1=new ParallelParticle(start, middle,pso);
			ParallelParticle pso2=new ParallelParticle(middle,end,pso);
			pso1.fork();
			pso2.fork();
		}
	}
	

}
