import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ThreadDealt2Next implements Runnable{
    private ClusterBean c1 = null;
    private ClusterBean c2 = null;
    private boolean in_finished = false;
    private volatile  boolean out_finished  = false;
    private ConcurrentLinkedQueue<ClusterPairBean> inN_queue = null;
    private HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map = null;
    public ThreadDealt2Next(ConcurrentLinkedQueue<ClusterPairBean> inN_queue, HashMap<ClusterBean,ArrayList<ClusterPairBean>> c_f_map){
        this.inN_queue = inN_queue;
        in_finished = false;
        this.c_f_map = c_f_map;
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
            mustTwo++;
            while(!inN_queue.isEmpty()){
                mustTwo = 0;
                ClusterPairBean clusterPairBean = inN_queue.poll();
                if(clusterPairBean ==null)
                    continue;
                this.setOut_finished(false);
                c1 = clusterPairBean.getC1();
                c2 = clusterPairBean.getC2();
                ArrayList<ClusterPairBean> tmp_pairs1 = c_f_map.get(c1);
                ArrayList<ClusterPairBean> tmp_pairs2 = c_f_map.get(c2);
                tmp_pairs1.add(clusterPairBean);
                tmp_pairs2.add(clusterPairBean);

            }

            if(mustTwo >= 2){
                this.setOut_finished(true);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}