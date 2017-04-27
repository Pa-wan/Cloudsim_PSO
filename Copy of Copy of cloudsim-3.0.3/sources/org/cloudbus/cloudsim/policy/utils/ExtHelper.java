package org.cloudbus.cloudsim.policy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.policy.VmsToHosts.VmAllocationPolicyFactory;
import org.cloudbus.cloudsim.provisioners.BwProvisionerImpl;
import org.cloudbus.cloudsim.provisioners.PeProvisionerImpl;
import org.cloudbus.cloudsim.provisioners.RamProvisionerImpl;
import org.cloudbus.cloudsim.provisioners.VmSchedulerImpl;

public class ExtHelper {
private static final String LINE_SEPARATOR = System.getProperty("line.separator");
public static VmAllocationPolicy vmAllocationPolicy;	

public static Datacenter createDatacenterByVmAllocationPolicyFactory(
		String name,
		Class<? extends Datacenter> datacenterClass,
		List<Host> hostList,
		VmAllocationPolicyFactory vmAllocationFac) throws Exception {
	String arch = "x86"; // system architecture
	String os = "Linux"; // operating system
	String vmm = "Xen";
	double time_zone = 10.0; // time zone this resource located
	double cost = 3.0; // the cost of using processing in this resource
	double costPerMem = 0.05; // the cost of using memory in this resource
	double costPerStorage = 0.001; // the cost of using storage in this resource
	double costPerBw = 0.0; // the cost of using bw in this resource

	DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
			arch,
			os,
			vmm,
			hostList,
			time_zone,
			cost,
			costPerMem,
			costPerStorage,
			costPerBw);

	Datacenter datacenter = null;
	//VmAllocationPolicy vmAllocationPolicy=null;
	try {
		vmAllocationPolicy=vmAllocationFac.create(hostList);
		datacenter = datacenterClass.getConstructor(
				String.class,
				DatacenterCharacteristics.class,
				VmAllocationPolicy.class,
				List.class,
				Double.TYPE).newInstance(
				name,
				characteristics,
				vmAllocationPolicy,
				new LinkedList<Storage>(),
				Constants.SCHEDULING_INTERVAL);
	} catch (Exception e) {
		e.printStackTrace();
		System.exit(0);
	}

	return datacenter;
}

	public static List<Host> createHostList(int hostsNumber) {
        List<Host> hostList = new ArrayList<Host>();
        for (int i = 0; i < hostsNumber; i++) {
            int hostType = i % ExtendedConstants.HOST_TYPES;

            List<Pe> peList = new ArrayList<Pe>();
            for (int j = 0; j < ExtendedConstants.HOST_PES[hostType]; j++) {
                peList.add(new Pe(j, new PeProvisionerImpl(ExtendedConstants.HOST_MIPS[hostType])));
            }
            //NOTE: Use our own implementation of powerHost!
            hostList.add(new Host(
                    i,
                    new RamProvisionerImpl(ExtendedConstants.HOST_RAM[hostType]),
                    new BwProvisionerImpl(ExtendedConstants.HOST_BW),
                    ExtendedConstants.HOST_STORAGE,
                    peList,
                    new VmSchedulerImpl(peList) ));
        }
        return hostList;
    }
    public static ExtDatacenterBrocker createExtBrocker(int vmAllocationMode) {
        ExtDatacenterBrocker broker = null;
        try {
            broker = new ExtDatacenterBrocker("Broker",vmAllocationMode);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return broker;
    }
    
    public static List<Vm> createVmList(int userId, int vmsNumber) {
        List<Vm> vms = new ArrayList<Vm>();
        for (int i = 0; i < vmsNumber; i++) {
            int vmType = i / (int) Math.ceil((double) vmsNumber / ExtendedConstants.VM_TYPES);
            vms.add(new Vm(
                    i,
                    userId,
                    ExtendedConstants.VM_MIPS[vmType],
                    ExtendedConstants.VM_PES[vmType],
                    ExtendedConstants.VM_RAM[vmType],
                    ExtendedConstants.VM_BW[vmType],
                    ExtendedConstants.VM_SIZE[vmType],
                    "Xen",
                    new CloudletSchedulerDynamicWorkload(ExtendedConstants.VM_MIPS[vmType], ExtendedConstants.VM_PES[vmType])
            		));
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
		ExtHelper.enableOutput = enableOutput;
	}
	public static String getLineSeparator() {
		return LINE_SEPARATOR;
	}
}
