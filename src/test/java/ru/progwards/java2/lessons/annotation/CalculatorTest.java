package ru.progwards.java2.lessons.annotation;

import progwards.java2.lessons.annotation.After;
import progwards.java2.lessons.annotation.Before;
import progwards.java2.lessons.annotation.Calculator;
import progwards.java2.lessons.annotation.Test;

public class CalculatorTest {
    Calculator simCalc;

    @Before
    public void init () {
        simCalc = new Calculator();
    }

    @Test(Priority = 1)
    public void sum () {
        Integer [][] paramSum = {{Integer.MAX_VALUE, 0, Integer.MAX_VALUE}, {0,0,0}, {-1, Integer.MAX_VALUE, Integer.MIN_VALUE}, {999, 555, 444}, {0, -333, 333}};
        for (Integer[] param : paramSum) {
            if (param[0] == simCalc.sum(param[1], param[2])) {
                System.out.println("Тест sum пройден с параметрами " + param[1] + " и " + param[2]);
            } else {
                System.out.println("Тест sum не пройден, так как " + param[1] + " + " + param[2] + " не равно " + simCalc.sum(param[1], param[2]));
            }
        }
    }

    @Test(Priority = 5)
    public void sumExc () {
        try {
            simCalc.sum(Integer.MIN_VALUE,Integer.MIN_VALUE);
            System.out.println("Тест sumExc с параметрами "  + Integer.MIN_VALUE + " и " + Integer.MIN_VALUE + "не пройден - не выброшено исключение при переполнении");
        } catch (ArithmeticException ex) {
            System.out.println("Тест sumExc с параметрами " + Integer.MIN_VALUE + " и " + Integer.MIN_VALUE + " пройден");
        }
    }

    @Test(Priority = 2)
    public void sub () {
        Integer [][] paramSub = {{Integer.MIN_VALUE+1, 0, Integer.MAX_VALUE}, {0,0,0}, {0, Integer.MIN_VALUE, Integer.MIN_VALUE}, {111, 555, 444}, {-666, -333, 333}};
        for (Integer[] param : paramSub) {
            if (param[0] == simCalc.sub(param[1], param[2])) {
                System.out.println("Тест sub пройден  с параметрами " + param[1] + " и " + param[2]);
            } else {
                System.out.println("Тест sub не пройден, так как " + param[1] + " - " + param[2] + " не равно " + simCalc.sub(param[1], param[2]));
            }
        }
    }

    @Test(Priority = 6)
    public void subExc () {
        Integer [][] paramSubExc = {{Integer.MIN_VALUE, 1}, {-2, Integer.MAX_VALUE}};
        for (Integer[] parSubExc: paramSubExc)
            try {
                simCalc.sub(parSubExc[0],parSubExc[1]);
                System.out.println("Тест subExc с параметрами " + parSubExc[0] + " и " + parSubExc[1] + "не пройден - не выброшено исключение при переполнении");
            } catch (ArithmeticException ex) {
                System.out.println("Тест subExc с параметрами " + parSubExc[0] + " и " + parSubExc[1] +  " пройден");
            }
    }

    @Test(Priority = 3)
    public void mult () {
        Integer [][] paramMult = {{Integer.MAX_VALUE, 1, Integer.MAX_VALUE}, {0,0,0}, {0, Integer.MIN_VALUE, 0}, {4440, 10, 444}, {-144, -12, 12}};
        for (Integer[] param : paramMult) {
            if (param[0] == simCalc.mult(param[1], param[2])) {
                System.out.println("Тест mult пройден с параметрами " + param[1] + " и " + param[2]);
            } else {
                System.out.println("Тест mult не пройден, так как " + param[1] + " * " + param[2] + " не равно " + simCalc.mult(param[1], param[2]));
            }
        }
    }

    @Test(Priority = 7)
    public void multExc () {
        Integer [][] paramMultExc = {{Integer.MIN_VALUE, 2}, {-2, Integer.MAX_VALUE}};
        for (Integer[] parSubExc: paramMultExc)
            try {
                simCalc.mult(parSubExc[0],parSubExc[1]);
                System.out.println("Тест multExc с параметрами " + parSubExc[0] + " и " + parSubExc[1] + "не пройден - не выброшено исключение при переполнении");
            } catch (ArithmeticException ex) {
                System.out.println("Тест multExc с параметрами " + parSubExc[0] + " и " + parSubExc[1] +  " пройден");
            }
    }

    @Test(Priority = 4)
    public void div () {
        Integer [][] paramDiv = {{Integer.MAX_VALUE, Integer.MAX_VALUE, 1}, {0,0,1}, {0,0,-1}, {1000, -1000, -1},
                {-1, Integer.MIN_VALUE, Integer.MAX_VALUE}, {0, Integer.MAX_VALUE, Integer.MIN_VALUE}};
        for (Integer[] param : paramDiv) {
            if (param[0] == simCalc.div(param[1], param[2])) {
                System.out.println("Тест div пройден с параметрами " + param[1] + " и " + param[2]);
            } else {
                System.out.println("Тест div не пройден, так как " + param[1] + " / " + param[2] + " не равно " + simCalc.div(param[1], param[2]));
            }
        }

    }

    @Test(Priority = 8)
    public void divExc () {
        Integer [] paramDivExc = {1, Integer.MIN_VALUE, Integer.MIN_VALUE};
        for (Integer parDivExc: paramDivExc) {
            try {
                simCalc.div(parDivExc, 0);
                System.out.println("Тест divExc с параметрами " + parDivExc + " и 0 не пройден - не выброшено исключение при переполнении");
            } catch (ArithmeticException ex) {
                System.out.println("Тест divExc с параметрами " + parDivExc + " и 0 пройден");
            }
        }
    }

    @After
    public void destroy () {
        simCalc = null;
    }
}
