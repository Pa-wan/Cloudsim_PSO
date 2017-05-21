package org.cloudbus.cloudsim.ui;

import javax.swing.table.DefaultTableModel;

import org.cloudbus.cloudsim.policy.VmsToHosts.Main;

public class RefreshThread implements Runnable{
	public void run() {
        while (true){
            try {
                //刷新列表
            	GlobalObject.getjTable1().setModel(new DefaultTableModel(Main.getVmsInHost(),new String[] { "主机编号", "虚拟机编号",
    				"CPU(%)", "内存(%)", "带宽(%)", "利用率(%)" }));
            	GlobalObject.getjTable2().setModel(new DefaultTableModel(Main.getVmToHost(),new String[] { "虚拟机编号", "主机编号",
    				"CPU(%)", "内存(%)", "带宽(%)" }));
            	GlobalObject.getjTable3().setModel(new DefaultTableModel(Main.getVmMigrate(),new String[] { "待迁移虚拟机编号",
					"源主机编号","目的主机编号" }));
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
