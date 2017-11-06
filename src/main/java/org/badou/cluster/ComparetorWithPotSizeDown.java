package org.badou.cluster;

import java.util.Comparator;

/**
 * Created by yishuihan on 17-7-29.
 */
public class ComparetorWithPotSizeDown implements Comparator {
    public int compare(Object o1, Object o2) {
        ClusterBean s1=(ClusterBean) o1;
        ClusterBean s2=(ClusterBean)o2;

        Integer s1_pre = s1.getPointSize();
        Integer s2_pre = s2.getPointSize();//;
//        Double s1_pre = s1.getSEC();
//        Double s2_pre = s2.getSEC();//;
        if(s1_pre<s2_pre)//DOWN exchange
            return 1;
        else
            return -1;
    }
}
