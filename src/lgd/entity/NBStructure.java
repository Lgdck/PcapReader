package lgd.entity;

/**
 * 牛逼的结构
 * @author lgd
 * @date 2022/3/19 18:41
 */
public class NBStructure {

    private String tS;

    private long seqMess;

    private long ackMess;

    private String src;

    private String des;

    private int id;

    private int checkSum;

    private int identify; //标识

    public int getIdentify() {
        return identify;
    }

    public void setIdentify(int identify) {
        this.identify = identify;
    }

    public long getSeqMess() {
        return seqMess;
    }

    public void setSeqMess(long seqMess) {
        this.seqMess = seqMess;
    }

    public long getAckMess() {
        return ackMess;
    }

    public void setAckMess(long ackMess) {
        this.ackMess = ackMess;
    }

    public NBStructure(String tS, long seqMess, long ackMess, String src, String des, int id, int checkSum, int identify) {
        this.tS = tS;
        this.seqMess = seqMess;
        this.ackMess = ackMess;
        this.src = src;
        this.des = des;
        this.id = id;
        this.checkSum = checkSum;
        this.identify = identify;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public NBStructure() {
    }


    public String gettS() {
        return tS;
    }

    public void settS(String tS) {
        this.tS = tS;
    }



    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
