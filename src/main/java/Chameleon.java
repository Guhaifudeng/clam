
import fig.basic.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by yishuihan on 17-7-22.
 */

public class Chameleon {


    //创建辅助类
    //第一阶段 构建最小簇－初始化
    //第一阶段　设置超参数
    private HashSet <Integer> set_s = null;
    private ConcurrentHashMap <Integer, HashMap <Integer, Double>> sparse_mat = null;
    private Double miss_value = -1.0;
    private Double threshold = 0.0;//
    private Double alpha = 1.0;//closeness / relative
    private Double minRC = 0.0;
    private Double minRI = 0.0;
    private Double minMetric = 0.0;
    private ChameleonTool chameleonTool = null;
    private HashSet<ClusterBean> resultSet = null;
    private Double minInitClusterPairRC = 0.4;
    private HashMap<String,Integer> pot_s_i = null;
    private HashMap<Integer,String> pot_i_s = null;
    private Integer knn = Integer.MAX_VALUE;
    private Integer threadnum = 0;
    private Integer clusterSize = 0;

    public Chameleon(){
        this.set_s = new HashSet <>();
        this.resultSet = new HashSet <>();
    }
    public Chameleon(ConcurrentHashMap<Integer,HashMap<Integer,Double>> sparse_mat){
        this.set_s = new HashSet <>();
        this.sparse_mat = sparse_mat;
        this.resultSet = new HashSet <>();
    }
    public Chameleon(HashMap<Integer,HashMap<Integer,Double>> sparse_mat){
        this.set_s = new HashSet <>();
        this.sparse_mat.putAll(sparse_mat);
        this.resultSet = new HashSet <>();
    }


    public void initParameter(Double threshold,Double alpha,Double minMetric,Double minRC,Double minRI,Double minInitClusterPairRC){
        this.minInitClusterPairRC = minInitClusterPairRC;
        this.threshold = threshold;
        this.alpha = alpha;
        this.minMetric = minMetric;
        this.minRI = minRI;
        this.minRC = minRC;
    }
    public void initParameter(Double threshold,Double alpha,Double minRC,Double minInitClusterPairRC){
        this.threshold = threshold;
        this.alpha = alpha;
        this.minRC = minRC;
        this.minInitClusterPairRC = minInitClusterPairRC;
        this.minRI =0.0;
        this.minMetric = 0.0;
    }
    public void initParameter(Double threshold,Double minRC){
        this.threshold = threshold;
        this.alpha = 2.0;
        this.minRC = minRC;
        this.minInitClusterPairRC = 0.4;
        this.minRI =0.0;
        this.minMetric = 0.0;
    }

    public void initParameter(Double minRC){
        this.threshold = 0.6;
        this.alpha = 2.0;
        this.minRC = minRC;
        this.minInitClusterPairRC = 0.4;
        this.minRI =0.0;
        this.minMetric = 0.0;
    }

    public void setKnn(Integer knn){
        this.knn = knn;
    }
    private Integer getKnn(Integer knn){
        return this.knn;
    }

    public void setSparseMatIndIntArray(HashMap<Integer,ArrayList<Pair<Integer,Double>>> sparse_mat){
        this.changeIntMatToIntMatArray(sparse_mat);
    }
    public void setSparseMatIndStrArray(HashMap<String,ArrayList<Pair<String,Double>>> sparse_mat){
        this.changeStrMatToIntMatArray(sparse_mat);
    }
    public void setPotStrToInt(HashMap<String,Integer> pot_s_i){
        this.pot_s_i = pot_s_i;
        this.pot_i_s = new HashMap <>();
        for(Map.Entry<String,Integer> entry: this.pot_s_i.entrySet()){
            this.pot_i_s.put(entry.getValue(),entry.getKey());
        }
        if(this.pot_i_s.size() != this.pot_s_i.size()){
            System.err.println("the map from string to integer is wrong");
            System.exit(-1);
        }
    }
    private boolean changeStrMatToIntMatArray(HashMap<String,ArrayList<Pair<String,Double>>> sparse_mat){
        this.pot_i_s = new HashMap <>();
        this.pot_s_i = new HashMap <>();
        this.sparse_mat = new ConcurrentHashMap <>();
        for(String indStr: sparse_mat.keySet()){
            Integer indInt = pot_i_s.size();
            pot_i_s.put(indInt,indStr);
            pot_s_i.put(indStr,indInt);
        }

        for(Map.Entry<String,ArrayList<Pair<String,Double>>> entry: sparse_mat.entrySet()){
            String indStr = entry.getKey();
            Integer indIdInt = pot_s_i.get(indStr);
            ArrayList<Pair<String,Double>> pairs = entry.getValue();
            HashMap<Integer,Double> pot_knn = new HashMap <>();

            for(int i=0;i<pairs.size() && i< knn;i++){
                Pair<String,Double> pair = pairs.get(i);
                Integer indInt = pot_s_i.get(pair.getFirst());
                if(indInt== null)
                {
                    System.err.println("some point has no knn");
                    System.exit(-1);
                    return false;
                }
                pot_knn.put(indInt,pair.getSecond());
            }
            this.sparse_mat.put(indIdInt,pot_knn);
        }
        return true;
    }

    private boolean changeIntMatToIntMatArray(HashMap<Integer,ArrayList<Pair<Integer,Double>>> sparse_mat){
        this.sparse_mat = new ConcurrentHashMap <>();
        for(Map.Entry<Integer,ArrayList<Pair<Integer,Double>>> entry: sparse_mat.entrySet()){
            Integer indInt = entry.getKey();
            ArrayList<Pair<Integer,Double>> pairs = entry.getValue();
            HashMap<Integer,Double> pot_knn = new HashMap <>();
            for(int i=0;i<pairs.size() && i< knn;i++){
                Pair<Integer,Double> pair = pairs.get(i);
                if(pair.getFirst()== null)
                {
                    System.err.println("some point has no knn");
                    System.exit(-1);
                    return false;
                }
                pot_knn.put(pair.getFirst(),pair.getSecond());
            }
            this.sparse_mat.put(indInt,pot_knn);
        }
        return true;
    }


    public Vector<ArrayList<String>> returnResultWithStr(){
        if(this.pot_i_s ==null || this.pot_s_i ==null){
            System.err.println("has not finished the map from integer to string \n");
            System.exit(-1);
        }
        HashSet<ClusterBean> result = this.resultSet;
        ComparetorWithPotSizeDown comparetorWithPotSizeDown = new ComparetorWithPotSizeDown();
        PriorityQueue<ClusterBean> priorityQueue = new PriorityQueue <>(comparetorWithPotSizeDown);
        priorityQueue.addAll(result);
        result.clear();
        Vector<ArrayList<String>> returnVec = new Vector <>();
        while(!priorityQueue.isEmpty()){
            ClusterBean clusterBean  = priorityQueue.poll();
            ArrayList<Integer> potsInt = clusterBean.getPoints();
            ArrayList<String> potsStr = new ArrayList <>();
            for(Integer pot: potsInt){
                potsStr.add(this.pot_i_s.get(pot));
            }
            potsInt.clear();
            returnVec.add(potsStr);
        }
        return returnVec;
    }
    public Vector<ArrayList<Integer>> returnResultWithInt(){
        HashSet<ClusterBean> result = this.resultSet;
        ComparetorWithPotSizeDown comparetorWithPotSizeDown = new ComparetorWithPotSizeDown();
        PriorityQueue<ClusterBean> priorityQueue = new PriorityQueue <>(comparetorWithPotSizeDown);
        priorityQueue.addAll(result);
        result.clear();
        Vector<ArrayList<Integer>> returnVec = new Vector <>();
        while(!priorityQueue.isEmpty()){
            ClusterBean clusterBean  = priorityQueue.poll();
            ArrayList<Integer> potsInt = clusterBean.getPoints();
            returnVec.add(potsInt);
        }
        return returnVec;
    }

    private Integer getThreadnum() {
        return threadnum;
    }

    public void setThreadnum(Integer threadnum) {
        this.threadnum = threadnum;
    }

    public void outParameterSetting(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("knn\t"+this.knn+"\n");
        stringBuffer.append("stepOneT\t"+this.threshold+"\n");
        stringBuffer.append("stepTwoMin\t"+ this.minInitClusterPairRC+"\n");
        stringBuffer.append("minRC\t"+this.minRC+"\n");
        stringBuffer.append("minRI\t"+this.minRI+"\n");
        stringBuffer.append("alpha\t"+this.alpha+"\n");
        stringBuffer.append("minMetric\t"+this.minMetric+"\n");
        stringBuffer.append("threadNum\t"+ this.threadnum+"\n");
        System.out.println(stringBuffer.toString());

    }
    private boolean outLogFlag = false;
    private boolean getOutLogFlag(){
        return this.outLogFlag;
    }

    public void setOutLogFlag(boolean outLogFlag) {
        this.outLogFlag = outLogFlag;
    }

    private void outRunningLog(String outStr){
        if(this.getOutLogFlag() == false)
            return;
        System.out.print(outStr);
    }
    public void buildCluster() throws Exception{
        long startTime = System.currentTimeMillis();
        chameleonTool = new ChameleonTool(this.sparse_mat);
        chameleonTool.setMissVsalue(this.miss_value);
        chameleonTool.setThreshold(this.threshold);
        chameleonTool.setAlpha(this.alpha);
        //vec
        HashSet<ClusterBean> clusters_set = new HashSet <>();
        Queue <Integer> has_point = new PriorityQueue <>();
        ComparetorPair comparetorPair = new ComparetorPair();
        PriorityBlockingQueue <ClusterPairBean> clusters_priQueue = new PriorityBlockingQueue <>(10000, comparetorPair);
        //result
        HashSet<ClusterBean> result_set = this.resultSet;
        //step one
        has_point.addAll(this.sparse_mat.keySet());
        HashSet <Integer> point_flag = new HashSet <>();
        //build mini sub_cluster
        int size = 0;

        this.outRunningLog("has point :" + has_point.size()+"\n");


        while (!has_point.isEmpty()) {
            ClusterBean new_cluster = new ClusterBean(has_point.size(), chameleonTool);

            new_cluster.initPoint();
            Integer point = has_point.poll();
            point_flag.add(point);
            new_cluster.addPoint(point);

            if (has_point.isEmpty()) {
                size += new_cluster.getPointSize();
                new_cluster.setSEC(threshold);

                new_cluster.setAlpha(alpha);
                clusters_set.add(new_cluster);
                break;
            }

            Double max_sim = Double.MIN_VALUE;
            Integer max_o = -1;
            HashMap <Integer, Double> top_n = chameleonTool.getPointTopN(point);
            for (Map.Entry <Integer, Double> entry : top_n.entrySet()) {
                Integer o2 = entry.getKey();
                if (point_flag.contains(o2))
                    continue;
                Double tmp_sim = chameleonTool.getPairCenterPointSim(new_cluster.getPoints().get(0), o2);
                if (tmp_sim > max_sim && tmp_sim >= threshold) {
                    max_sim = tmp_sim;
                    max_o = o2;
                }
            }
            if (point_flag.contains(max_o)) {
                System.err.println("the points in sub_cluster is the same");
                System.exit(1);
            }
            if (max_sim >= threshold) {
                new_cluster.addPoint(max_o);
                point_flag.add(max_o);
                has_point.remove(max_o);

            }

            if (new_cluster.getPointSize() >3 || new_cluster.getPointSize()< 1){
                System.err.println("sub_cluster size beyond two or blow 1");
                System.exit(-1);
            }

            new_cluster.setAlpha(alpha);
            clusters_set.add(new_cluster);
            size += new_cluster.getPointSize();
        }
        this.outRunningLog("real point size = " + size+"\n");
        this.outRunningLog("first step finished " + clusters_set.size()+"\n");
        point_flag.clear();

        //step two
        HashMap <ClusterBean, ArrayList <ClusterPairBean>> c_f_map = new HashMap <>();
        Integer cluster_num = 0;
        //multi threading
        Thread t = null;
        ThreadDealt[] threadDealts = new ThreadDealt[threadnum];
        ConcurrentLinkedQueue <Pair <ClusterBean, ClusterBean>> in_queue = new ConcurrentLinkedQueue <>();
        ConcurrentLinkedQueue <ClusterPairBean> out_queue = new ConcurrentLinkedQueue <>();
        for (int i = 0; i < threadnum; i++) {
            threadDealts[i] = new ThreadDealt(clusters_priQueue, out_queue, in_queue, chameleonTool);
            t = new Thread(threadDealts[i]);
            t.setDaemon(true);
            t.start();

        }
        ThreadDealtNext threadDealtN = new ThreadDealtNext(out_queue, c_f_map);
        t = new Thread(threadDealtN);
        t.setDaemon(true);
        t.start();
        int i = 0;
        int j = 0;
        //初始化
        for (ClusterBean c1 : clusters_set) {
            j = 0;
            for (ClusterBean c2 : clusters_set) {
                if (i <= j) {
                    break;
                }
                in_queue.add(new Pair <>(c1, c2));
                while (in_queue.size() > 10000) {
                    //System.out.println("+"+in_queue.size());
                    Thread.sleep(10);
                    //System.out.println("-"+in_queue.size());
                }

                j++;
            }

            if (i % 2000 == 0)
                this.outRunningLog("cluster ind: "+ i+ "\tIMP-->" + clusters_priQueue.size()+"\n");
            i++;
        }

        while (!in_queue.isEmpty() || !out_queue.isEmpty()) {
            Thread.sleep(2);
        }
        for (i = 0; i < threadnum; i++) {
            threadDealts[i].setIn_finished(true);
        }
        threadDealtN.setIn_finished(true);
        this.outRunningLog("IMP size "+ clusters_priQueue.size()+"\n");
        //rm keys,then out

        clusters_set.removeAll(c_f_map.keySet());
        result_set.addAll(clusters_set);
        cluster_num += clusters_set.size();
        this.outRunningLog("all mini sub_cluster: " + clusters_set.size()+"\n" );
        clusters_set.clear();
        clusters_set.addAll(c_f_map.keySet());

        this.outRunningLog("delete noise point: " + clusters_set.size() + "-" + clusters_priQueue.size()+"\n");
        long endTime = System.currentTimeMillis();
        this.outRunningLog(String.format("has run time : %.1f minute\n\n", 1.0*(endTime-startTime)/1000/60));
        //heap

        ThreadDealt2[] threadDealt2s = new ThreadDealt2[threadnum];
        TemMess temMess = new TemMess(false);
        for (int ii = 0; ii < threadnum; ii++) {
            threadDealt2s[ii] = new ThreadDealt2(clusters_priQueue, out_queue, in_queue, chameleonTool, temMess);
            threadDealt2s[ii].setThrehold(minMetric, minRC, minRI);
            t = new Thread(threadDealt2s[ii]);
            t.setDaemon(true);
            t.start();
        }
        ThreadDealt2Next threadDealt2Next = new ThreadDealt2Next(out_queue, c_f_map);
        t = new Thread(threadDealt2Next);
        t.setDaemon(true);
        t.start();

        long delT = 0;
        long maxT = 0;
        long maxT2000 = 0;
        long delT2000 = 0;
        while (!clusters_set.isEmpty() && !clusters_priQueue.isEmpty()) {
            long tmp1 = System.currentTimeMillis();
            ClusterPairBean pair = clusters_priQueue.poll();
            ClusterBean c1 = pair.getC1();
            ClusterBean c2 = pair.getC2();
            if (c1.getPointSize() < c2.getPointSize()) {
                ClusterBean tmp_c = c1;
                c1 = c2;
                c2 = tmp_c;
            }

            if (!in_queue.isEmpty() || !out_queue.isEmpty()) {
                System.err.println("multi thread error");
            }
            Double opt = pair.getOpt();
            Double SEC = pair.getSEC();
            Double RC = pair.getRC();
            Double RI = pair.getRI();
            c1 = chameleonTool.mergeTwoClustersToOne(c1, c2, opt, RI, RC);

            //update opt-move
            ArrayList <ClusterPairBean> pairs1 = c_f_map.get(c1);
            ArrayList <ClusterPairBean> pairs2 = c_f_map.get(c2);

            for (ClusterPairBean cl1 : pairs1) {
                clusters_priQueue.remove(cl1);
            }
            for (ClusterPairBean cl2 : pairs2) {
                clusters_priQueue.remove(cl2);
            }
            pairs1.clear();
            pairs2.clear();

            clusters_set.remove(c2);
            clusters_set.remove(c1);

            long tmp2 = System.currentTimeMillis();
            temMess.setFlag(false);
            for (ClusterBean cluster : clusters_set) {
                in_queue.add(new Pair <>(c1, cluster));
            }

            boolean end_flag = false;
            while (!end_flag || !in_queue.isEmpty() || !out_queue.isEmpty()) {
                end_flag = true;
                for (j = 0; j < threadnum; j++) {
                    if (threadDealt2s[j].getOut_finished() == false) {
                        end_flag = false;
                        break;
                    }
                }
                if(threadDealt2Next.getOut_finished() == false){
                    end_flag = false;
                }
                Thread.sleep(1);


            }
            long tmp3 = System.currentTimeMillis();
            if (temMess.getFlag() == false) {
                cluster_num++;
                result_set.add(c1);
            } else {
                clusters_set.add(c1);
            }

            if (clusters_set.size() % 2000 == 0) {
                endTime = System.currentTimeMillis();
                this.outRunningLog("left clusters : " + clusters_set.size() + "-->MP " + clusters_priQueue.size()+"\n");
                this.outRunningLog("del time : " + delT2000+" ms\n");
                this.outRunningLog("max time : " + maxT2000+" ms\n");
                this.outRunningLog(String.format("has run time : %.1f minute\n\n", 1.0*(endTime-startTime)/1000/60));
                delT2000 = 0;
                maxT2000 = 0;
            }
            maxT += tmp3 - tmp2;
            delT += tmp2 - tmp1;
            maxT2000 += tmp3 - tmp2;
            delT2000 += tmp2 - tmp1;

        }
        for (i = 0; i < threadnum; i++) {
            threadDealt2s[i].setIn_finished(true);
        }
        threadDealt2Next.setIn_finished(true);

        this.outRunningLog("second step finished " + "-" + clusters_set.size() + "-" + clusters_priQueue.size()+"\n");
        this.outRunningLog("del time : " + delT+" ms\n");
        this.outRunningLog("max time : " + maxT+" ms\n");

        cluster_num += clusters_set.size();
        result_set.addAll(clusters_set);
        clusters_set.clear();
        this.outRunningLog("step two finished"+"\n");
        this.outRunningLog("all finished! cluster num: " + cluster_num+" "+result_set.size()+" \n");
        endTime = System.currentTimeMillis();
        this.outRunningLog(String.format("all run time : %.1f minute\n\n",1.0*(endTime-startTime)/1000/60));
    }

    public Integer getClusterSize() {
        return clusterSize;
    }

    public void setClusterSize(Integer clusterSize) {
        this.clusterSize = clusterSize;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getMinRC() {
        return minRC;
    }

    public void setMinRC(Double minRC) {
        this.minRC = minRC;
    }

    public Double getMinRI() {
        return minRI;
    }

    public void setMinRI(Double minRI) {
        this.minRI = minRI;
    }

    public Double getMinMetric() {
        return minMetric;
    }

    public void setMinMetric(Double minMetric) {
        this.minMetric = minMetric;
    }

    public Double getMinInitClusterPairRC() {
        return minInitClusterPairRC;
    }

    public void setMinInitClusterPairRC(Double minInitClusterPairRC) {
        this.minInitClusterPairRC = minInitClusterPairRC;
    }


}
