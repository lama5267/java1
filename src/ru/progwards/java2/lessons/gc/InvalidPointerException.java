package progwards.java2.lessons.gc;

public class InvalidPointerException extends Throwable {
    @Override
    public String toString() {
        return "InvalidPointerException. неверный указатель";
    }
}
