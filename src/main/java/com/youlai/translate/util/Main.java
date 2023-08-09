package com.youlai.translate.util;



import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.StringUtils;

public class Main {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20230412001639043";
    private static final String SECURITY_KEY = "jSx8sdssxyzyBLmbOgbU";
    private static TransApi api = new TransApi(APP_ID, SECURITY_KEY);

    public static void main(String[] args) {

        String query = "苹果";
        String transResult = api.getTransResult(query, "zh", "en");
        JSONObject jsonObject = JSONObject.parseObject(transResult);
        JSONArray trans_result = jsonObject.getJSONArray("trans_result");
        Object dst = trans_result.get(0);
        JSONObject jsonObjectDst = JSONObject.parseObject(dst.toString());
        Object dstFinal = jsonObjectDst.get("dst");
        System.out.println(dstFinal.toString());
    }

//    public static void main(String[] args) {
//        StringBuffer stringBuffer = new StringBuffer("");
//        stringBuffer.append("\n").append("\n").append("\n").append("\n").append("\n").append("\n");
//        stringBuffer.trimToSize();
//        System.out.println(StringUtils.isEmpty(stringBuffer.toString()));
//    }

    public String translate(String s,String srcLanguage,String destLanguage){
        String transResult = api.getTransResult(s, srcLanguage, destLanguage);
        JSONObject jsonObject = JSONObject.parseObject(transResult);
        JSONArray trans_result = jsonObject.getJSONArray("trans_result");
        Object dst = trans_result.get(0);
        JSONObject jsonObjectDst = JSONObject.parseObject(dst.toString());
        Object dstFinal = jsonObjectDst.get("dst");
        return dstFinal.toString();
    }

}
