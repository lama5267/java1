package progwards.java2.lessons.patterns;

public class ShortInteger extends AbsInteger {
    short val;


    public ShortInteger (short val) {
        super(val);
        this.val = val;
    }

    @Override
    public String toString () {
        return String.valueOf(val);
    }

}