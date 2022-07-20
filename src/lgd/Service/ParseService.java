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
        //mac��
        byte[] globalHeaderBuffer = new byte[24];

        // pcap packet header: 16 bytes
        //ʱ���  ���ݰ����� ��
        byte[] packetHeaderBuffer = new byte[16];

        //������Ϣ
        byte[] packetDataBuffer;



        FileInputStream in=null;
        try {

            in=new FileInputStream(file);
            //��һ������ʱû��
            if (in.read(globalHeaderBuffer)!=24){
                System.out.println("����pcap");
            }
            int idx=0;  //���ڶԱ��һ����ʱ���  ����һ�����ʱ��ó�����1970-0-0��ƫ��  ����Ķ���ȥ���ֵ
            while (in.read(packetHeaderBuffer)>0){
                NBStructure nb=new NBStructure();
                //�������ݰ�ͷ  ��ð�����
                PacketHeader packetHeader = parsePacketHeader(packetHeaderBuffer,nb,idx++);
                packetDataBuffer = new byte[packetHeader.getCapLen()];
                if (in.read(packetDataBuffer) != packetHeader.getCapLen()) {
                    System.out.println("��ƥ������ݳ���");
                    return;
                }
                if(packetHeader.getCapLen()>1500)   continue;

                // �������ݰ�����  IP+TCP
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
        // ip���׶��ĵ�һ���ֽڵ�ǰ4λ�ǰ汾 ����λ���ײ��ĳ��ȣ���λ4�ֽڣ�
        // expect:0x45--->4:ipv4  5->20�ֽ� 69
        // �汾�̶���64���Լ�ȥ64 �� *4���ǳ���
        //��������Э�������tcp  �����DataBuffer��14  �ĳ�14+64
        int ipHeaderLen = (packetDataBuffer[14+64] - 64 ) * 4;
        byte[] ipHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 14+64, 14 +64+ ipHeaderLen);

        IPHeader ipHeader = parseIPHeader(ipHeaderBuffer);

        if (ipHeader.getProtocol() != IPHeader.PROTOCOL_TCP) {
            System.out.println("This packet is not TCP segment");
            return;
        }
        //���ip���ݰ���ʶ�ֶ� ���ṹ
        nb.setIdentify(ipHeader.getIdentify());

        // ����ƫ��λ��TCP�ֶε�13���ֽڣ�0��ʼ����ռ��4λ����λ��4�ֽ�
        int tcpHeaderLen  = ((packetDataBuffer[14+ipHeaderLen+12+64] & 0xf0) >> 4) * 4;
        byte[] tcpHeaderBuffer = Arrays.copyOfRange(packetDataBuffer, 14 + ipHeaderLen+64, 14 + ipHeaderLen + tcpHeaderLen+64);

        TCPHeader tcpHeader = parseTCPHeader(tcpHeaderBuffer);

        nb.setSrc(IPUtils.int2IPv4(ipHeader.getSrcIP()));
        nb.setDes(IPUtils.int2IPv4(ipHeader.getDstIP()));
        nb.setSeqMess(tcpHeader.getSeqMess());
        nb.setAckMess(tcpHeader.getAckMess());
        nb.setCheckSum(tcpHeader.getCheckSum()*tcpHeader.getCheckSum());  //������ƽ��  ����ֻ�ܱ�ʾ65535��

        StringBuilder sb=new StringBuilder();
        //����seq  ack  �ͱ�ʶ  ����hash�õ�id
        sb.append(nb.getSeqMess()).append(nb.getAckMess()).append(nb.getIdentify());
        long hashId = (HashAlgorithm.hash(sb.toString().getBytes()));
        nb.setId(Math.abs((int) hashId));
        //System.out.println(ipHeader);
        //System.out.println(tcpHeader);
    }
    //����TCPͷ
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
        //System.out.println(checkSum);  //��ӡУ���
        return tcpHeader;
    }
    //�������ݰ�ͷ
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
        //System.out.println(timeS+"   "+timeMs);  //��������s
        int capLen = DataUtils.byteArray2Int(capLenBuffer, 4);
        int len = DataUtils.byteArray2Int(lenBuffer, 4);
        //����ȡ����ÿһ��������ʱ��
        if(idx==0){
            initialS=timeS;
            initialMS=timeMs;
        }
          //�ò���ʱ�����
        packetHeader.setTimeS(timeS-initialS);  //��Ϊ��ֹʱ���ǵ�����ʱ��1970-1-1��0:0:0
        packetHeader.setTimeMs(timeMs-initialMS);
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(20); //����20λС��
        instance.setGroupingUsed(false); //ȡ����ѧ������
        //String TS=new StringBuilder().append(packetHeader.getTimeS()).append(".").append(packetHeader.getTimeMs()).toString();
        String TS = instance.format((packetHeader.getTimeS() + packetHeader.getTimeMs() / 1000000.0) / 1.0);
        //System.out.println("---------------"+TS);  //��s��ms ƴ�ӳɲ��ÿ�ѧ��������ʾ����ʽ

        packetHeader.setCapLen(capLen);
        packetHeader.setLen(len);
        //packetHeader.setTS(TS);

//        System.out.println(packetHeader);
        nb.settS(TS);
        return packetHeader;
    }
    //����IPͷ
    public IPHeader parseIPHeader(byte[] ipHeaderBuffer) {
        IPHeader ipHeader = new IPHeader();
        int headerLen = ipHeaderBuffer.length;
        ipHeader.setHeaderLen(headerLen);
        // �ײ������ݳ��Ⱥ�
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
