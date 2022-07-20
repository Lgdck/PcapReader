package lgd.entity;

/**
 * TCP ��ͷ��20 �ֽ�
 * @author lgd
 * @date 2022/3/19 18:42
 */
public class TCPHeader {
	
	/**
	 * Դ�˿ڣ�2 �ֽڣ�
	 */
	private int srcPort;
	
	/**
	 * Ŀ�Ķ˿ڣ�2 �ֽڣ�
	 */
	private int dstPort;
	
	/**
	 * ���ݱ�ͷ�ĳ���(4 bit) + ����(4 bit) = 1 byte
	 */
	private int headerLen;

	private long seqMess;
	private long ackMess;

	private int checkSum;

	public int getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(int checkSum) {
		this.checkSum = checkSum;
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

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	public int getHeaderLen() {
		return headerLen;
	}

	public void setHeaderLen(int headerLen) {
		this.headerLen = headerLen;
	}

	public TCPHeader() {}

	@Override
	public String toString() {
		return "TCPHeader [srcPort=" + srcPort
				+ ", dstPort=" + dstPort
				+ ", seq=" + seqMess
				+ ", ack=" + ackMess
				+ ", headerLen=" + headerLen
				+ ", checkSum=" +checkSum
				+ "]";
	}

}
