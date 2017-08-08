import fig.basic.Pair;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ThreadDealt implements Runnable{
    private ClusterBean c1 = null;
    private ClusterBean c2 = null;
    private boolean in_finished = false;
    private ConcurrentLinkedQueue<ClusterPairBean> out2_queue = null;
    private PriorityBlockingQueue<ClusterPairBean> out_queue = null;
    private ConcurrentLinkedQueue<Pair<ClusterBean,ClusterBean>> in_queue = null;
    private ChameleonTool chameleonTool = null;
    private Double minInitClusterPairRC = 0.4;
    public ThreadDealt(PriorityBlockingQueue<ClusterPairBean> out_queue, ConcurrentLinkedQueue<ClusterPairBean> out2_queue,
                       ConcurrentLinkedQueue<Pair<ClusterBean,ClusterBean>> in_queue, ChameleonTool chameleonTool){
        this.out_queue = out_queue;
        this.out2_queue = out2_queue;
        this.in_queue = in_queue;
        this.chameleonTool = chameleonTool;
        this.in_finished = false;

    }
    public void setIn_finished(boolean in_finished) {
        this.in_finished = in_finished;
    }
    public Double getMinInitClusterPairRC() {
        return minInitClusterPairRC;
    }
    public void setMinInitClusterPairRC(Double minInitClusterPairRC) {
        this.minInitClusterPairRC = minInitClusterPairRC;
    }
    public void run() {
        while (in_finished == false || !in_queue.isEmpty()){
            Pair<ClusterBean,ClusterBean> pair = in_queue.poll();
            if(pair ==null)
                continue;
            c1 = pair.getFirst();
            c2 = pair.getSecond();


            Pair<Double,Integer> pair1 = chameleonTool.calSEC(c1,c2);
            Double SEC = pair1.getFirst()/pair1.getSecond();
            Double RC = chameleonTool.calRC(c1,c2,pair1);
            Double RI = chameleonTool.calRI(c1,c2,pair1);
            Double opt = chameleonTool.calFunctionDefinedOptimization(RC,RI);

            if(RC>=minInitClusterPairRC){
                ClusterPairBean tmp_pair = new ClusterPairBean(c1,c2);
                tmp_pair.setOpt(opt);
                tmp_pair.setRC(RC);
                tmp_pair.setRI(RI);
                tmp_pair.setSEC(SEC);
                out2_queue.add(tmp_pair);
                out_queue.add(tmp_pair);
            }
        }

    }
}




