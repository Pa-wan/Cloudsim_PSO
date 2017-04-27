package org.cloudbus.cloudsim.ui;

import org.cloudbus.cloudsim.policy.VmsToHosts.Main;
import org.cloudbus.cloudsim.policy.utils.ExtendedConstants;

public class MainFrame {
	
	public static void main(String[] args) {
		
		ExtendedConstants.setHostNum(100);//物理机数
		ExtendedConstants.setVmNum(200);//虚拟机数
		Main.init();//算法执行
	}

}
