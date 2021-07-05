package progwards.java2.lessons.patterns;

import java.util.Random;

public class Client {

    public static void main(String[] args) {
        Thread[] threads = new Thread[10];
        for (int i =0; i<threads.length; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random random = new Random();
                    int next;
                    AbsInteger newInteger;
                    for (int j = 0; j<10; j++) {
                        next = random.nextInt();
                        newInteger = AbsFactInteger.INSTANCE.getInteger(next);
                        System.out.println("Число " + next + " и класс " + newInteger.getClass());
                        next = random.nextInt(128);
                        newInteger = AbsFactInteger.INSTANCE.getInteger(next);
                        System.out.println("Число " + next + " и класс " + newInteger.getClass());
                        next = random.nextInt(Short.MAX_VALUE);
                        newInteger = AbsFactInteger.INSTANCE.getInteger(next);
                        System.out.println("Число " + next + " и класс " + newInteger.getClass());
                    }
                }
            });
        }

        for (int i =0; i<threads.length; i++) {
            threads[i].start();
        }

        for (int i =0; i<threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
