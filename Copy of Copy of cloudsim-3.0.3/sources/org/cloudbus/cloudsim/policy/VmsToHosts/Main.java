package org.cloudbus.cloudsim.policy.VmsToHosts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.migrate.StartMigrate;
import org.cloudbus.cloudsim.migrate.SelVmMigrating;
import org.cloudbus.cloudsim.policy.VmToHost.VmAllocationPolicyRandom;
import org.cloudbus.cloudsim.policy.utils.ExtHelper;
import org.cloudbus.cloudsim.policy.utils.ExtendedConstants;
import org.cloudbus.cloudsim.policy.utils.ExtDatacenter;
import org.cloudbus.cloudsim.policy.utils.ExtDatacenterBrocker;
import org.cloudbus.cloudsim.ui.RefreshThread;

public class Main {
	private static List<Vm> vmlist;
	private static List<Host> hostList;
	public static VmAllocationPolicy instance;
	public static int brokerId;
	public static ExtDatacenterBrocker broker;
	public static Map<Integer, Long> storageMap;// 物理机固有的存储，Host类中不保存该值
	public static int userId = 5;
	private static DecimalFormat decfmt = new DecimalFormat("##.##");

	public static void init(String policyName) {
		// public static void init(){
		Log.printLine("Starting VmsToHosts...");

		try {
			int num_user = 1; // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = true; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);

			// 将输出信息保存在日志文件中
			boolean enableOutput = true;
			boolean outputToFile = true; // 控制输出到文件，默认设置为false

			// String []policyArray={"","pso","random","greedy","roundrobin"
			// ,"firstfit","combmff","comblff","ant","acopso"};//使用算法名称作为文件名
			// String policyName=policyArray[2];
			// 根据字符串名获取枚举名称
			VmAllocationPolicyEnum vmAllocPolicy = VmAllocationPolicyEnum
					.valueOf(policyName);
			// 根据枚举名称获得接口实例
			VmAllocationPolicyFactory vmAllocationFac = getPolicyByName(vmAllocPolicy);

			String experimentName = policyName.toString();
			String outputFolder = "C:\\cloudsim\\VmsToHosts\\";
			ExtHelper.initLogOutput_modify(enableOutput, outputToFile,
					outputFolder, experimentName);

			// 第二步: 创建数据中心
			int numberOfHosts = ExtendedConstants.numberOfHosts;
			hostList = ExtHelper.createHostList(numberOfHosts); // 创建物理机
			storageMap = new HashMap<Integer, Long>();
			for (Host host : hostList) {
				storageMap.put(host.getId(), host.getStorage());
			}

			// 通过工厂进行调用，传入接口实例
			ExtDatacenter datacenter = (ExtDatacenter) ExtHelper
					.createDatacenterByVmAllocationPolicyFactory("Datacenter",
							ExtDatacenter.class, hostList, vmAllocationFac);

			// 第三步: 创建数据中心代理
			broker = ExtHelper
					.createExtBrocker(ExtDatacenterBrocker.VM_ALLOCATION_MODE_LIST);
			brokerId = broker.getId();

			// 第四步: Create VMs and Cloudlets and send them to broker
			int numberOfVms = ExtendedConstants.numberOfVms;
			vmlist = ExtHelper.createVmList(brokerId, numberOfVms); // creating
																	// vms
			broker.submitVmList(vmlist);

			double start = System.currentTimeMillis();
			CloudSim.startSimulation();
			double end = System.currentTimeMillis();
			System.out.println("time: " + (end - start) / 1000 + "s");

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			printCloudletList(newList);

			Log.printLine("VmsToHosts finished!");

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	/**
	 * 通过枚举获得，工厂实例
	 * 
	 * @param vmAllocPolicy
	 * @return
	 */
	public static VmAllocationPolicyFactory getPolicyByName(
			VmAllocationPolicyEnum vmAllocPolicy) {
		VmAllocationPolicyFactory vmAllocationFac = null;
		switch (vmAllocPolicy) {
		case random:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyRandom(hostList);
				}
			};
			break;
		case firstfit:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyFirstFit(hostList);
				}
			};
			break;
		case greedy:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyGreedy(hostList);
				}
			};
			break;
		case roundrobin:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyRoundRobin(hostList);
				}
			};
			break;
		case pso:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyPSO2(hostList);
				}
			};
			break;
		case combmff:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyCombinedMostFullFirst(hostList);
				}
			};
			break;
		case comblff:
			vmAllocationFac = new VmAllocationPolicyFactory() {
				public VmAllocationPolicy create(List<? extends Host> hostList) {
					return new VmAllocationPolicyCombinedLeastFullFirst(
							hostList);
				}
			};
			break;
		default:
			System.err.println("Choose proper VM placement polilcy!");
			System.exit(1);

		}
		return vmAllocationFac;
	}

	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list
	 *            list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + indent
				+ "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.00");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}

	}

	public static Object[][] getVmToHost() {
		Object[][] data = new Object[vmlist.size()][];
		int k = 0;
		Map<Vm, ArrayList<Double>> triUtilToVm=StartMigrate.getTriUtilToVm();
		for (Vm vm : vmlist) {
			try {
				Object[] row = new Object[5];
				row[0] = vm.getId();
				row[1] = vm.getHost().getId();		
				if(triUtilToVm!=null){
					ArrayList<Double> temp=triUtilToVm.get(vm);
					row[2] =decfmt.format(temp.get(0)*100);
					row[3] = decfmt.format(temp.get(1)*100);
					row[4] = decfmt.format(temp.get(2)*100);
				}else {
					row[2] = 100;
					row[3] = decfmt.format(100 * vm.getCurrentAllocatedRam()
							/ vm.getRam());
					row[4] = decfmt.format(100 * vm.getCurrentAllocatedBw()
							/ vm.getBw());
				}
				
				data[k++] = row;
			} catch (Exception e) {
			}
		}
		return data;
	}

	public static Object[][] getVmsInHost() {
		Object[][] data = new Object[hostList.size()][];
		int k = 0;
		for (Host host : hostList) {
			try {
				Object[] row = new Object[6];
				row[0] = host.getId();
				String temp = "";
				for (Vm vm : host.getVmList()) {
					temp += vm.getId() + "  ";
				}
				row[1] = temp;
				row[2] = decfmt.format((host.getTotalMips() - host
						.getAvailableMips()) * 100 / host.getTotalMips());
				row[3] = decfmt.format(host.getRamProvisioner().getUsedRam()
						* 100 / host.getRam());
				row[4] = decfmt.format(host.getBwProvisioner().getUsedBw()
						* 100 / host.getBw());
				row[5] = decfmt.format(host.getLoad()*100);
				data[k++] = row;
			} catch (Exception e) {
			}
		}
		return data;
	}

	public static Object[][] getVmMigrate() {
		Map<Integer, List<Integer>> solu = SelVmMigrating.getSolution();
		if(solu!=null){
			Object[][] data = new Object[solu.size()][];
			Iterator<Entry<Integer, List<Integer>>> iterator = solu.entrySet().iterator();
			int k = 0;
			while (iterator.hasNext()) {
				try {
					Object[] row = new Object[3];
					Entry<Integer, List<Integer>> entry = iterator.next();
					row[0] = entry.getKey();
					row[1]= entry.getValue().get(0);
					row[2] = entry.getValue().get(1);
					data[k++] = row;
				} catch (Exception e) {
				}
			}
			return data;
		}
		return null;
	}

	public static void test() {	
		StartMigrate t1 = new StartMigrate(hostList);
		RefreshThread t2 = new RefreshThread();
		new Thread(t1).start();
		new Thread(t2).start();
	}
}