package progwards.java2.lessons.synchro;

public class Philosopher implements Runnable{
    String name;

 Fork right;// - вилка справа

 Fork left;// - вилка слева

 long reflectTime;// - время, которое философ размышляет в мс

 long eatTime; //- время, которое философ ест в мс

 long reflectSum;// - суммарное время, которое философ размышлял в мс

 long eatSum;// - суммарное время, которое философ ел в мс

    public Philosopher(String name, Fork right, Fork left, long reflectTime, long eatTime) {
        this.name = name;
        this.right = right;
        this.left = left;
        this.reflectTime = reflectTime;
        this.eatTime = eatTime;

    }

    void reflect(){
        long start = System.currentTimeMillis();
        long reflectTimeNow = 0;
while (reflectTimeNow < reflectTime){
    System.out.println("Размышляет " + name);
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    reflectTimeNow = System.currentTimeMillis()-start;
}
reflectSum += reflectTimeNow;
    }

    void eat(){
        long start = System.currentTimeMillis();
        long eatTimeNow = 0;
while (eatTimeNow < eatTime){
    System.out.println("ест " + name);
    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    eatTimeNow = System.currentTimeMillis();
}
eatSum += eatTimeNow - start;
    }

    @Override
    public void run() {
        boolean canEat = false;   // может ли есть?
        while (Simposion.talkContinue) {     // пока разговор не окончен
            while (!canEat) {     // пока не может - пытается
                try {
                    Simposion.semaphore.acquire();    // входит под семафор
                    // берет правую вилку
                    right.isFree().compareAndSet(true, false);
                    // пробует взять левую вилку
                    canEat = left.isFree().compareAndSet(true, false);
                    // если получилось взять обе вилки
                    if (canEat) {
                        eat();                   // ест
                        left.isFree().set(true); // освобождает левую
                    }
                    right.isFree().set(true);    // освобождает правую
                    Simposion.semaphore.release();  // выход из-под семафора
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            reflect();
            canEat = false;
        }
    }
}
