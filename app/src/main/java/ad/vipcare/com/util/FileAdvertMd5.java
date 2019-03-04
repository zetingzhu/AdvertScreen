package ad.vipcare.com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zeting
 * Date 19/1/9.
 */

public class FileAdvertMd5 {


    /**
     * 获取文件的md5 ,来验证是否是安骑的文件
     * @param path
     * @return
     */
    public static String getMyFileMD5(String path) {
        BigInteger bi = null;
        byte[] md5Buffer ;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {

                String key = "anqi2019" ;
                md5Buffer = new byte[len + key.length()] ;
                for (int i = 0; i < len + key.length(); i++ ) {
                    if (i < len) {
                        md5Buffer[i] = buffer[len - 1 - i] ;
                    } else {
                        md5Buffer[i] = key.getBytes()[i - len] ;
                    }
                }
                md.update(md5Buffer, 0, len + key.length());
//                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

}
