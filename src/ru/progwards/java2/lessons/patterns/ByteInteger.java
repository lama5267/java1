package progwards.java2.lessons.patterns;

public class ByteInteger extends AbsInteger{
    byte val;


    public ByteInteger (byte val) {
        super(val);
        this.val = val;
    }

    @Override
    public String toString () {
        return String.valueOf(val);
    }





    public static void main(String[] args) {
        ByteInteger bb = new ByteInteger((byte)127);
        ByteInteger cc = new ByteInteger((byte)-5);
        ShortInteger dd = new ShortInteger( (short)128);
        IntInteger vv = new IntInteger(125523);
        System.out.println(add(bb,cc));
        System.out.println(add(bb,cc).getClass());
    }

}
