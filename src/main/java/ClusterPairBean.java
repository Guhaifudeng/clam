/**
 * Created by yishuihan on 17-7-29.
 */
public class ClusterPairBean {
    ClusterBean c1 = null;
    ClusterBean c2 = null;
    Double opt = Double.MIN_VALUE;
    Double RC = Double.MIN_VALUE;
    Double RI = Double.MIN_VALUE;
    Double SEC = Double.MIN_VALUE;

    public Double getRC() {
        return RC;
    }

    public void setRC(Double RC) {
        this.RC = RC;
    }

    public Double getRI() {
        return RI;
    }

    public void setRI(Double RI) {
        this.RI = RI;
    }

    public Double getSEC() {
        return SEC;
    }

    public void setSEC(Double SEC) {
        this.SEC = SEC;
    }

    ClusterPairBean(ClusterBean c1, ClusterBean c2){
        this.c1 = c1;
        this.c2 = c2;
    }
    public ClusterBean getC1() {
        return c1;
    }

    public void setC1(ClusterBean c1) {
        this.c1 = c1;
    }

    public ClusterBean getC2() {
        return c2;
    }

    public void setC2(ClusterBean c2) {
        this.c2 = c2;
    }

    public Double getOpt() {
        return opt;
    }

    public void setOpt(Double opt) {
        this.opt = opt;
    }
}