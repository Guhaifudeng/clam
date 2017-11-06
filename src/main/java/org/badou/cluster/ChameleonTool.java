package org.badou.cluster;

import fig.basic.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yishuihan on 17-7-28.
 */
public class ChameleonTool {

    private ConcurrentHashMap<Integer,HashMap<Integer,Double>> sparse_mat = null;
    private Double miss_value = 0.0;
    private Double threshold = -1.0;//
    private Double alpha = 1.0;//closeness / relative
    public ChameleonTool(ConcurrentHashMap<Integer,HashMap<Integer,Double>> sparse_mat){
        this.sparse_mat = sparse_mat;


    }
    public Double getMissValue() {
        return miss_value;
    }

    public void setMissVsalue(Double miss_value) {
        this.miss_value = miss_value;
    }


    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }
    public HashMap<Integer,Double> getPointTopN(Integer o){
        return this.sparse_mat.get(o);
    }
    public Double getPairCenterPointSim(int o1, int o2){

        HashMap<Integer,Double> topN_o1 = sparse_mat.get(o1);
        HashMap<Integer,Double> topN_o2 = sparse_mat.get(o2);
        if(topN_o1.containsKey(o2)){
            return topN_o1.get(o2);
        }else if(topN_o2.containsKey(o1)){
            return  topN_o2.get(o1);
        }else{
            return 0.0;
        }

    }

    public Double getPairCenterPointSim(int o1,int o2,boolean addEC){
        HashMap<Integer,Double> topN_o1 = sparse_mat.get(o1);
        HashMap<Integer,Double> topN_o2 = sparse_mat.get(o2);

        if(addEC==false){//SEC
            if(topN_o1.containsKey(o2)){
                return topN_o1.get(o2);
            }else if(topN_o2.containsKey(o1)){
                return  topN_o2.get(o1);
            }
        }else {
            if(topN_o1.containsKey(o2)&&topN_o2.containsKey(o1)){
                return topN_o1.get(o2);
            }
        }
        return 0.0;
    }
    //init sub_cluster
    public boolean ableAddPointToClusterWithThreshold(ClusterBean cluster,Integer o1){
        ArrayList<Integer> points = cluster.getPoints();
        for(Integer point: points){
            if(this.getPairCenterPointSim(point,o1)<threshold){
                return false;
            }
        }
        return true;
    }
    //absolute interconnection
    public Double calEC(ClusterBean cluster1,ClusterBean cluster2){

        ArrayList<Integer> points1 = cluster1.getPoints();
        ArrayList<Integer> points2 = cluster2.getPoints();
        Double sum_edge_weight = 0.0;
        int edge_num = 0;
        for(Integer point1:points1){
            for(Integer point2:points2){

                Double tmp = this.getPairCenterPointSim(point1,point2);
                if(tmp < 1e-12 && tmp >-1e-12){
                    continue;
                }
                sum_edge_weight += tmp;
                edge_num++;
            }
        }
        if(edge_num == 0){
            return Double.MIN_VALUE;
        }else{
            return sum_edge_weight;
        }
    }
    //absolute closeness
    public Pair<Double,Integer> calSEC(ClusterBean cluster1, ClusterBean cluster2){
//        ArrayList<Integer> points1 = new ArrayList <>();
//        points1.addAll(cluster1.getPoints());
//        ArrayList<Integer> points2 = new ArrayList <>();
//        points2.addAll(cluster2.getPoints());
        ArrayList<Integer> points1 = cluster1.getPoints();
        ArrayList<Integer> points2 = cluster2.getPoints();
        if(points1.size()==0)

            System.err.println("data error 1");
//            System.err.println(cluster1.);
        if(points2.size()==0){
            System.err.println("data error 2");
        }
        Double sum_edge_weight = 0.0;
        int edge_num = 0;
        for(Integer point1:points1){
            for(Integer point2:points2){
                Double tmp = this.getPairCenterPointSim(point1,point2);
                if(tmp < 1e-12 && tmp >-1e-12){
                    continue;
                }
                sum_edge_weight += tmp;
                edge_num++;
            }
        }
        if(edge_num == 0){
            return new Pair<Double, Integer>(Double.MIN_VALUE,1);
        }else{
            return new Pair<Double, Integer>(sum_edge_weight,edge_num);
        }
    }
    //relative interconnection
    public Double calRI(ClusterBean cluster1,ClusterBean cluster2,Pair<Double,Integer> EC_Edge){

        Double EC_1_2 = EC_Edge.getFirst();
        Double EC_1 = cluster1.getEC();
        Double EC_2 = cluster2.getEC();
        return 2*EC_1_2/(EC_1 + EC_2);
    }
    public Double calRI(ClusterBean cluster1,ClusterBean cluster2){
        Double EC_1_2 = this.calEC(cluster1,cluster2);
        Double EC_1 = cluster1.getEC();
        Double EC_2 = cluster2.getEC();
        return 2*EC_1_2/(EC_1 + EC_2);

    }
    //relative closeness
    public Double calRC(ClusterBean cluster1,ClusterBean cluster2){
        Pair<Double,Integer> SEC_pair= this.calSEC(cluster1,cluster2);
        Double SEC_1_2 = SEC_pair.getFirst()/SEC_pair.getSecond();
        Double SEC_1 = cluster1.getSEC();
        Double SEC_2 = cluster2.getSEC();
        Integer C_1 = cluster1.getPointSize();
        Integer C_2 = cluster2.getPointSize();
        Integer C_1_2 = C_1 + C_2;
        Double smooth2 = 1.0;
        Integer max_C1_C2 = Math.max(C_1,C_2);
        Integer min_C1_C2 = Math.min(C_1,C_2);
        if(SEC_pair.getSecond() / max_C1_C2 <=  min_C1_C2*0.2 ){
            smooth2 = Math.sqrt(1.0*SEC_pair.getSecond()/(C_1*C_2));
        }

        Double smooth = SEC_1_2;
        return (SEC_1_2*C_1_2)/(SEC_1*C_1+SEC_2*C_2) * smooth * smooth2;
    }
    public Double calRC(ClusterBean cluster1,ClusterBean cluster2,Pair<Double,Integer> EC_Edge){
        Pair<Double,Integer> SEC_pair= EC_Edge;
        Double SEC_1_2 = SEC_pair.getFirst()/SEC_pair.getSecond();
        Double SEC_1 = cluster1.getSEC();
        Double SEC_2 = cluster2.getSEC();
        Integer C_1 = cluster1.getPointSize();
        Integer C_2 = cluster2.getPointSize();
        Integer C_1_2 = C_1 + C_2;

        Double smooth2 = 1.0;
        Integer max_C1_C2 = Math.max(C_1,C_2);
        Integer min_C1_C2 = Math.min(C_1,C_2);
        if(SEC_pair.getSecond() / max_C1_C2 <=  min_C1_C2*0.2 ){
            smooth2 = Math.sqrt(1.0*SEC_pair.getSecond()/(C_1*C_2));
        }

        Double smooth = SEC_1_2;
        return (SEC_1_2*C_1_2)/(SEC_1*C_1+SEC_2*C_2) * smooth * smooth2;
    }
    //opt function
    public Double calFunctionDefinedOptimization(Double RI,Double RC){

        Double opt = RI * Math.pow(RC,this.alpha);
        return opt;
    }
    //merge clusters
    public ClusterBean mergeTwoClustersToOne(ClusterBean cluster1,ClusterBean cluster2,Double opt,Double RI,Double RC){
        ArrayList<Integer> point2 = cluster2.getPoints();
        Pair<Double,Integer> pair = this.calSEC(cluster1,cluster2);
        cluster1.setSEC(pair.getFirst()/pair.getSecond());
        cluster1.setEC(pair.getFirst());
        cluster1.setMergeEdgeNum(pair.getSecond());
        cluster1.setIs_merged(true);
        cluster1.setOpt(opt);
        cluster1.setRC(RC);
        cluster1.setRI(RI);
        for(Integer point:point2){
            cluster1.addPoint(point);
        }
        point2.clear();
        return cluster1;
    }

    public ClusterBean mergeTwoClustersToOne(ClusterBean cluster1,ClusterBean cluster2,Double RI,Double RC){
        ArrayList<Integer> point2 = cluster2.getPoints();
        Pair<Double,Integer> pair = this.calSEC(cluster1,cluster2);
        cluster1.setEC(pair.getFirst());
        cluster1.setSEC(pair.getFirst()/pair.getSecond());
        cluster1.setMergeEdgeNum(pair.getSecond());
        cluster1.setIs_merged(true);
        Double opt = this.calFunctionDefinedOptimization(RI,RC);
        cluster1.setOpt(opt);
        cluster1.setRC(RC);
        cluster1.setRI(RI);
        for(Integer point:point2){
            cluster1.addPoint(point);
        }
        point2.clear();
        return cluster1;
    }


}
