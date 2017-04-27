package org.cloudbus.cloudsim.policy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


public class ExtendedHelper extends org.cloudbus.cloudsim.examples.power.Helper {
	 
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static List<PowerHost> createHostList(int hostsNumber) {
        List<PowerHost> hostList = new ArrayList<PowerHost>();
        for (int i = 0; i < hostsNumber; i++) {
            int hostType = i % ExtendedConstants.HOST_TYPES;

            List<Pe> peList = new ArrayList<Pe>();
            for (int j = 0; j < ExtendedConstants.HOST_PES[hostType]; j++) {
                peList.add(new Pe(j, new PeProvisionerSimple(ExtendedConstants.HOST_MIPS[hostType])));
            }
            //NOTE: Use our own implementation of powerHost!
            hostList.add(new ExtendedHost(
                    i,
                    new RamProvisionerSimple(ExtendedConstants.HOST_RAM[hostType]),
                    new BwProvisionerSimple(ExtendedConstants.HOST_BW),
                    ExtendedConstants.HOST_STORAGE,
                    peList,
                    new VmSchedulerTimeSharedOverSubscription(peList),
                    ExtendedConstants.HOST_POWER[hostType]));
        }
        return hostList;
    }
    public static ExtendedDatacenterBrocker createExtendedBrocker(int vmAllocationMode) {
        ExtendedDatacenterBrocker broker = null;
        try {
            broker = new ExtendedDatacenterBrocker("Broker",vmAllocationMode);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return broker;
    }
    public static List<Vm> createVmList(int brokerId, int vmsNumber) {
        List<Vm> vms = new ArrayList<Vm>();
        for (int i = 0; i < vmsNumber; i++) {
            int vmType = i / (int) Math.ceil((double) vmsNumber / ExtendedConstants.VM_TYPES);
            vms.add(new PowerVm(
                    i,
                    brokerId,
                    ExtendedConstants.VM_MIPS[vmType],
                    ExtendedConstants.VM_PES[vmType],
                    ExtendedConstants.VM_RAM[vmType],
                    ExtendedConstants.VM_BW[vmType],
                    ExtendedConstants.VM_SIZE[vmType],
                    1,
                    "Xen",
                    new CloudletSchedulerDynamicWorkload(ExtendedConstants.VM_MIPS[vmType], ExtendedConstants.VM_PES[vmType]),
                    ExtendedConstants.SCHEDULING_INTERVAL));
        }
        return vms;
    }
    
    /** The enable output. */
	public  static boolean enableOutput;
  
	//保存日志文件
	public  static void initLogOutput_modify(
			boolean enableOutput,  //可以输出
			boolean outputToFile,  //可以输出到文件
			String outputFolder,  //文件输出目录
			String filename  //文件名称
			) throws IOException, FileNotFoundException {
		setEnableOutput(enableOutput);
		Log.setDisabled(!isEnableOutput());
		if (isEnableOutput() && outputToFile) {
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			File folder2 = new File(outputFolder);
			if (!folder2.exists()) {
				folder2.mkdir();
			}

			File file = new File(outputFolder + getExperimentName(filename) + ".txt"); //接受可变参数
			file.createNewFile();
			Log.setOutput(new FileOutputStream(file));
		}
	}
	
	/**
	 * Gets the experiment name.
	 * 
	 * @param args the args
	 * @return the experiment name
	 */
	protected static  String getExperimentName(String... args) {
		StringBuilder experimentName = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (args[i].isEmpty()) {
				continue;
			}
			if (i != 0) {
				experimentName.append("_");
			}
			experimentName.append(args[i]);
		}
		return experimentName.toString();
	}
	public static boolean isEnableOutput() {
		return enableOutput;
	}
	public static void setEnableOutput(boolean enableOutput) {
		ExtendedHelper.enableOutput = enableOutput;
	}
	public static String getLineSeparator() {
		return LINE_SEPARATOR;
	}
}
