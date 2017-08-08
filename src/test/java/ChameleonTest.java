import fig.basic.Pair;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ChameleonTest {
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
            }else if(arg.equalsIgnoreCase("label")){
                label = args[i+1];
            }

        }

        Chameleon chameleon = new Chameleon();
        chameleon.setKnn(100);
        chameleon.initParameter(0.6,0.5);
        chameleon.setOutLogFlag(true);
        chameleon.setThreadnum(threadnum);
        LoadData loadData = new LoadData();
        loadData.buildSentencesIndexMap(topN_file);
//        HashMap<Integer,String> i_s_map = loadData.getIndexSentencesMap();
//        HashMap<String,Integer> s_i_map = loadData.getSentencesIndexMap();
//        loadData.buildSentTopNMatMap(topN_file,s_i_map,100);
//
//        HashMap<Integer,ArrayList<Pair<Integer,Double>>> hashMap = loadData.getSparseMat();
        loadData.buildSentTopNMatMap(topN_file,100);
        HashMap<String,ArrayList<Pair<String,Double>>> hashMap = loadData.getSparseMatStr();
        loadData.buildSentencesLabelMap(label);
        HashMap<String,String> labelMap = loadData.getSentLabelMap();

        chameleon.setSparseMatIndStrArray(hashMap);

        chameleon.outParameterSetting();
        chameleon.buildCluster();
        Vector<ArrayList<String>> clusterBeans = chameleon.returnResultWithStr();
        System.out.println(clusterBeans.size());
        out += "_threshold_0.60_minRC_"+"0.5.txt";
        PrintWriter pw = new PrintWriter( new OutputStreamWriter( new FileOutputStream(out),"utf-8"),true);
        for(ArrayList<String> arrayList:clusterBeans){
            for(String pot:arrayList){
                pw.print(labelMap.get(pot)+"\t");
            }
            pw.print("\n");
            for(String pot: arrayList){
                pw.print(pot+"\t");

            }
            pw.print("\n");
            pw.print("\n");
        }

        System.out.println("size = " + clusterBeans.size());
        pw.flush();
        pw.close();
    }

}
