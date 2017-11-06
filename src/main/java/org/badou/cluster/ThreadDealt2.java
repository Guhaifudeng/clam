package org.badou.cluster;

import fig.basic.Pair;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ThreadDealt2 implements Runnable{
    private ConcurrentLinkedQueue<ClusterPairBean> out2_queue = null;
    private PriorityBlockingQueue<ClusterPairBean> out_queue = null;
    private boolean in_finished = false;
    private boolean out_finished =  true;
    private ConcurrentLinkedQueue<Pair<ClusterBean,ClusterBean>> in_queue = null;
    private ChameleonTool chameleonTool = null;
    private ReadWriteLock rwl = null;
    private TemMess temMess = null;
    public ThreadDealt2(PriorityBlockingQueue<ClusterPairBean> out_queue, ConcurrentLinkedQueue<ClusterPairBean> out2_queue, ConcurrentLinkedQueue<Pair<ClusterBean,ClusterBean>> in_queue,
                        ChameleonTool chameleonTool, TemMess temMess, ReentrantReadWriteLock rwl ){
        this.temMess = temMess;
        this.out_queue = out_queue;
        this.out2_queue = out2_queue;
        this.in_queue = in_queue;
        this.chameleonTool = chameleonTool;
        in_finished = false;
        this.rwl = rwl;

    }
    private Double minMetric = 0.0;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    private Double min_RC = 0.0;
    private Double min_RI = 0.0;
    private Integer index = 0;

    public void setThrehold(Double minMetric,Double min_RC,Double min_RI){
        this.minMetric = minMetric;
        this.min_RC = min_RC;
        this.min_RI = min_RI;
    }
    public void setIn_finished(boolean in_finished) {
        this.in_finished = in_finished;
    }
    public  boolean getOut_finished(){
        return  this.out_finished;
    }
    public  void setOut_finished(boolean out_finished) {
        this.out_finished = out_finished;
    }

    private int mustTwo = 0;
    public void run() {
        while (in_finished == false) {
            while (!in_queue.isEmpty()) {

                this.rwl.readLock().lock();
                Pair <ClusterBean, ClusterBean> pair = in_queue.poll();
                if (pair == null){
                    this.rwl.readLock().unlock();
                    continue;

                }
                this.setOut_finished(false);
                ClusterBean c1 = pair.getFirst();
                ClusterBean cluster = pair.getSecond();
                if (c1.getPointSize() == 0 && cluster.getPointSize() == 0){
                    System.err.println("c1-cluster " +index);
                }else if (cluster.getPointSize()==0){
                    System.err.println("cluster "+index);
                }else if (c1.getPointSize() == 0){
                    System.err.println("c1 "+ index);
                }
                Pair<Double,Integer> pair1 = chameleonTool.calSEC(c1, cluster);
                Double t_SEC = pair1.getFirst() / pair1.getSecond();
                Double t_RC = chameleonTool.calRC(c1, cluster,pair1);
                Double t_RI = chameleonTool.calRI(c1, cluster,pair1);
                Double t_opt = chameleonTool.calFunctionDefinedOptimization(t_RI, t_RC);
                if (t_opt > minMetric && t_RC > min_RC) {
                    ClusterPairBean tmp_pair = new ClusterPairBean(c1, cluster);
                    tmp_pair.setOpt(t_opt);
                    tmp_pair.setRC(t_RC);
                    tmp_pair.setRI(t_RI);
                    tmp_pair.setSEC(t_SEC);
                    out_queue.add(tmp_pair);
                    out2_queue.add(tmp_pair);

                    if(temMess.getFlag() == false)
                        temMess.setFlag(true);
                }
                this.setOut_finished(true);
                this.rwl.readLock().unlock();

            }



            }

        }
}
