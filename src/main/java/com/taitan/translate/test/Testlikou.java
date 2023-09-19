package com.taitan.translate.test;

import com.alibaba.fastjson2.JSON;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;


@Slf4j
public class Testlikou {
    public static void main(String[] args) throws Exception {

        Long begin = System.currentTimeMillis();
        System.out.println(longestPalindrome("babad"));
        Long end = System.currentTimeMillis();
        System.out.println(end-begin);

    }

    public static String longestPalindrome1(String s) {
        String results ="";
        int resultsi=0;

        for (int i = 0; i <s.length() ; i++) {
            for (int j = i+1; j <s.length()+1 ; j++) {
                String temp = s.substring(i,j);
                StringBuilder tempb = new StringBuilder(temp);
                String tempa = tempb.reverse().toString();
                if(temp.equals(tempa)){
                    if(tempb.length()>resultsi){
                        results = temp;
                        resultsi=tempb.length();
                    }
                }
            }
        }
        return results;
    }

    public static String longestPalindrome(String s) {
        StringBuilder b = new StringBuilder(s);
        String results ="";
        int resultsi=0;

        for (int i = 0; i <b.length() ; i++) {
            for (int j = i+1; j <b.length()+1 ; j++) {
                String temp = b.substring(i,j);
                StringBuilder tempb = new StringBuilder(temp);
                String tempa = tempb.reverse().toString();
                if(temp.equals(tempa)){
                    if(tempb.length()>resultsi){
                        results = temp;
                        resultsi=tempb.length();
                    }
                }
            }
        }
        return results;
    }


    public static  double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[]c = new int[nums1.length + nums2.length];
        System.arraycopy(nums1, 0, c, 0, nums1.length );
        System.arraycopy(nums2, 0, c, nums1.length,nums2.length);
        Arrays.sort(c);
        double result=0;
        int len=c.length;
        if(len%2==0){
            int temp = c[len/2-1]+c[len/2];
            result = temp/2.0;
        }else{
            result=c[(len+1)/2-1];
        }

            return result;
    }

    public static int lengthOfLongestSubstring1(String s) {

        StringBuilder b = new StringBuilder(s);
        StringBuilder c = new StringBuilder();
        int a = 0;
        for(int i=0;i<b.length();i++){
            String temp = String.valueOf(b.charAt(i));
            int index = c.indexOf(temp);
                if(index==-1){
                    c.append(b.charAt(i));
                    if(c.length()>a){
                        a=c.length();
                    }
                }else {
                    c.delete(0,index+1);
                    c.append(b.charAt(i));
                }
            }
        return a;
    }









    public static int[] twoSum2(int[] nums, int target) {
        int[] indexs = new int[2];

        // 建立k-v ，一一对应的哈希表
        HashMap<Integer,Integer> hash = new HashMap<Integer,Integer>();
        for(int i = 0; i < nums.length; i++){
            if(hash.containsKey(nums[i])){
                indexs[0] = i;
                indexs[1] = hash.get(nums[i]);
                return indexs;
            }
            // 将数据存入 key为补数 ，value为下标
            hash.put(target-nums[i],i);
        }

        return indexs;
    }

    public static int[] twoSum(int[] nums, int target) {
        int[] result = new int[2];
        for(int i=0;i<nums.length;i++){
                for (int j = i + 1; j < nums.length; j++) {
                    int b = nums[i];
                    int c = nums[j];
                    if ((b + c) == target) {
                        result[0] = i;
                        result[1] = j;
                        return result;
                    }
                }

        }
        return null;
    }
}

