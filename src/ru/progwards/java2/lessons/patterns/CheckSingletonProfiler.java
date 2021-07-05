package progwards.java2.lessons.patterns;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CheckSingletonProfiler {
    int counter = 0;
    static Lock lock1 = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();
public synchronized void inc(SingletonProfiler sp){
    sp.enterSection("inc",Thread.currentThread());
    counter++;
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    sp.enterSection("inc",Thread.currentThread());
}

    public synchronized void dec (SingletonProfiler sp) {
        sp.enterSection("dec", Thread.currentThread());


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sp.exitSection("dec", Thread.currentThread());
    }

    public static void main(String[] args) {
        CheckSingletonProfiler csp = new CheckSingletonProfiler();
        Thread [] threads = new Thread[11];
        SingletonProfiler[] singletonProfilers = new SingletonProfiler[11];
        for (int i=0; i<threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                singletonProfilers[finalI] = SingletonProfiler.INSTANSE;
                System.out.println("до " + finalI + " = " + csp.counter);

                if (finalI %2 == 0)
                    csp.inc(singletonProfilers[finalI]);
                else csp.dec(singletonProfilers[finalI]);

                if (finalI %3 ==0)
                    csp.dec(singletonProfilers[finalI]);

                System.out.println("после" + finalI + " = "+ csp.counter);

            });
        }
        for (int i=0; i<threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i< threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(csp.counter);
        System.out.println(SingletonProfiler.INSTANSE.getStatisticInfo());
    }

}

