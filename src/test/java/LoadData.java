import fig.basic.Pair;

import java.io.*;
import java.util.*;

/**
 * Created by yishuihan on 17-7-29.
 */
public class LoadData {
    private HashMap <String, Integer> s_i_map = null;
    private HashMap <Integer, String> i_s_map = null;

    private HashMap <Integer, ArrayList <Pair <Integer, Double>>> sparse_mat = null;
    private HashMap <String ,ArrayList <Pair <String,Double>>> sparseMatStr = null;
    public LoadData() {




    }

    public boolean buildSentencesIndexMap(String file) throws Exception {
        s_i_map = new HashMap <>();
        File rf = new File(file);
        if (!rf.isFile()) {
            System.err.println(file + " is invalid!");
            System.exit(0);
            return false;
        }
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf), "utf-8"));
        while ((line = br.readLine()) != null) {
            String[] str = line.split(",");
            if (!s_i_map.containsKey(str[0]))
                this.s_i_map.put(str[0], this.s_i_map.size());
        }
        return true;
    }


    public boolean buildSentTopNMatMap(String topN_file, HashMap <String, Integer> s_i_map, Integer Knn) throws Exception {
        sparse_mat = new HashMap <>();
        File rf = new File(topN_file);
        if (!rf.isFile()) {
            System.err.println(topN_file + " is invalid!");
            System.exit(0);
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf), "utf-8"));
        String line = "";
        ArrayList <Pair <Integer, Double>> i_topN = null;
        while ((line = br.readLine()) != null) {
            String[] str_list = line.split(",");

            Integer list0_i = s_i_map.get(str_list[0]);
            //System.out.println(str_list[0]);

            i_topN = new ArrayList <>();
            for (int i = 1; i < str_list.length && i <= Knn; i++) {
                String[] tmp = str_list[i].split(":");

                if (!s_i_map.containsKey(tmp[0])) {
                    System.err.println("N error");
                }
                Integer tmp0_i = s_i_map.get(tmp[0]);
                Double tmp1_i = Double.valueOf(tmp[1]);
                i_topN.add(new Pair <>(tmp0_i, tmp1_i));
                //System.out.println(tmp0_i+"\t"+tmp1_i);
            }
            if (!s_i_map.containsKey(str_list[0])) {
                System.err.println("0 error");
            }

            //if(i_topN.size()!=300 || str_list.length != 301);
            //System.out.println(str_list.length);
            this.sparse_mat.put(list0_i, i_topN);
            if(this.sparse_mat.size() % 1000 ==0){
                System.out.println("load point : " +this.sparse_mat.size());
            }
        }
        return true;
    }
    public boolean buildSentTopNMatMap(String topN_file, Integer Knn) throws Exception {
        sparseMatStr = new HashMap <>();
        File rf = new File(topN_file);
        if (!rf.isFile()) {
            System.err.println(topN_file + " is invalid!");
            System.exit(0);
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf), "utf-8"));
        String line = "";
        ArrayList <Pair <String, Double>> i_topN = null;
        while ((line = br.readLine()) != null) {
            String[] str_list = line.split(",");

            String list0_i = str_list[0];
            //System.out.println(str_list[0]);

            i_topN = new ArrayList <>();
            for (int i = 1; i < str_list.length && i <= Knn; i++) {
                String[] tmp = str_list[i].split(":");

                if (!s_i_map.containsKey(tmp[0])) {
                    System.err.println("N error");
                }

                Double tmp1_i = Double.valueOf(tmp[1]);
                i_topN.add(new Pair <>(tmp[0], tmp1_i));
                //System.out.println(tmp0_i+"\t"+tmp1_i);
            }
            if (!s_i_map.containsKey(str_list[0])) {
                System.err.println("0 error");
            }

            this.sparseMatStr.put(list0_i, i_topN);
            if(this.sparseMatStr.size() % 1000 ==0){
                System.out.println("load point : " +this.sparseMatStr.size());
            }
        }
        return true;
    }

    public HashMap <String, Integer> getSentencesIndexMap() {
        return this.s_i_map;
    }

    public HashMap <Integer, ArrayList <Pair <Integer, Double>>> getSparseMat() {
        return this.sparse_mat;
    }
    public HashMap<String,ArrayList<Pair<String,Double>>> getSparseMatStr(){
        return this.sparseMatStr;
    }
    public HashMap <Integer, String> getIndexSentencesMap() {
        i_s_map = new HashMap <>();
        for (Map.Entry <String, Integer> s_i : this.s_i_map.entrySet()) {
            String sent = s_i.getKey();
            Integer ind = s_i.getValue();
            this.i_s_map.put(ind, sent);
        }
        return this.i_s_map;
    }
    HashMap<String,String> s_f_map = null;
    public boolean buildSentencesLabelMap(String file) throws Exception{
        s_f_map = new HashMap <>();
        File rf = new File(file);
        if(!rf.isFile()){
            System.err.println(file+" is invalid!");
            System.exit(0);
            return false;
        }
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf),"utf-8"));
        while((line = br.readLine())!= null){
            String[] str = line.split("\t");
            if(str.length!=2)
                System.err.println("wrong labels");
            if(!s_f_map.containsKey(line))
                this.s_f_map.put(str[0],str[1]);
        }
        return true;
    }
    public HashMap<String,String> getSentLabelMap(){
        return  this.s_f_map;
    }
}
