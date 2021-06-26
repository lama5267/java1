package progwards.java2.lessons.synchro;

public class OutOfMemoryException extends RuntimeException {
    private int size;

    OutOfMemoryException(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "OutOfMemoryException{" + " Невозможно найти память размером = " + size + '}';
    }
}
