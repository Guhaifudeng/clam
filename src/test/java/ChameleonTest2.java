import fig.basic.Pair;
import org.badou.cluster.Chameleon;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ChameleonTest2 {
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
        chameleon.initParameter(0.4);
        chameleon.setOutLogFlag(true);
        chameleon.setThreadnum(threadnum);
        LoadData2 loadData2 = new LoadData2();
        loadData2.buildSentencesIndexMap(topN_file);
        HashMap<Integer,String> i_s_map = loadData2.getIndexSentencesMap();
        HashMap<String,Integer> s_i_map = loadData2.getSentencesIndexMap();
        loadData2.buildSentTopNMatMap(topN_file,s_i_map,300);

        HashMap<Integer,ArrayList<Pair<Integer,Double>>> hashMap = loadData2.getSparseMat();
//        loadData2.buildSentTopNMatMap(topN_file,300);
//        HashMap<String,ArrayList<Pair<String,Double>>> hashMap = loadData.getSparseMatStr();
//        loadData.buildSentencesLabelMap(label);
//        HashMap<String,String> labelMap = loadData.getSentLabelMap();

//        chameleon.setSparseMatIndStrArray(hashMap);
//        hashMap =
        chameleon.setPotStrToInt(s_i_map);
        chameleon.setSparseMatIndIntArray(hashMap);

        chameleon.outParameterSetting();
        chameleon.buildCluster();
//        Vector<ArrayList<String>> clusterBeans = chameleon.returnResultWithStr();
        Vector<ArrayList<Integer>> clusterBeans = chameleon.returnResultWithInt();
        System.out.println(clusterBeans.size());
        out += "_minRC_"+"0.4_2.txt";
        PrintWriter pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream(out),"utf-8"),true);
        for(ArrayList<Integer> arrayList:clusterBeans){
//            for(String pot:arrayList){
//                pw.print(labelMap.get(pot)+"\n");
//            }
//            pw.print("\n");
            for(Integer pot: arrayList){
                pw.print(i_s_map.get(pot)+"\n");

            }
//            pw.print("\n");
            pw.print("\n");
        }

        System.out.println("size = " + clusterBeans.size());
        pw.flush();
        pw.close();
    }

}
