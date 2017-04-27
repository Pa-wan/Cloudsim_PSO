package org.cloudbus.cloudsim.hust.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cloudbus.cloudsim.hust.base.PhysicalMachine;
import org.cloudbus.cloudsim.hust.base.VirtualMachine;


public class FileUtil {
	
	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * @param fileName  文件名
	 */
	public static ArrayList<double[]> readFile(String filePath,String delimeter,int n) {
		
		ArrayList<double[]> entries = new ArrayList<double[]>();
		
		File file = new File(filePath);
		BufferedReader reader = null;
		int index=0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null && index<n) {
				String[] line=tempString.split(delimeter);
				double[] entry = new double[4];
				entry[0] = Double.valueOf(line[0]);
				entry[1] = Double.valueOf(line[1]);
				entry[2] = Double.valueOf(line[2]);
				entry[3] = Double.valueOf(line[3]);
				entries.add(entry);
				index++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return entries;
	}
	
	/**
	 * 将物理机信息以指定分隔符写入指定文件
	 * @param pms  物理机列表
	 * @param filePath  文件路径
	 * @param delimeter  指定分隔符
	 * @throws IOException
	 */
	public static void writePmToFile(PhysicalMachine []pms,String filePath,String delimeter) throws IOException{
        File file=new File(filePath);
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out=new FileOutputStream(file,false); //如果追加方式用true        
        StringBuffer sb=new StringBuffer();
        for(PhysicalMachine lee:pms){
        	double []array=Utils.PmToArray(lee);
        	for(int index=0;index<3;index++){
        		sb.append(String.valueOf(array[index])).append(",");
        	}
        	sb.append(String.valueOf(array[3])).append("\n");
        }
        out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
        out.close();
    } 
	
	/**
	 * 将虚拟机信息以指定分隔符写入指定文件
	 * @param vms  虚拟机列表
	 * @param filePath  文件路径
	 * @param delimeter  指定分隔符
	 * @throws IOException
	 */
	public static void writeVmToFile(VirtualMachine []vms,String filePath,String delimeter) throws IOException{
        File file=new File(filePath);
        if(!file.exists())
            file.createNewFile();
        FileOutputStream out=new FileOutputStream(file,false); //如果追加方式用true        
        StringBuffer sb=new StringBuffer();
        for(VirtualMachine lee:vms){
        	double []array=Utils.VmToArray(lee);
        	for(int index=0;index<3;index++){
        		sb.append(String.valueOf(array[index])).append(",");
        	}
        	sb.append(String.valueOf(array[3])).append("\n");
        }
        out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
        out.close();
    } 
	
	/**
	 * 从指定路径导入物理机信息
	 * @param filePath  文件路径
	 * @param delimeter  分隔符
	 * @return 物理机列表
	 */
	public static PhysicalMachine [] loadPmFromFile(String filePath,String delimeter,int n){
		ArrayList<double[]> entries=readFile(filePath,delimeter,n);
		int index=0,pm_size=entries.size();
		PhysicalMachine [] pms=new PhysicalMachine [pm_size];
		for(double[]lee:entries){
			pms[index++]=new PhysicalMachine(lee);
		}
		return pms;
	}
	
	/**
	 * 从指定路径导入虚拟机信息
	 * @param filePath  文件路径
	 * @param delimeter  分隔符
	 * @return  虚拟机列表
	 */
	public static VirtualMachine [] loadVmFromFile(String filePath,String delimeter,int n){
		ArrayList<double[]> entries=readFile(filePath,delimeter,n);
		int index=0,vm_size=entries.size();
		VirtualMachine [] vms=new VirtualMachine [vm_size];
		for(double[]lee:entries){
			vms[index++]=new VirtualMachine(lee);
		}
		return vms;
	}
	
	/**
	 * 从指定目录中获取最新的num个文件
	 * @param path  目录路径
	 * @return  最新文件对应的路径
	 */
	public static File [] getLastModifiedFileFromDir(String dir,int num){
		//存放文件名
		File []result=new File[num];
		//定义时间格式
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		
		File file=new File(dir);
		// 定义文件修改时间
		double modify_time=-1;
		int index,size;
		
		//文件路径和修改时间的映射
		HashMap<File,Double>map=new HashMap<File,Double>();
		
		//必须为目录才行
		if(file.isDirectory()){
			//列出目录下所有文件
			File [] listFiles=file.listFiles();
			size=listFiles.length;
			
			for(index=0;index<size;index++){
				modify_time=Double.parseDouble(df.format(new Date(listFiles[index].lastModified())));
				map.put(listFiles[index],modify_time);
			}
			
			//对HashMap按照value值进行排序
			File [] sortedFiles=sortedMap(map);
			
			//从排序的map中获取指定数量的File
			result=getSortedFileByNum(sortedFiles,num);
		}
		return result;
	}
	
	//获取目录下文件名称，不带路径
	public static String []getLastModifiedFileName(File []files){
		int index,size,last;
		size=files.length;
		String []result=new String [size];
		for(index=0;index<size;index++){
			last=files[index].toString().lastIndexOf("\\");
			result[index]=files[index].toString().substring(last+1);
		}
		return result;	
		
	}
	
	public static int getNumberOfFiles(String dir){
		int length=1;
		File file=new File(dir);
		if(file.isDirectory())
			length=file.listFiles().length;
		return length;
	}
	
	public static File [] getSortedFileByNum(File[] sortedFiles,int num){
		File []files=new File[num];
		int index;
		for(index=0;index<num;index++)
			files[index]=sortedFiles[index];
        return files;
	}
	
	/**
	 * 对指定HashMap按照value值进行排序
	 * @param map
	 * @return
	 */
	public static File [] sortedMap(HashMap<File,Double>map){
		//存放运行结果
		File []result=new File[map.size()];
		
		List<Map.Entry<File,Double>> list = new ArrayList<Map.Entry<File,Double>>(map.entrySet());
		// 对HashMap中的 value 进行排序  
        Collections.sort(list, new Comparator<Map.Entry<File,Double>>() {  
            //降序排序  
            @Override  
            public int compare(Map.Entry<File,Double> o1, Map.Entry<File,Double> o2) {  
                return o2.getValue().compareTo(o1.getValue());  
            }  
        });
        
        //将降序排列的结果放入File数组
        int index=0;
        for (Map.Entry<File,Double> mapping : list) 
        	result[index++]=mapping.getKey();
		
        return result;
	}
	
	public static void printResult(ArrayList<double[]> alist){
		for(double[]lee:alist){
			print(lee);
		}
	}
	
	public static String generatePath(String dir,String suffix){
		//格式化时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
		String result=dir+df.format(new Date())+"."+suffix;
		return result;
		
	}
	public static void print(double []array){
		for(double lee:array)
			System.out.print(lee+" ");
		System.out.println();
	}
	
	public static void print(String []array){
		for(String lee:array)
			System.out.println(lee);
		System.out.println();
	}
	
	public static void printFiles(File [] files){
		for(File lee:files)
			System.out.println(lee);
		System.out.println();
	}
	
	
	public static void main(String []args){
		/**--------------------测试代码-------------------------------*/
//		//读取文件信息
//		String filePath="E:\\input1.csv";
//		String delimeter=",";
//		ArrayList<double[]> result=readFile(filePath,delimeter);
//		printResult(result);
//		
//		//导入物理机信息
//		PhysicalMachine []pms=loadPmFromFile(filePath,delimeter);
//		for(PhysicalMachine lee:pms)
//			System.out.println(lee);
		
		String dir="E:\\pm_dir";
		int num=getNumberOfFiles(dir);
		System.out.println(num);
		File [] files=getLastModifiedFileFromDir(dir,6);
		printFiles(files);
		
		String []result=getLastModifiedFileName(files);
		print(result);
		
	}

}
