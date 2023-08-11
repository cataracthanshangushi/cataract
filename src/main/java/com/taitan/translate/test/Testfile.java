package com.taitan.translate.test;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Testfile {
    public static void main(String[] args) throws Exception {
//        checkSignatureText("IoT -> SQS: エラーアクション(なんらかの異常時)\n: 現在状態データ");
        String basePath = "D:\\个人测试\\翻译前";
        File dir = new File(basePath);
        List<File> allFileList = new ArrayList<>();
        // 判断文件夹是否存在
        if (!dir.exists()) {
            System.out.println("目录不存在");
            return;
        }
        getAllFile(dir, allFileList);

        for (File file : allFileList) {
            System.out.println(file.getParentFile());
            System.out.println(file.getAbsolutePath());
            new File(file.getParentFile().toString().replace("翻译前","翻译后")).mkdirs();
            System.out.println(file.getAbsolutePath().replace("翻译前","翻译后"));
        }

    }

    public static void getAllFile(File fileInput, List<File> allFileList) {
        // 获取文件列表
        File[] fileList = fileInput.listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                // 递归处理文件夹
                // 如果不想统计子文件夹则可以将下一行注释掉
                getAllFile(file, allFileList);
            } else {
                // 如果是文件则将其加入到文件数组中
                allFileList.add(file);
            }
        }
    }
}
