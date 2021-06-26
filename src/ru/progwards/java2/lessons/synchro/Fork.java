package progwards.java2.lessons.synchro;

import java.util.concurrent.atomic.AtomicBoolean;

public class Fork {

    private AtomicBoolean isFree;

    Fork(){
        isFree = new AtomicBoolean(true);
    }
    public AtomicBoolean isFree(){
        return isFree;
    }
    public void setFree(AtomicBoolean free){
        isFree = free;
    }
}
