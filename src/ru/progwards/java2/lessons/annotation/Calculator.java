package progwards.java2.lessons.annotation;

public class Calculator implements ICalc {

    public int sum(int val1, int val2) {
        long result = (long) val1 + val2;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
            throw new ArithmeticException();
        return val1 + val2;
    }

    public int sub(int val1, int val2) {
        long result = (long) val1 - val2;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
            throw new ArithmeticException();
        return val1 - val2;
    }

    public int mult(int val1, int val2) {
        long result = (long) val1 * val2;
        if (result > Integer.MAX_VALUE || result < Integer.MIN_VALUE)
            throw new ArithmeticException();
        return val1 * val2;
    }

    public int div(int val1, int val2) {
        if (val2 == 0)
            throw new ArithmeticException();
        return val1 / val2;
    }

    public static void main(String[] args) {
        Calculator simCalc = new Calculator();
        System.out.println(simCalc.div(Integer.MIN_VALUE, 0));
    }
}