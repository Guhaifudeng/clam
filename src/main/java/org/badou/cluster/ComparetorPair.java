package org.badou.cluster;

import java.util.Comparator;
public class ComparetorPair implements Comparator{
    public int compare(Object o1, Object o2) {
        ClusterPairBean s1=(ClusterPairBean) o1;
        ClusterPairBean s2=(ClusterPairBean)o2;

        Double s1_pre = s1.getOpt();
        Double s2_pre = s2.getOpt();//;
//        Double s1_pre = s1.getSEC();
//        Double s2_pre = s2.getSEC();//;
        if(s1_pre<s2_pre)//DOWN exchange
            return 1;
        else
            return -1;
    }

}



