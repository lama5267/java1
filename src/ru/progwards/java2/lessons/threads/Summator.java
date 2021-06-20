package progwards.java2.lessons.threads;


import java.math.BigInteger;

public class Summator {
    int count;
    static BigInteger sumItog = BigInteger.ZERO;   //общая сумма

    Summator(int count) {
        this.count = count;
    }

    static synchronized void increase (BigInteger threadSum) {
        sumItog = sumItog.add(threadSum);
    }

    public BigInteger sum(BigInteger number) {
        Thread [] threads = new Thread[count];
        // количество целых частей разбиения по потокам
        BigInteger diapazon = number.divide(BigInteger.valueOf(count));
        BigInteger end = BigInteger.ZERO;
        for (int i = 0; i< count; i++) {
            // добавляем единицу для рвномерности
            BigInteger start = end.add(BigInteger.ONE);
            if (i==count-1)
                end = number;
            else
                end = start.add(diapazon);
            BigInteger finalEnd = end;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    BigInteger threadSum = BigInteger.ZERO;   // сумма в потоке
                    BigInteger current = start;               // текущее число для суммирования
                    // пока не прошли весь диапазон потока - суммируем числа
                    while (!current.equals(finalEnd.add(BigInteger.ONE))) {
                        threadSum = threadSum.add(current);
                        current = current.add(BigInteger.ONE);
                    }
                    increase(threadSum);    // передаем число в общую сумму
                }
            });
        }
        // запуск всех потоков
        for (int i = 0; i< count; i++) {
            threads[i].start();
        }
        // проверка все ли потоки выполнились
        for (int i = 0; i< count; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sumItog;
    }

    public static void main(String[] args) {
        Summator summator = new Summator(3);
        System.out.println("Threads: " + summator.sum(BigInteger.valueOf(1000)));

    }
}
