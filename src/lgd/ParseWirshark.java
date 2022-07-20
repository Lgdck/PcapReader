package lgd;

import lgd.Service.ParseService;
import lgd.Util.DevideFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author lgd
 * @date 2022/3/19 18:50
 */
public class ParseWirshark {
    public static void main(String[] args) throws IOException {
        File pcapFile = new File("E:\\工具\\5g测量\\周报\\7-18\\45\\45-test1.pcap");
        ParseService service=new ParseService();
        service.parseFile(pcapFile);


        //File totalFile=new File("E:\\work\\rda\\1.txt");
        //DevideFile.DevideFile(totalFile);
    }
}
