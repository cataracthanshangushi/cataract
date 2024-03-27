package com.taitan.system;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

public class lianxi {
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String a = "5";

        int count=0;
        char[] b = String.valueOf(a).toCharArray();

        for(int i=0;i<b.length;i++){
            if(b[i]=='1'){
                count++;
            }
        }

        System.out.print(count);

//        String a = in.nextLine();

//        String line = "232332323";
//        while(line.length()>8){
//
//            String result = line.substring(0,8);
//            System.out.println(result);
//            line=line.substring(8,line.length());
//
//        }
//
//        char[] re = line.toCharArray();
//
//        for(int i=re.length;i<9;i++){
//           line = line + "0";
//        }
//        System.out.println(line);

        // 注意 hasNext 和 hasNextLine 的区别
//        while (in.hasNextInt()) { // 注意 while 处理多个 case
//            String a = in.nextLine();
//            int b = in.nextInt();


//            String a = "AAATCACGGAGAAACCAGGTCAGCTGTCTTTACCTCGC";
//            int b = 19;
//            //  String result = in.nextLine();
//            char[] ch = a.toCharArray();
//            char[] hc = new char[b];
//            char[] hc2 = new char[b];
//            int begin=0;
//            int change=0;
//            for(int i=0;i<ch.length-b+1;i++){
//                change=0;
//                for(int j=0;j<b;j++){
//                    char temp=ch[i+j];
//                    if(temp=='G'||temp=='C'){
//                        change++;
//                    }
//                    hc[j]=ch[i+j];
//                }
//
//                if(change>begin){
//                    begin=change;
//                    hc2=hc.clone();
//                }
//            }
//            System.out.println(hc2);
//        }
//        Scanner scan = new Scanner(System.in);
//        while (scan.hasNext()){
//            String line1 = "1 1 5 1 5 2 4 4";
//            String[] line = line1.split(" ");
//            int count=0;
//            int copycount=0;
//            int selcount=0;
//            boolean overOp=false;
//            for(String a:line){
//                if(a.equals("1")){
//                    //a
//                    count=selcount>0?1:count+1;
//                    selcount=0;
//                }else if(a.equals("2")){
//                    //复制
//                    copycount = selcount;
//                }else if(a.equals("3")){
//                    //剪切
//                    count-=selcount;
//                    copycount=selcount;
//                    selcount=0;
//                }else if(a.equals("4")){
//                    //粘贴
//                    count-=selcount;
//                    selcount=0;
//                    count+=copycount;
//                }else if(a.equals("5")){
//                    //全选
//                    selcount = count;
//                }
//            }
//            System.out.println(count);
//        }



//        Scanner scan = new Scanner(System.in);
//        String[] line = scan.nextLine().split(",");
//        int t1=0;
//        int t2=0;
//        for(String temp:line){
//            if(Integer.parseInt(temp)==1){
//                t1++;
//            }else {
//                t2+=Math.ceil(t1);
//                t1=0;
//            }
//        }
//        t2+=Math.ceil(t1);
//        System.out.println(t2);
    }
}
