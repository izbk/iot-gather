package net.cdsunrise.ztyg.acquisition.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import org.springframework.util.ClassUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Binke Zhang
 * @date 2019/8/29 14:51
 */

public class ZipUtils {

    public static File saveUrlAs(String url,String filePath,String fileName,String method){
        File file=new File(filePath);
        if (!file.exists()){
            file.mkdirs();
        }
        FileOutputStream fileOut = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try{
            URL httpUrl = new URL(url);
            conn=(HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // post方式不能使用缓存
            conn.setUseCaches(false);
            //连接指定的资源
            conn.connect();
            //获取网络输入流
            inputStream=conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            if (!filePath.endsWith("/")) {
                filePath += "/";
            }
            //写入到文件
            fileOut = new FileOutputStream(filePath+fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fileOut);

            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            //保存文件
            while(length != -1){
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            conn.disconnect();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("抛出异常！！");
        }
        return file;
    }

    public static void downloadFile(File file, HttpServletResponse response, boolean isDelete) {
        try {
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            // 是否删除压缩文件
            if(isDelete){
                file.delete();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static File MockCreateZip() {
        int[] names = {955,956,957,958,959,960};
        String host = "http://171.221.254.61:8900/zuul/fs/file/ewp/download/";
        // 获取springboot运行jar的路径
        String destPath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        String dirPath = destPath+ File.separator+"test";
        FileUtil.mkdir(dirPath);
        for (int i = 0;i<names.length;i++) {
            String url = host+names[i];
            String filePath = dirPath+File.separator+i+File.separator;
            String fileName = names[i]+".txt";
            saveUrlAs(url,filePath,fileName,"GET");
        }
        // 生成压缩文件
        File zipFile = ZipUtil.zip(dirPath);
        // 删除临时文件夹
        FileUtil.del(dirPath);
        return zipFile;
    }

    public static void main(String[] args) throws Exception {
        MockCreateZip();
    }
}

