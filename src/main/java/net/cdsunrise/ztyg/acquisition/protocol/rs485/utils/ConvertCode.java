package net.cdsunrise.ztyg.acquisition.protocol.rs485.utils;

/**
 * @author: LiuWei
 * @date: Create in 13:50 2018/09/17
 */
public class ConvertCode {

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytes2Str(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * @param msg
     * @return 接收字节数据并转为16进制字符串
     */
    public static String receiveHexToString(byte[] by) {
        try {
 			/*io.netty.buffer.WrappedByteBuf buf = (WrappedByteBuf)msg;
 			ByteBufInputStream is = new ByteBufInputStream(buf);
 			byte[] by = input2byte(is);*/
            String str = bytes2Str(by);
            str = str.toLowerCase();
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("接收字节数据并转为16进制字符串异常");
        }
        return null;
    }

}
