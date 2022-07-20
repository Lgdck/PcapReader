package lgd.entity;

/**
 * TCP 包头：20 字节
 * @author lgd
 * @date 2022/3/19 18:42
 */
public class TCPHeader {
	
	/**
	 * 源端口（2 字节）
	 */
	private int srcPort;
	
	/**
	 * 目的端口（2 字节）
	 */
	private int dstPort;
	
	/**
	 * 数据报头的长度(4 bit) + 保留(4 bit) = 1 byte
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
