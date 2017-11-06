import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yishuihan on 17-10-12.
 */
public class LoadData3 {
    private HashMap<String ,Integer> s_i_map = null;
    private HashMap<Integer ,String> i_s_map = null;

    HashMap<Integer,HashMap<Integer,Double>> sparse_mat = null;
    public LoadData3(){
        s_i_map = new HashMap <>();
        i_s_map = new HashMap <>();
        sparse_mat = new HashMap <>();

    }
    public void clearAll(){
        this.s_i_map.clear();
        s_i_map = null;
        this.i_s_map.clear();
        i_s_map = null;
        this.sparse_mat.clear();
        sparse_mat = null;
    }

    public HashMap<Integer,HashMap<Integer,Double>> getSparseMat(){
        return  this.sparse_mat;
    }
    public boolean buildSentTopNMatMap(String topN_file,HashMap<String,Integer> s_i_map,Integer Knn) throws Exception{
//        HashSet<Integer> all_point = new HashSet <>();
//        all_point.addAll(s_i_map.values());
        File rf = new File(topN_file);
        if(!rf.isFile()){
            System.err.println(topN_file+" is invalid!");
            System.exit(0);
            return false;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf),"utf-8"));
        String line = "";
        HashMap<Integer ,Double> i_topN = null;
        Integer list0_i = 0;
        int sent_count = 0;
        boolean is_first = true;
        int line_num = 0;
        i_topN = new HashMap <>();
        while((line = br.readLine())!=null){


            String[] str_list = line.split("\t");
            if(str_list.length == 1){
                if(str_list[0].length() ==0) {
                    sent_count++;
                    if (sent_count % 5000 == 0) {
                        System.out.println("dealt sents: " + sent_count);
                    }
                    is_first = true;
                    if(sparse_mat.containsKey(list0_i)){
                        System.err.println(list0_i);
                    }
                    sparse_mat.put(list0_i, i_topN);

                }else{
                    if(!s_i_map.containsKey(str_list[0])) {
//                        System.out.println(str_list[0]);
//                        System.err.println("sentences error");
                    }
                    else {
                        list0_i = s_i_map.get(str_list[0]);
                        //System.out.println(str_list[0]);
                        i_topN = new HashMap <>();
                        line_num = 0;
                    }
                }

            }else if(str_list.length == 2) {

                if(is_first){
                    if(s_i_map.get(str_list[0]) != list0_i){
                        System.err.println(this.i_s_map.get(list0_i));

                    }
                    is_first = false;
                    continue;
                }
//                if(!s_i_map.containsKey(str_list[0])){
////                    System.out.println(str_list[0]);
//                    System.err.println("sentences error");
//                    continue;
//                }
                Integer tmp0_i = s_i_map.get(str_list[0]);
                Double  tmp1_i = Double.valueOf(str_list[1]);
                if(tmp1_i <1e-6)
                    continue;
//                tmp1_i = (tmp1_i+1.0)/2;

                if(i_topN.size()< Knn)
                    i_topN.put(tmp0_i,tmp1_i);
                line_num++;
            }else {
                System.err.println("data format wrong");
            }
        }
        System.out.println("dealt sents: "+ sent_count);
        System.out.println("sent knn mat: "+ sparse_mat.size());
        return true;
    }
    public boolean buildSentencesIndexMap(String file) throws Exception{
        File rf = new File(file);
        if(!rf.isFile()){
            System.err.println(file+" is invalid!");
            System.exit(0);
            return false;
        }
        String line = "";
        int line_count_e = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rf),"utf-8"));
//        PriorityQueue<String> priorityQueue = new PriorityQueue <>();
        while((line = br.readLine())!= null){
            if(line.equals("") || line.equals(" "))
            {
                line_count_e++;
                continue;
            }
            String[] str = line.split("\t");
            String line1 = str[0];
            if(!s_i_map.containsKey(line1)&&line1.length()>0){
                this.s_i_map.put(line1,this.s_i_map.size());
            }
            else if(s_i_map.containsKey(line1)&&line1.length()>0 && str.length ==1){
//                if(line1.length()>0 && s_i_map.containsKey(line1) && str.length == 1)
//                    priorityQueue.add("\t"+line);
//                System.out.println("hello"+line1);
            }
        }
//        while (!priorityQueue.isEmpty()){
//            System.out.println(priorityQueue.poll());
//        }
        System.out.println("empty_line_e: "+ line_count_e);
        System.out.println("set_sent_size :"+ s_i_map.size());
        return true;
    }


    public HashMap<Integer,String> getIndexSentencesMap(){

        for(Map.Entry<String,Integer>s_i :this.s_i_map.entrySet()){
            String sent = s_i.getKey();
            Integer ind = s_i.getValue();
            this.i_s_map.put(ind, sent);
        }
        return this.i_s_map;
    }
    public HashMap<String,Integer> getSentencesIndexMap(){
        return this.s_i_map;
    }
}
