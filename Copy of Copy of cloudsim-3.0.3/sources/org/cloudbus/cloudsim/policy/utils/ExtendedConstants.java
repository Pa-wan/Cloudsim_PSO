package org.cloudbus.cloudsim.policy.utils;

import org.cloudbus.cloudsim.examples.power.Constants;

public class ExtendedConstants extends Constants {
	public final static int HOST_TYPES	 = 3;
	    public final static int[] HOST_MIPS	 = {2000,3000,1000};
	    public final static int[] HOST_PES	 = {8,6,12};
	    public final static int[] HOST_RAM	 = {128*1000,64*1000,96*1000};
	    public final static int HOST_BW		 = 100000; 
	    public final static int HOST_STORAGE = 1000000; 
	   
	    public final static int VM_TYPES	= 5;
	    public final static int[] VM_MIPS	= {500,200,300,100,400};
	    public final static int[] VM_PES	= {1,2,2,2,1};
	    public final static int[] VM_RAM	= {1*1000,4*1000,2*1000,2*1000,1*1000};
	    public final static int[] VM_BW		= {2*1000,4*1000,10*1000,2*1000,4*1000}; 
	    public final static int[] VM_SIZE	= {40*1000,100*1000,50*1000,80*1000,60*1000}; 
   
//    public final static PowerModel[] HOST_POWER = {
//            new PowerModelSpecPowerHuaweiRH2288HV2Xeon2609(),
//            new PowerModelSpecPowerHuaweiRH2285V2Xeon2450(),new PowerModelSpecPowerHuaweiRH2288HV2Xeon2609(),
//            new PowerModelSpecPowerHuaweiRH2285V2Xeon2450(),new PowerModelSpecPowerHuaweiRH2288HV2Xeon2609(),
//            new PowerModelSpecPowerHuaweiRH2285V2Xeon2450()
//    };
    
	   public static int numberOfHosts;
	   public static int numberOfVms;
	   
	   public static void setHostNum(int hostNum){
		   numberOfHosts=hostNum;
	   }
	   
	   public static void setVmNum(int vmNum){
		   numberOfVms=vmNum;
	   }
    
    
    
}

