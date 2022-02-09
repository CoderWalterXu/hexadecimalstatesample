package com.xlh.hexadecimalstatesample.utils;

/**
 * @author: Watler Xu
 * time:2020/7/30
 * description: 位运算
 * version:0.0.1
 */
public class BitOperationUtil {


    /**
     * 判断
     *
     * @param mod   用户当前值
     * @param value 需要判断值
     * @return 是否存在
     */
    public static boolean hasMark(int mod, int value) {
        return (mod & value) == value;
//        return (mod & value) != 0;
    }

    /**
     * 增加
     *
     * @param mod   已有值
     * @param value 需要添加值
     * @return 新的状态值
     */
    public static int addMark(int mod, int value) {
        if (hasMark(mod, value)) {
            return mod;
        }
        return (mod | value);
    }

    /**
     * 删除
     *
     * @param mod   已有值
     * @param value 需要删除值
     * @return 新值
     */
    public static int removeMark(int mod, int value) {
        if (!hasMark(mod, value)) {
            return mod;
        }
        return mod & (~value);
    }

}
