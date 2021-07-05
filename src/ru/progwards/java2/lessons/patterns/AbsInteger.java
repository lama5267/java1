package progwards.java2.lessons.patterns;

import java.math.BigInteger;

public class AbsInteger {
    int num;

    public AbsInteger (int num) {
        this.num = num;
    }

    public String toString() {
        return String.valueOf(num);
    }

    static AbsInteger add(AbsInteger num1, AbsInteger num2) {
        BigInteger N1 = new BigInteger(String.valueOf(num1));
        BigInteger N2 = new BigInteger(String.valueOf(num2));
        N1 = N1.add(N2);
        int temp = N1.intValueExact();
        if (temp < Byte.MAX_VALUE && temp > Byte.MIN_VALUE) {
            return new ByteInteger((byte)temp);
        }
        else if (temp < Short.MAX_VALUE && temp > Short.MIN_VALUE) {
            return new ShortInteger((short) temp);
        }
        else return new IntInteger(temp);
    }
}