package progwards.java2.lessons.synchro;

import java.util.concurrent.Semaphore;

public class Simposion {

static Semaphore semaphore;
static boolean talkContinue = true;// разговор продолжается

    String [] name = {"Сократ", "Аристотель", "Платон", "Конфуций", "Кант"};
    Fork[] forks;               // вилки
    Philosopher [] phils;       // философы
    Thread [] threads;          //потоки

//инициализирует необходимое количество философов и вилок. Каждый философ выполняется в отдельном потоке.
    // reflectTime задает время в мс, через которое философ проголодается, eatTime задает время в мс,
    // через которое получив 2 вилки философ наестся и положит вилки на место

    Simposion(long reflectTime, long eatTime) {
        forks = new Fork[name.length];
        phils = new Philosopher[name.length];
        threads =  new Thread[phils.length];
        semaphore = new Semaphore(phils.length/2,true);

        for (int i=0; i<name.length; i++) {
            forks[i] = new Fork();
        }

        for (int i=0; i<name.length; i++) {
            if (i != name.length-1)
                phils[i] = new Philosopher(name[i], forks[i], forks[i+1], reflectTime, eatTime);
            else
                phils[i] = new Philosopher(name[i], forks[i], forks[0], reflectTime, eatTime);
        }

        for (int i=0; i < phils.length; i++) {
            threads[i] = new Thread(phils[i]);
        }
    }
    //  запускает философскую беседу
    void start() {
        for (int i=0; i < phils.length; i++) {
            threads[i].start();
        }
    }

    //  завершает философскую беседу
    void stop() {
        talkContinue = false;
    }

    // печатает результаты беседы в формате Философ name, ел ххх, размышлял xxx, где ххх время в мс
    void print() {
        for (Philosopher phil : phils) {
            System.out.println("Философ " + phil.name + " ел " + phil.eatSum + " размышлял " + phil.reflectSum);
        }
    }

    public static void main(String[] args) {
        Simposion simposion = new Simposion(500, 200);
        long timeSpeak = 10000;
        System.out.println("Продолжительность разговора " + timeSpeak/1000 + " секунд, плюс доработка потоков");
        simposion.start();

        try {
            Thread.sleep(timeSpeak);
            simposion.stop();
            for (int i=0; i < simposion.phils.length; i++) {
                simposion.threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        simposion.print();
        //реализует тест для философской беседы. Проверить варианты,
        // когда ресурсов (вилок) достаточно (философы долго размышляют и мало едят)
        // и вариант когда не хватает (философы много едят и мало размышляют)
    }
}

