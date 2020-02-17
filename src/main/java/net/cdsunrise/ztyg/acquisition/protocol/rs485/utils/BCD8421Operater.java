package net.cdsunrise.ztyg.acquisition.protocol.rs485.utils;

public class BCD8421Operater {

	/**
	 * BCD字节数组===>String
	 * 
	 * @param bytes
	 * @return 十进制字符串
	 */
	public String bcd2String(byte[] bytes) {
		StringBuilder temp = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			// 高四位
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			// 低四位
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
	}

	private byte ascII2Bcd(byte asc) {
		if ((asc >= '0') && (asc <= '9')){
			return (byte) (asc - '0');
		}
		else if ((asc >= 'A') && (asc <= 'F')){
			return (byte) (asc - 'A' + 10);
		}
		else if ((asc >= 'a') && (asc <= 'f')){
			return (byte) (asc - 'a' + 10);
		}
		else{
			return (byte) (asc - 48);
		}

	}

	/**
	 * 字符串==>BCD字节数组
	 *
	 * @param str
	 * @return BCD字节数组
	 */
	public byte[] string2Bcd(String str) {
		int len = str.length();
		int mod = len % 2;

		if (mod != 0) {
			str = "0" + str;
			len = str.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = str.getBytes();
		int j, k;

		for (int p = 0; p < str.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}
}
