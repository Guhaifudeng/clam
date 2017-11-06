package org.badou.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ThreadDealtNext implements Runnable{
    private ClusterBean c1 = null;
    private ClusterBean c2 = null;
    private boolean in_finished = false;
    private ConcurrentLinkedQueue<ClusterPairBean> inN_queue = null;
    private HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map = null;
    private ReadWriteLock rwl = null;
    public ThreadDealtNext(ConcurrentLinkedQueue<ClusterPairBean> inN_queue, HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map, ReentrantReadWriteLock rwl){
        this.inN_queue = inN_queue;
        this.in_finished = false;
        this.rwl = rwl;
        this.c_f_map = c_f_map;

    }

    public void setIn_finished(boolean in_finished) {
        this.in_finished = in_finished;
    }


    public void run() {
        this.rwl.readLock().lock();
        while (in_finished == false || !inN_queue.isEmpty()){

            ClusterPairBean clusterPairBean = inN_queue.poll();
            if(clusterPairBean ==null)
                continue;
//
            c1 = clusterPairBean.getC1();
            c2 = clusterPairBean.getC2();
            if(c_f_map.containsKey(c1)){
                ArrayList<ClusterPairBean> pairs = c_f_map.get(c1);
                pairs.add(clusterPairBean);
            }else {

                ArrayList<ClusterPairBean> pairs = new ArrayList <ClusterPairBean>();
                pairs.add(clusterPairBean);
                c_f_map.put(c1,pairs);
            }
            if(c_f_map.containsKey(c2)){
                ArrayList<ClusterPairBean> pairs = c_f_map.get(c2);
                pairs.add(clusterPairBean);
            }else {

                ArrayList<ClusterPairBean> pairs = new ArrayList <ClusterPairBean>();
                pairs.add(clusterPairBean);
                c_f_map.put(c2,pairs);
            }

        }
        this.rwl.readLock().unlock();
    }
}