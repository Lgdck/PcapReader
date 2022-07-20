package lgd.Util;

public class DataUtils {

	/**
	 * ��һά���ֽ���������
	 * @param arr
	 */
	public static void reverseByteArray(byte[] arr){
		byte temp;
		int n = arr.length;
		for(int i = 0; i < n / 2; i++){
			temp = arr[i];
			arr[i] = arr[n - 1 - i];
			arr[n - 1 - i] = temp;
		}
	}

	/**
	 * byte ת int
	 * @param b
	 * @return
	 */
	public static int byteToInt (byte b) {
		return (b & 0xff);
	}
	//��λ��ǰ����λ�ں�
	public static int bytes2int(byte[] bytes){
		int result = 0;
		if(bytes.length == 4){
			int a = (bytes[0] & 0xff) << 24;//˵����
			int b = (bytes[1] & 0xff) << 16;
			int c = (bytes[2] & 0xff) << 8;
			int d = (bytes[3] & 0xff);
			result = a | b | c | d;
		}
		return result;
	}
	public static int byteArray2Int(byte[] array, int length) {
		if (length == 2) {
			return (array[0] & 0xff) * 256 + (array[1] & 0xff);
		} else if (length == 4) {
			int value= 0;
			//�ɸ�λ����λ
			for (int i = 0; i < 4; i++) {
				int shift= (4 - 1 - i) * 8;
				value +=(array[i] & 0x00FF) << shift;//����λ��
			}

			return value;
		}
		return -1;
	}
	public static long byteArray2long(byte[] array, int length) {
		if (length == 2) {
			return (array[0] & 0xff) * 256 + (array[1] & 0xff);
		} else if (length == 4) {
			long value= 0;
			//�ɸ�λ����λ
			for (int i = 0; i < 4; i++) {
				int shift= (4 - 1 - i) * 8;
				value +=(long)(array[i] & 0x00FF) << shift;//����λ��
			}

			return value;
		}
		return -1;
	}
	public static double arr2double (byte[] arr, int start) {
		int i = 0;
		int len = 8;
		int cnt = 0;
		byte[] tmp = new byte[len];
		for (i = start; i < (start + len); i++) {
			tmp[cnt] = arr[i];
//System.out.println(java.lang.Byte.toString(arr[i]) + " " + i);
			cnt++;
		}
		long accum = 0;
		i = 0;
		for ( int shiftBy = 0; shiftBy < 64; shiftBy += 8 ) {
			accum |= ( (long)( tmp[i] & 0xff ) ) << shiftBy;
			i++;
		}
		return Double.longBitsToDouble(accum);
	}
	/**
	 * byte תΪ 16 �����ַ���
	 * @param b
	 * @return
	 */
	public static String byteToHexString (byte b) {
		return intToHexString(byteToInt(b));
	}

	/**
	 * short ת 16 �����ַ���
	 * @param s
	 * @return
	 */
	public static String shortToHexString (short s) {
		String hex = intToHexString(s);
		int len = hex.length();
		if (len > 4) {	// ��ʱ short ֵΪ��ֵ����λ�Ჹ 1����� ffffed5c����˽�ȥ����λ
			hex = hex.substring(4);
		} 

		len = hex.length();
		if (len < 4) {	// ��С�� 4�����λ�� 0
			int n = 4 - len;
			for (int i = 0; i < n; i ++) {
				hex = "0" + hex;
			}
		}

		return "0x" + hex;
	}

	/**
	 * �� int תΪ 16 �����ַ���
	 * @param data
	 * @return
	 */
	public static String intToHexString (int data) {
		return Integer.toHexString(data);
	}

}