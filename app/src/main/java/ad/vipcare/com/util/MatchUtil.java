package ad.vipcare.com.util;

import java.math.BigDecimal;

/**
 * Created by zeting
 * Date 19/1/23.
 */

public class MatchUtil {
    /**
     * 显示获取进度条进度
     * @param value1
     * @param value2
     * @param scale
     * @return
     * @throws IllegalAccessException
     */
    public static int getProgress (double value1, double value2, int scale) throws IllegalAccessException {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        if (value1 >= value2){
            return 100 ;
        } else {
            BigDecimal b1 = new BigDecimal(Double.toString(value1));
            BigDecimal b2 = new BigDecimal(Double.toString(value2));
            Double progress = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).subtract(new BigDecimal(1)).doubleValue();
            if (progress <= 0) {
                progress = 0d;
            }
            return progress.intValue() ;
        }
    }

    /**
     * 不够位数的在前面补0，保留num的长度位数字
     * @param code
     * @return
     */
    public static String autoGenericCode(String code, int num) {
        String result = "";
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        result = String.format("%0" + num + "s", code );
        return result;
    }

}
