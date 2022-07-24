package lgd;

import lgd.Service.CalService;
import lgd.Service.ParseService;
import lgd.Util.DevideFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lgd
 * @date 2022/3/19 18:50
 */
public class ParseWirshark {
    public static void main(String[] args) throws IOException {
/*
        File pcapFile = new File("E:/工具/5g测量/周报/7-18/45/45-test1.pcap");
        FileOutputStream out=new FileOutputStream("E:\\工具\\5g测量\\周报\\7-18\\45\\45-test1.txt");
        ParseService service=new ParseService();
        service.parseFile(pcapFile,out);

*/
        File src=new File("E:\\工具\\5g测量\\周报\\724\\test6-send-12346.txt");
        File des=new File("E:\\工具\\5g测量\\周报\\724\\test6-45-12346.txt");

        CalService.calTwoFile(src,des);

        //File totalFile=new File("E:\\work\\rda\\1.txt");
        //DevideFile.DevideFile(totalFile);
    }
}
