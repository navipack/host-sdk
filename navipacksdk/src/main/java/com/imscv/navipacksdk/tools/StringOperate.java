
package com.imscv.navipacksdk.tools;
/*
 * 字符串操作。一般只用于显示和打印
 */
public class StringOperate {


    /**
     * byte数组转hex字符串
     * 一个byte转为2个hex字符和一个空格
     * @param src 源数组
     * @param len 要转换的长度
     * @return 字符串 只做输出用
     */
    public static String bytes2Hex(byte[] src, int len) {
        if (len > src.length) {
            len = src.length;
        }
        char[] res = new char[len * 3];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0; i < len; i++) {
            res[j++] = hexDigits[src[i] >> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
            res[j++] = ' ';
        }

        return new String(res);
    }

    /**
     * byte数组转hex字符串
     * @param src    数据源
     * @param offset 偏移
     * @param len    长度
     * @return 字符串 只做输出用
     */
    public static String bytes2Hex(byte[] src, int offset, int len) {
        if (offset + len > src.length) {
            len = src.length - offset;
            if(len < 0)
            {
                return new String("null");
            }
        }
        char[] res = new char[len * 3];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0, j = 0, k = offset; i < len; i++, k++) {
            res[j++] = hexDigits[src[k] >> 4 & 0x0f];
            res[j++] = hexDigits[src[k] & 0x0f];
            res[j++] = ' ';
        }

        return new String(res);
    }

    /**
     * 将byte数组转为String
     * @param src 数据源
     * @param len 转换长度
     * @return 字符串
     */
    public static String byteArray2String(byte[] src, int len) {
        char[] res = new char[len * 4];

        for (int i = 0, j = 0; i < len; i++) {
            res[j++] = (char) ((src[i] / 100) + '0');
            res[j++] = (char) (((src[i] / 10) % 10) + '0');
            res[j++] = (char) ((src[i] % 10) + '0');
            res[j++] = ' ';
        }
        return new String(res);

    }

}
