package progwards.java2.lessons.patterns;

import java.math.BigInteger;

public enum AbsFactInteger {
INSTANCE;
    public AbsInteger getInteger (int num) {
        int temp = new BigInteger(String.valueOf(num)).intValueExact();
        if (temp < Byte.MAX_VALUE && temp > Byte.MIN_VALUE)
            return new ByteInteger((byte)temp);
        else if (temp < Short.MAX_VALUE && temp > Short.MIN_VALUE)
            return new ShortInteger((short) temp);
        else return new IntInteger(temp);
    }
}
