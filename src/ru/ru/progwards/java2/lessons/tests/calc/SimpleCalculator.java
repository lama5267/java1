package ru.progwards.java2.lessons.tests.calc;


public class SimpleCalculator {
    private static boolean checkMaxOrMin(long result) {
        return result > Integer.MAX_VALUE || result < Integer.MIN_VALUE;
    }

    public static  int sum(int val1, int val2) {
        long result = (long) val1 + val2;
        if (checkMaxOrMin(result))
            throw new ArithmeticException();

        return val1 + val2;
    }
    public static int  diff(int val1, int val2) {
        long result = (long) val1 - val2;
        if (checkMaxOrMin(result))
            throw new ArithmeticException();

        return val1 - val2;
    }

    public static int mult(int val1, int val2) {
        long result = (long) val1 * val2;
        if (checkMaxOrMin(result))
            throw new ArithmeticException();

        return val1 * val2;
    }

    public static int div(int val1, int val2) {//
        long result = (long) val1 / val2;
        if (checkMaxOrMin(result))
            throw new ArithmeticException();
        if (val2 == 0)
            throw new ArithmeticException();

        return val1 / val2;
    }
}
