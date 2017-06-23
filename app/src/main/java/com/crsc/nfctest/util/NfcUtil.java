package com.crsc.nfctest.util;

import android.content.Context;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.widget.Toast;

import com.crsc.nfctest.resource.AppResource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * NFC工具类
 */
public class NfcUtil {

    /**
     * 消息提示
     * @param context 应用上下文
     * @param text 文本内容
     */
    public static void toastMessage(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 将字节数组转换成字符串
     * @param src 字节数组
     * @return 字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串转换成字节数组
     * @param hexString 字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }


    /**
     * 将字符传换成字节
     * @param c 字符
     * @return 字节
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    /**
     * 判断标签是否可用
     * @param tagFromIntent 标签Intent
     * @return 是否可用
     */
    public static boolean ifTagEnable(Tag tagFromIntent) {
        boolean tagEnable = false;
        for (String tech : tagFromIntent.getTechList()) {
            if (tech.equals(MifareClassic.class.getName()))
                tagEnable = true;
        }
        return tagEnable;

    }

    /**
     * 根据区号获取对应块数
     * @param sector 区号
     * @return 块数
     */
    public static int getBlockNumberBySector(int sector){
        return sector < 32 ? 3 :15;
    }


    /**
     * 获取全局块号
     * @param sector 区号
     * @param blockInSector 区内块号
     * @return 全局块号
     */
    public static int blockNumber(int sector,int blockInSector){
        return sector<32 ? (sector*4+blockInSector):(32*4+(sector-32)*16+blockInSector);
    }


    /**
     * 从标签中获取数据
     * @param mContext 应用上下文
     * @param mNfcTag NFC标签对象
     * @return 标签内字节数组
     */
    public static byte[] readValuesFromTag(Context mContext, Tag mNfcTag) {
        byte[] mReadResultCache = new byte[4096];
        boolean authenticate = false;
        MifareClassic mMifareClassic = MifareClassic.get(mNfcTag);
        int sectorCount = mMifareClassic.getSectorCount();// ��ȡTAG�а�����������
        try {
            mMifareClassic.connect();
            for (int j = 0; j < sectorCount; j++) {
                authenticate = mMifareClassic.authenticateSectorWithKeyA(j,NfcUtil.hexStringToBytes(AppResource.defaultAuthenticateKey));
                int bCount;
                int bIndex;
                bCount = mMifareClassic.getBlockCountInSector(j);
                bIndex = mMifareClassic.sectorToBlock(j);
                if (authenticate) {

                    bCount = mMifareClassic.getBlockCountInSector(j);
                    bIndex = mMifareClassic.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mMifareClassic.readBlock(bIndex);
                        System.arraycopy(data, 0, mReadResultCache,bIndex * 16, data.length);
                        bIndex++;
                    }
                } else {
                    authenticate = false;
                    mReadResultCache = null;
                    break;
                }
            }
        } catch (IOException e) {
            NfcUtil.toastMessage(mContext, e.toString());
            mReadResultCache = null;
        }
        return mReadResultCache;
    }

    /**
     * 将字节数组转换成字符串
     * @param bytes 字节数组
     * @return 字符串
     */
    public static String getStringFromBytes(byte[] bytes) {
        try {
            return new String(bytes, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将字符串加0补齐成4位字符串
     * @param str 补齐字符串
     * @return 补齐后的字符串
     */
    public static String getStringFromStr(String str) {
        if (str == null)
            return "0000";
        if (str.length() == 1)
            return "000" + str;
        if (str.length() == 2)
            return "00" + str;
        if (str.length() == 3)
            return "0" + str;
        if (str.length() == 4)
            return str;
        return "0000";
    }

    /**
     * 将字符串加0补齐成2位字符串
     * @param str 补齐字符串
     * @return 补齐后的字符串
     */
    public static String getStringFrom2Str(String str) {
        if (str == null)
            return "00";
        if (str.length() == 1)
            return "0" + str;
        if (str.length() == 2)
            return str;
        return "00";
    }
}
