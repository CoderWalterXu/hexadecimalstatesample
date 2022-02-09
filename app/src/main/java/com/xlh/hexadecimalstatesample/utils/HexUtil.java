package com.xlh.hexadecimalstatesample.utils;

import android.util.Log;

/**
 * @author: Watler Xu
 * time:2020/7/30
 * description:
 * version:0.0.1
 */
public class HexUtil {

    public static final String TAG ="TEST";

    /**
     * 判断
     * @param mod 用户当前值
     * @param value  需要判断值
     * @return 是否存在
     */
    public static boolean hasMark(long mod, long value) {
        Log.e(TAG,"hasMark--mod："+mod+"  value:"+value);
        boolean hasMark = (mod & value) == value;
        Log.e(TAG,"hasMark--(mod & value) == value:"+hasMark);
        return (mod & value) == value;
    }

    /**
     * 增加
     * @param mod 已有值
     * @param value  需要添加值
     * @return 新的状态值
     */
    public static long addMark(long mod, long value) {
        if (hasMark(mod, value)) {
            return mod;
        }
        Log.e(TAG,"addMark--mod："+mod+"  value:"+value);
        long addMark = (mod | value);
        Log.e(TAG,"addMark--(mod | value):"+addMark);
        return (mod | value);
    }

    /**
     * 删除
     * @param mod 已有值
     * @param value  需要删除值
     * @return 新值
     */
    public static long removeMark(long mod, long value) {
        if (!hasMark(mod, value)) {
            return mod;
        }
        Log.e(TAG,"removeMark--mod："+mod+"  value:"+value);
        long removeMark = mod & (~value);
        Log.e(TAG,"removeMark--mod & (~value):"+removeMark);
        return mod & (~value);
    }

}
