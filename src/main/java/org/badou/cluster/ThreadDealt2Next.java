package org.badou.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ThreadDealt2Next implements Runnable{
    private ClusterBean c1 = null;
    private ClusterBean c2 = null;
    private boolean in_finished = false;
    private boolean out_finished  = true;
    private ConcurrentLinkedQueue<ClusterPairBean> inN_queue = null;
    private HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map = null;
    private ReadWriteLock rwl = null;
    public ThreadDealt2Next(ConcurrentLinkedQueue<ClusterPairBean> inN_queue, HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map, ReentrantReadWriteLock rwl){
        this.inN_queue = inN_queue;
        in_finished = false;
        this.c_f_map = c_f_map;
        this.rwl = rwl;
    }
    public void setIn_finished(boolean in_finished) {
        this.in_finished = in_finished;
    }
    public boolean getOut_finished(){
        return this.out_finished;
    }

    public void setOut_finished(boolean out_finished) {
        this.out_finished = out_finished;
    }
    private int mustTwo = 0;
    public void run() {
        while (in_finished == false){
            while(!inN_queue.isEmpty()){


//                mustTwo = 0;
                this.rwl.readLock().lock();
                ClusterPairBean clusterPairBean = inN_queue.poll();
                if(clusterPairBean ==null){
                    this.rwl.readLock().unlock();
                    continue;
                }
                this.setOut_finished(false);
                c1 = clusterPairBean.getC1();
                c2 = clusterPairBean.getC2();
                ArrayList<ClusterPairBean> tmp_pairs1 = c_f_map.get(c1);
                ArrayList<ClusterPairBean> tmp_pairs2 = c_f_map.get(c2);
                tmp_pairs1.add(clusterPairBean);
                tmp_pairs2.add(clusterPairBean);
                this.setOut_finished(true);
                this.rwl.readLock().unlock();


            }



        }

    }
}