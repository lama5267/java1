package progwards.java2.lessons.gc;

public class OutOfMemoryException extends Throwable {
    @Override
    public String toString() {
        return "OutOfMemoryException. нет свободного блока подходящего размера";
    }
}
