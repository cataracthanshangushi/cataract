package com.youlai.translate.test;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
//        checkSignatureText("IoT -> SQS: エラーアクション(なんらかの異常時)\n: 現在状態データ");
        boolean i = checkSignatureText("I");
        boolean i1 = checkSignatureText("");
        System.out.println(i);
        System.out.println(i1);
    }

    private static boolean checkSignatureText(String signatureText) throws Exception {
        log.info("判断 字母、数字、符号");
        StringBuilder stringBuilder = new StringBuilder("");
        //校验，不支持：空格、符号、全数字  ,符号中英文符号
        char[] signatureChs=signatureText.toCharArray();
        for(char c:signatureChs){
            //48-57数字，65-90大写字母，97-122大写字母 ，0-127以内的英文字母
            if(c<127 && ( (c>47 && c<58) || (c>64 && c<91) || (c>96 && c<121) )){
                return true;
            }
        }

        String regEx = "[ _`[email protected]#$%^&*()+=|{}‘:;‘,\\[\\].<>/?~！@#￥%……&*()——+|{}【】‘；：”“’。，、？\"-]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(signatureText);
        if(m.find()){
            return true;
        }
        return false;
    }
}
