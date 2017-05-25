package org.cloudbus.cloudsim.migrate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	 /**
     * 使用FileWriter类写文本文件
     */
    public static void writeMethod1(String s)
    {
            String fileName="E:\\migrate.txt";
            try
            {
                    //使用这个构造函数时，如果存在kuka.txt文件，
                    //则先把这个文件给删除掉，然后创建新的kuka.txt
                    FileWriter writer=new FileWriter(fileName);
                    writer.write(s);
                    writer.close();
            } catch (IOException e)
            {
                    e.printStackTrace();
            }
    }
    /**
     * 使用FileWriter类往文本文件中追加信息
     */
    public static void writeMethod2(String s)
    {
            String fileName="E:\\migrate.txt";
            try
            {
                    //使用这个构造函数时，如果存在kuka.txt文件，
                    //则直接往kuka.txt中追加字符串
                    FileWriter writer=new FileWriter(fileName,true);
                    writer.write("\n"+s);
                    writer.close();
            } catch (IOException e)
            {
                    e.printStackTrace();
            }
    }
    //注意：上面的例子由于写入的文本很少，使用FileWrite类就可以了。但如果需要写入的
    //内容很多，就应该使用更为高效的缓冲器流类BufferedWriter。
    /**
     * 使用BufferedWriter类写文本文件
     */
    public static void writeMethod3(String s)
    {
            String fileName="E:/migrate.txt";
            try
            {
                    BufferedWriter out=new BufferedWriter(new FileWriter(fileName));
                    out.newLine();  //注意\n不一定在各种计算机上都能产生换行的效果
                    out.write(s);
                    out.close();
            } catch (IOException e)
            {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
    }


}
