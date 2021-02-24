package progwards.java2.lessons.gc;

public class Heap2 {
    byte[] bytes;
    int allocated;
    int maxSize;

    Heap2(int maxHeapSize) {
        this.maxSize = maxHeapSize;
        bytes = new byte[maxHeapSize];
        allocated = 0;
    }

    public int malloc(int size) {
        int ptr = allocated;
        allocated += size;
        allocated %= maxSize;
        return ptr;
    }

    public void free(int ptr) {
    }
}
