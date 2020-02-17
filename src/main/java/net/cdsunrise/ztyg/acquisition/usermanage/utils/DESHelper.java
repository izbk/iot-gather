package net.cdsunrise.ztyg.acquisition.usermanage.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;

/**
 * @ClassName: DESHelper
 * @Description: Des加密算法的工具类
 * @author Binke Zhang
 * @date 2017年4月25日 下午6:02:46
 *
 */
public class DESHelper {

    /**
     * base64+des 加密
     *
     * @Title encrypt
     * @Description
     * @param key
     * @param plaintext
     * @return
     *
     */
    public static String encrypt(String key,String plaintext){
        return Base64.getEncoder().encodeToString(encrypt(plaintext.getBytes(),key));
    }
    /**
     * des+base64 解密
     *
     * @Title decrypt
     * @Description
     * @param key
     * @param ciphertext
     * @return
     *
     */
    public static String decrypt(String key,String ciphertext){
        try {
            return new String(DESHelper.decrypt(Base64.getDecoder().decode(ciphertext), key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title encrypt
     * @Description 根据key，对数据进行加密
     * @param datasource
     * @param key
     * @return
     */
    @SuppressWarnings("Duplicates")
    public static byte[] encrypt(byte[] datasource, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            // 现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Title decrypt
     * @Description 根据key，对数据进行解密
     * @param src
     * @param key
     * @return
     * @throws Exception
     */
    @SuppressWarnings("Duplicates")
    public static byte[] decrypt(byte[] src, String key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(key.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }


    public static void main(String[] args) throws Exception{
        System.out.println(Base64.getEncoder().encodeToString(DESHelper.encrypt("76923280-05d9-49b2-b752-44c31cd7013c".getBytes(), "14964608-5e80-4b10-822b-bdcae94663fe")));
        System.out.println(Base64.getEncoder().encodeToString(DESHelper.encrypt((Calendar.getInstance().getTimeInMillis()+"").getBytes(), "14964608-5e80-4b10-822b-bdcae94663fe")));
        System.out.println(Base64.getEncoder().encodeToString(DESHelper.encrypt("admin".getBytes(), "12345678")));

        System.out.println(new String(DESHelper.decrypt(Base64.getDecoder().decode("4WUyj0U4R2+y3VOaPxz5Ig=="), "14964608-5e80-4b10-822b-bdcae94663fe")));

        //base64+des 加密解密测试
        String key = "adfafafrevvbd-mm=";
        String plaintext = "123456";
        String ciphertext = DESHelper.encrypt(key, plaintext);
        System.out.println(DESHelper.decrypt(key, ciphertext));
    }
}
