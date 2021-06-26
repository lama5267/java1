package progwards.java2.lessons.synchro;

public class InvalidPointerException extends Exception{
    InvalidPointerException(String message){
        super(message);
    }

    public InvalidPointerException(int ptr) {
    }
}
