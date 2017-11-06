/**
 * Created by yishuihan on 17-10-23.
 */
public class MultiTest {
    public static void  main(String[] args){
        Thread t = new Thread(new A("hhe"));
        t.start();
        t = new Thread(new A("addd"));
        t.start();
    }

}
class A implements Runnable{
    int a;
    String id = null;
    A(String id){
        this.id = id;
    }
    @Override

    public void run() {
        int i=100;
        while(i>0){
            i--;
            System.out.println(this.id);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}