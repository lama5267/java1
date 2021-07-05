package progwards.java2.lessons.patterns;

import java.util.ArrayDeque;

public class FilterSpeedGps implements Speed {
    private SpeedGps speedGPS;
    double mathExpectation;                  // матожидание
    ArrayDeque<Double> speeds;               // 50 последних скоростей для вычисления 3 сигм
    double sigma;                            //среднеквадратичное отклонение

    FilterSpeedGps(SpeedGps speedGPS) {
        this.speedGPS = speedGPS;
        speeds = new ArrayDeque<>();
    }

    @Override
    public void parse(String name) {
        speedGPS.parse(name);
    }

    @Override
    public double distBetween(GPS gps1, GPS gps2) {
        return speedGPS.distBetween(gps1, gps2);
    }

    @Override
    public double speed(GPS gps) {
        return speedGPS.speed(gps);
    }

    @Override
    public void track(String name) {
        parse(name);
        // первые 50 точек для статистики
        for (int i=0; i<50; i++) {
            double pointSpeed = speed(speedGPS.gpspoints.get(i));
            speeds.offerLast(pointSpeed);
            System.out.printf("Текущая скорость - %.2f м/с\n", pointSpeed);
        }

        calcSigma();

        int mistake = 0;
        for (int i = 50; i<speedGPS.gpspoints.size()-1; i++) {
            double pointSpeed = speed(speedGPS.gpspoints.get(i));
            speeds.pollFirst();
            speeds.offerLast(pointSpeed);
            if ((pointSpeed > mathExpectation - 3*sigma) && (pointSpeed < mathExpectation + 3*sigma)) {
                System.out.printf("Текущая скорость - %.2f м/с\n", pointSpeed);
                if (mistake!=0)
                    mistake = 0;
            } else {
                mistake ++;
                System.out.printf("Выход за пределы 3 сигм - %.2f м/с\n", pointSpeed);
                //если подряд вылетают 9 точек, возможно
                // какое-то серьезное изменение - проводим пересчет статистики
                // условие можно поменять на другое, более обоснованное
                if (mistake > 8) {
                    System.out.println("Пересчет статистики");
                    calcSigma();
                }
            }
        }
    }

    public void calcSigma() {
        ArrayDeque<Double> speedsTemp = new ArrayDeque<>(speeds);
        double sum = 0;
        // мат ожидание
        while (!speedsTemp.isEmpty())
            sum += speedsTemp.pollFirst();
        mathExpectation = sum / 50;
        System.out.printf("Матожидание " + "%.3f" + " м/с\n", mathExpectation);

        // дисперсия
        speedsTemp = new ArrayDeque<>(speeds);
        sum = 0;
        while (!speedsTemp.isEmpty()) {
            double diff = mathExpectation - speedsTemp.pollFirst();
            sum += diff * diff;
        }
        double dispersion = sum / 50;
        System.out.printf("Дисперсия " + "%.3f" + " м/с\n", dispersion);

        // средеквадр отклонение
        sigma = Math.sqrt(dispersion);
        System.out.printf("Отклонение " + "%.3f" + " м/с\n" + "3 сигма " + "%.3f" + " м/с\n", sigma, 3 * sigma);
    }
}
