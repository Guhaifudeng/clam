package org.badou.cluster;

import java.util.ArrayList;

/**
 * Created by yishuihan on 17-7-28.
 */
public class ClusterBean {

    private Integer id = -1;//cluster id
    private ArrayList <Integer> points = null;//the points in this cluster

    private ChameleonTool chameleonTool = null;

    private Double alpha = 1.0;
    private Double opt = Double.MIN_VALUE;
    private Double RI = Double.MIN_VALUE;
    private Double RC = Double.MIN_VALUE;

    private Double EC = Double.MIN_VALUE;
    private Double SEC = 1.0;

    private Integer merge_edge_num = 1;
    private boolean is_merged;


    public ClusterBean(Integer id, ChameleonTool chameleonTool) {
        this.id = id;
        this.chameleonTool = chameleonTool;

        is_merged = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getSEC() {
        if (!isIs_merged())
        {
            if (this.points.size() == 1) {
                return 1.0;

            } else {
                Integer point1 = this.points.get(0);
                Integer point2 = this.points.get(this.getPointSize() / 2);
                return this.chameleonTool.getPairCenterPointSim(point1, point2);
            }
        }
        return SEC;
    }
    public void setSEC(Double SEC) {
        this.SEC = SEC;
    }
    public Double getEC() {
        if (!is_merged) {
            if (!is_merged) {
                int point_size = this.getPointSize();
                if (point_size == 1 || point_size == 2) {
                    return this.getSEC();
                } else if (point_size == 3) {
                    return this.getSEC() * 2;
                } else {
                    return this.getSEC() * (int) (Math.pow((int) (point_size / 2), 2));
                }
            }
        }
        return EC;
    }
    public void setEC(Double EC) {
        this.EC = EC;
    }
    public Double getRI() {
        if (!is_merged) {
            int point_size = this.getPointSize();
            if (point_size == 1 || point_size == 2) {
                return 1.0;
            } else if (point_size == 3) {
                return 3.0;
            } else if (point_size >= 4) {
                return 4.0;
            }
        }
        return RI;
    }
    public void setRI(Double RI) {
        this.RI = RI;
    }
    public Double getRC() {
        if (!is_merged) {
            return 1.0;
        }
        return RC;
    }
    public void setRC(Double RC) {
        this.RC = RC;
    }
    public Double getAlpha() {
        return alpha;
    }
    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }
    public Double getOpt() {
        if (!is_merged) {
            return this.getRI() * Math.pow(this.getRC(), this.getAlpha());
        }
        return opt;
    }
    public void setOpt(Double opt) {

        this.opt = opt;
    }
    public ArrayList <Integer> getPoints() {

        return points;
    }
    public boolean isIs_merged() {
        return is_merged;
    }
    public void setIs_merged(boolean is_merged) {

        this.is_merged = is_merged;
    }
    public void addPoint(Integer point) {

        this.points.add(point);
    }
    public void initPoint() {
        this.points = new ArrayList <>();
    }
    public Integer getMergeEdgeNum() {
        if (!is_merged) {
            int point_size = this.getPointSize();
            if (point_size == 1 || point_size == 2) {
                return 1;
            } else if (point_size == 3) {
                return 2;
            } else {

                return (int) (Math.pow((int) (point_size / 2), 2));
            }
        }
        return merge_edge_num;
    }
    public Integer getPointSize() {
        return this.points.size();
    }
    public void setMergeEdgeNum(Integer merge_edge_num) {
        this.merge_edge_num = merge_edge_num;
    }
}