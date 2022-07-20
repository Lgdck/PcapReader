package lgd.Service;

import lgd.Util.DataUtils;
import lgd.Util.HashAlgorithm;
import lgd.Util.IPUtils;
import lgd.entity.IPHeader;
import lgd.entity.NBStructure;
import lgd.entity.PacketHeader;
import lgd.entity.TCPHeader;

import java.io.*;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * @author lgd
 * @date 2022/3/19 18:51
 */
public class ParseService {

    private static int initialS=0;
    private static int initialMS=0;

    public void parseFile(File file,OutputStream out) throws FileNotFoundException {
        //mac层
        byte[] globalHeaderBuffer = new byte[24];

        // pcap packet header: 16 bytes
        //时间戳  数据包长度 等
        byte[] packetHeaderBuffer = new byte[16];

        //报文信息
        byte[] packetDataBuffer;



        FileInputStream in=null;
        try {

            in=new FileInputStream(file);
            //这一部分暂时没用
            if (in.read(globalHeaderBuffer)!=24){
                System.out.println("坏的pcap");
            }
            int idx=0;  //用于对标第一个的时间戳  即第一个算的时候得出距离1970-0-0的偏差  后面的都减去这个值
            while (in.read(packetHeaderBuffer)>0){
                NBStructure nb=new NBStructure();
                //解析数据包头  获得包长度
                PacketHeader packetHeader = parsePacketHeader(packetHeaderBuffer,nb,idx++);
                packetDataBuffer = new byte[packetHeader.getCapLen()];
                if (in.read(packetDataBuffer) != packetHeader.getCapLen()) {
                    System.out.println("不匹配的数据长度");
                    return;
                }
                if(packetHeader.getCapLen()>1500)   continue;

                // 解析数据包数据  IP+TCP
                parsePacketData(packetDataBuffer,nb);
                String mess = nb.getId() + "                            " + nb.gettS() + "                                          " + nb.getSrc() + "                         " + nb.getDes();
                out.write((mess+"\n").getBytes());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void parsePacketData(byte[] packetDataBuffer,NBStructure nb) {
        // ip包首都的第一个字节的前4位是版本 后四位是首部的长度（单位4字节）
        // expect:0x45--->4:ipv4  5->20字节 69
        // 版本固定是64所以减去64 再 *4就是长度
        //如果是隧道协议包含的tcp  这里的DataBuffer由14  改成14+64
        int ipHeaderLen = (packetDataBuffer[14+64] - 64 ) * 4;
        byte[] ipHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 14+64, 14 +64+ ipHeaderLen);

        IPHeader ipHeader = parseIPHeader(ipHeaderBuffer);

        if (ipHeader.getProtocol() != IPHeader.PROTOCOL_TCP) {
            System.out.println("This packet is not TCP segment");
            return;
        }
        //添加ip数据包标识字段 到结构
        nb.setIdentify(ipHeader.getIdentify());

        // 数据偏移位于TCP字段第13个字节（0开始），占高4位，单位是4字节
        int tcpHeaderLen  = ((packetDataBuffer[14+ipHeaderLen+12+64] & 0xf0) >> 4) * 4;
        byte[] tcpHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 14 + ipHeaderLen+64, 14 + ipHeaderLen + tcpHeaderLen+64);

        TCPHeader tcpHeader = parseTCPHeader(tcpHeaderBuffer);

        nb.setSrc(IPUtils.int2IPv4(ipHeader.getSrcIP()));
        nb.setDes(IPUtils.int2IPv4(ipHeader.getDstIP()));
        nb.setSeqMess(tcpHeader.getSeqMess());
        nb.setAckMess(tcpHeader.getAckMess());
        nb.setCheckSum(tcpHeader.getCheckSum()*tcpHeader.getCheckSum());  //这里用平方  否则只能表示65535个

        StringBuilder sb=new StringBuilder();
        //利用seq  ack  和标识  进行hash得到id
        sb.append(nb.getSeqMess()).append(nb.getAckMess()).append(nb.getIdentify());
        long hashId = (HashAlgorithm.hash(sb.toString().getBytes()));
        nb.setId(Math.abs((int) hashId));
        //System.out.println(ipHeader);
        //System.out.println(tcpHeader);
    }
    //解析TCP头
    private TCPHeader parseTCPHeader(byte[] tcpHeaderBuffer) {
        // headerLen
        TCPHeader tcpHeader = new TCPHeader();
        tcpHeader.setHeaderLen(tcpHeaderBuffer.length);

        // sport and dport
        byte[] srcPortBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 0, 2);
        byte[] dstPortBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 2, 4);
        byte[] synBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 4, 8);
        byte[] ackBuffer = Arrays.copyOfRange(tcpHeaderBuffer, 8, 12);
        byte[] checkSumBuffer=Arrays.copyOfRange(tcpHeaderBuffer,16,18);
        int srcPort = DataUtils.byteArray2Int(srcPortBuffer, 2);
        int dstPort = DataUtils.byteArray2Int(dstPortBuffer, 2);
        long seqmess = DataUtils.byteArray2long(synBuffer, 4);
        long ackmess = DataUtils.byteArray2long(ackBuffer, 4);
        System.out.println(seqmess +"   "+ ackmess);
        int checkSum=DataUtils.byteArray2Int(checkSumBuffer,2);
        tcpHeader.setSrcPort(srcPort);
        tcpHeader.setDstPort(dstPort);
        tcpHeader.setSeqMess(seqmess);
        tcpHeader.setAckMess(ackmess);
        tcpHeader.setCheckSum(checkSum);
        //System.out.println(checkSum);  //打印校验和
        return tcpHeader;
    }
    //解析数据包头
    private PacketHeader parsePacketHeader(byte[] dataHeaderBuffer,NBStructure nb,int idx){

        byte[] timeSBuffer = Arrays.copyOfRange(dataHeaderBuffer, 0, 4);
        byte[] timeMsBuffer = Arrays.copyOfRange(dataHeaderBuffer, 4, 8);
        byte[] capLenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 8, 12);
        byte[] lenBuffer = Arrays.copyOfRange(dataHeaderBuffer, 12, 16);

        PacketHeader packetHeader = new PacketHeader();

        DataUtils.reverseByteArray(timeSBuffer);
        DataUtils.reverseByteArray(timeMsBuffer);
        DataUtils.reverseByteArray(capLenBuffer);
        DataUtils.reverseByteArray(lenBuffer);

        int timeS = DataUtils.byteArray2Int(timeSBuffer, 4);
        int timeMs = DataUtils.byteArray2Int(timeMsBuffer, 4);
        //System.out.println(timeS+"   "+timeMs);  //看距今多少s
        int capLen = DataUtils.byteArray2Int(capLenBuffer, 4);
        int len = DataUtils.byteArray2Int(lenBuffer, 4);
        //这里取决于每一个机器的时钟
        if(idx==0){
            initialS=timeS;
            initialMS=timeMs;
        }
          //用不到时间戳了
        packetHeader.setTimeS(timeS-initialS);  //因为截止时间是到格林时间1970-1-1的0:0:0
        packetHeader.setTimeMs(timeMs-initialMS);
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(20); //保留20位小数
        instance.setGroupingUsed(false); //取消科学计数法
        //String TS=new StringBuilder().append(packetHeader.getTimeS()).append(".").append(packetHeader.getTimeMs()).toString();
        String TS = instance.format((packetHeader.getTimeS() + packetHeader.getTimeMs() / 1000000.0) / 1.0);
        //System.out.println("---------------"+TS);  //将s和ms 拼接成不用科学计数法表示的形式

        packetHeader.setCapLen(capLen);
        packetHeader.setLen(len);
        //packetHeader.setTS(TS);

//        System.out.println(packetHeader);
        nb.settS(TS);
        return packetHeader;
    }
    //解析IP头
    public IPHeader parseIPHeader(byte[] ipHeaderBuffer) {
        IPHeader ipHeader = new IPHeader();
        int headerLen = ipHeaderBuffer.length;
        ipHeader.setHeaderLen(headerLen);
        // 首部和数据长度和
        byte[] totalLenBuffer = Arrays.copyOfRange(ipHeaderBuffer, 2, 4);
        byte[] identifyBuffer = Arrays.copyOfRange(ipHeaderBuffer, 4, 6);
        int totalLen = DataUtils.byteArray2Int(totalLenBuffer, 2);
        int identify=DataUtils.byteArray2Int(identifyBuffer,2);
        //System.out.println(identify+" ******");
        ipHeader.setTotalLen(totalLen);
        ipHeader.setIdentify(identify);
        // upper protocol
        // 6 represents tcp
        int protocol = DataUtils.byteToInt(ipHeaderBuffer[9]);
        ipHeader.setProtocol(protocol);

        // parse sip and dip
        byte[] srcIPBuffer = Arrays.copyOfRange(ipHeaderBuffer, 12, 16);
        byte[] dstIPBuffer = Arrays.copyOfRange(ipHeaderBuffer, 16, 20);
        int srcIP = DataUtils.byteArray2Int(srcIPBuffer, 4);
        int dstIP = DataUtils.byteArray2Int(dstIPBuffer, 4);
        ipHeader.setSrcIP(srcIP);
        ipHeader.setDstIP(dstIP);
        return ipHeader;
    }
}
