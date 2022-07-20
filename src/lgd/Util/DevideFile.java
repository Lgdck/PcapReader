package lgd.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author lgd
 * @date 2022/3/19 20:06
 */
public class DevideFile {

    private static final String SRC="212.129.237.80";
    private static final String DES="172.17.0.6";


    public static void DevideFile(File file) throws IOException {
        Scanner sc=new Scanner(file);
        FileOutputStream out=new FileOutputStream("E:\\work\\rda\\src.txt");
        //FileOutputStream out3=new FileOutputStream("E:\\work\\rda\\3.txt");
        //String a="a"+"   "+"b";
        //out3.write(a.getBytes());
        //out3.write(a.getBytes());
        FileOutputStream out2=new FileOutputStream("E:\\work\\rda\\des.txt");

        while (sc.hasNext()){
            String s = sc.nextLine();

            StringTokenizer st=new StringTokenizer(s);

            int count=0;
            String ts="";
            int id=0;
            while (st.hasMoreElements()){

                String token = st.nextToken();
                if (count==0)   id=Integer.valueOf(token);
                else if (count==1)  ts=token;
                else if (token.equals(SRC)){
                    String mess=id+"                          "+ts+"                                       "+SRC+"                                  "+DES;
                    out.write((mess+"\n").getBytes());
                    break;
                }else {
                    String mess=id+"                          "+ts+"                                       "+DES+"                                  "+SRC;
                    out2.write((mess+"\n").getBytes());
                    break;
                }
                count++;
            }
        }
        out.close();;
        out2.close();
    }

}
