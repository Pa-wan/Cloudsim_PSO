package org.cloudbus.cloudsim.ui;

import javax.swing.table.DefaultTableModel;

import org.cloudbus.cloudsim.policy.VmsToHosts.Main;

public class RefreshThread extends Thread{
	public void run() {
        while (true){
            try {
                //刷新列表
            	GlobalObject.getjTable().setModel(new DefaultTableModel(Main.getVmMigrate(),new String[] { "待迁移虚拟机编号",
				"目的主机编号" } ));
                Thread.sleep(20);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
