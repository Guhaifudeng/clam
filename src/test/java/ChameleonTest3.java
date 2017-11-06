import org.badou.cluster.Chameleon;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by yishuihan on 17-10-12.
 */
public class ChameleonTest3 {
    public static void main(String args[]) throws Exception{


        String topN_file = "";
        String out = "";
        Integer threadnum = 1;
        String label = "";
        for (int i = 0; i < args.length; i += 1) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }

            if(arg.equalsIgnoreCase("mat")) {
                topN_file = args[i + 1];
            }else  if(arg.equalsIgnoreCase("out")){
                out = args[i+1];
            }else if(arg.equalsIgnoreCase("thread")){
                threadnum = Integer.valueOf(args[i+1]);
//            }else if(arg.equalsIgnoreCase("label")){
//                label = args[i+1];
            }

        }
        //核心类
        Chameleon chameleon = new Chameleon();
        //最大top值
        chameleon.setKnn(300);
        //设置参数
        chameleon.initParameter(0.4);
        //是否输出中间结果
        chameleon.setOutLogFlag(true);
        //设置线程数
        chameleon.setThreadnum(threadnum);

        LoadData3 loadData3;
        HashMap<Integer,String> i_s_map;

        HashMap<String,Integer> s_i_map;
        HashMap<Integer,HashMap<Integer,Double>> hashMap;
        Vector<ArrayList<String>> clusterBeans;
        String pre_out = out;
        loadData3 = new LoadData3();
        //传入top n近邻文件地址,构建string integer映射
        loadData3.buildSentencesIndexMap(topN_file);
        //得到映射表
        i_s_map = loadData3.getIndexSentencesMap();
        s_i_map = loadData3.getSentencesIndexMap();
        //构建最近邻
        loadData3.buildSentTopNMatMap(topN_file, s_i_map, 300);
        //得到k近邻矩阵，ｋ为最大值
        hashMap = loadData3.getSparseMat();


        for(int aa = 0;aa<1000;aa++) {
            HashMap<String,Integer> temp_s_i_map = new HashMap<>();

            HashMap<Integer,HashMap<Integer,Double>> temp_hash_map = new HashMap <>();
            temp_s_i_map.putAll(s_i_map);
            temp_hash_map.putAll(hashMap);
            //传入string integer映射,方便后面直接得到string类型的聚类结果
            chameleon.setPotStrToInt(temp_s_i_map);
            chameleon.setSparseMatIndIntMap(temp_hash_map);
            chameleon.outParameterSetting();
            try {
                chameleon.buildCluster();
            }catch (Exception e){
                e.printStackTrace();
            }
            clusterBeans = chameleon.returnResultWithStr();
            System.out.println(clusterBeans.size());
            out = pre_out+ "_minRC_" + "0.4_3_str_"+String.valueOf(aa)+"_.txt";
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"), true);
            int potAllSize = 0;
            for (ArrayList <String> arrayList : clusterBeans) {
                //            for(String pot:arrayList){
                //                pw.print(labelMap.get(pot)+"\n");
                //            }
                //            pw.print("\n");
//                for (String pot : arrayList) {
//                    pw.print(pot + "\n");
//
//                }
//                //            pw.print("\n");
//                pw.print("\n");
                potAllSize += arrayList.size();
            }
            if (potAllSize!=s_i_map.size()){
                System.err.println("some data delete");
            }
            chameleon.clearAll();

            System.out.println("size = " + clusterBeans.size());
            pw.flush();
            pw.close();
            System.out.println("main thread num" +Thread.activeCount());
//            Runtime runtime = Runtime.getRuntime();
//            runtime.gc();
//            System.out.println(runtime.totalMemory()/1024*);
            Runtime myRun = Runtime.getRuntime();
            System.out.println("已用内存mb" + myRun.totalMemory()/(1024*1024));
//            System.out.println("最大内存" + myRun.maxMemory());
//            System.out.println("可用内存" + myRun.freeMemory());
//            chameleon.clearAll();
//            loadData3.clearAll();
//            loadData3 = null;
//            hashMap = null;
//            chameleon = null;
            myRun.gc();
            Thread.sleep(10000);
            System.out.println("清理垃圾后");
            System.out.println("已用内存mb" + myRun.totalMemory()/(1024*1024));
//            System.out.println("最大内存" + myRun.maxMemory());
//            System.out.println("可用内存" + myRun.freeMemory());
        }
        System.out.println("finished all");
    }
}
