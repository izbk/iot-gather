package net.cdsunrise.ztyg.acquisition.protocol.rs485.utils;


import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author: LiuWei
 * @date: Create in 9:29 2018/08/16
 */
public class Crc16Util {

    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        return StrUtil.fillBefore(Integer.toHexString(CRC).toUpperCase(),'0',4);
    }

    public static void main(String[] args) {
        System.out.println(getCRC(HexUtil.decodeHex("010400010004")));
        System.out.println(0x50&0xf0);
    }
}
