package com.book.record.java8Logic.encode;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis
 * @data 2022-07-25
 * @description :
 */
public class encoding_Unicode {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str    = "ÀÏÂí";
        String newStr = new String(str.getBytes("windows-1252"), "GB18030");
        System.out.println(newStr);
        recover(str);
    }


    /**
     * 恢复乱码
     * @param str
     * @throws UnsupportedEncodingException
     */
    public static void recover(String str)
            throws UnsupportedEncodingException {
        String[] charsets = new String[]{
                "windows-1252", "GB18030", "Big5", "UTF-8"};
        for (int i = 0; i < charsets.length; i++) {
            for (int j = 0; j < charsets.length; j++) {
                if (i != j) {
                    String s = new String(str.getBytes(charsets[i]), charsets[j]);
                    System.out.println("----原来的编码(A)假设是: "
                            + charsets[j] + ", 被错误解析为(B): " + charsets[i]);
                    System.out.println(s);
                    System.out.println();
                }
            }
        }
    }
}

