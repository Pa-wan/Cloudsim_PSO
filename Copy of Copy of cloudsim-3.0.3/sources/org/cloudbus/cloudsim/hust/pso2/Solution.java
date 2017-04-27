package org.cloudbus.cloudsim.hust.pso2;

import java.util.Map;

public class Solution {
	private int bestLoad;
	private double banlanceDegree;
	private Map<String,Integer> vmTohost;
	
	public Solution(int bestLoad, double banlanceDegree, Map<String,Integer> vmTohost) {
		this.bestLoad = bestLoad;
		this.banlanceDegree = banlanceDegree;
		this.vmTohost = vmTohost;
		
	}
	public int getBestLoad() {
		return bestLoad;
	}
	public void setBestLoad(int bestLoad) {
		this.bestLoad = bestLoad;
	}
	
	public double getBanlanceDegree() {
		return banlanceDegree;
	}
	public void setBanlanceDegree(double banlanceDegree) {
		this.banlanceDegree = banlanceDegree;
	}
	public Map<String, Integer> getVmTohost() {
		return vmTohost;
	}
	public void setVmTohost(Map<String, Integer> vmTohost) {
		this.vmTohost = vmTohost;
	}
}

