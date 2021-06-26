package progwards.java2.lessons.synchro;

import java.util.ArrayDeque;
import java.util.concurrent.ThreadLocalRandom;

public class HeapWithThreadTest {
    static final int maxSize = 19327352;
    static final int maxSmall = 10;
    static final int maxMedium = 100;
    static final int maxBig = 1000;
    static final int maxHuge = 10000;
    static int allocated = 0;

    static class Block {
        public int ptr;
        public int size;

        public Block(int ptr, int size) {
            this.ptr = ptr;
            this.size = size;
        }
    }

    static int getRandomSize() {
        int n = Math.abs(ThreadLocalRandom.current().nextInt()%10);
        int size = Math.abs(ThreadLocalRandom.current().nextInt());
        if (n < 6)
            size %= maxSmall;
        else if (n < 8)
            size %= maxMedium;
        else if (n < 9)
            size %= maxBig;
        else
            size %= maxHuge;
        return size > (maxSize-allocated)-1 ? (maxSize-allocated)/2+1 : size+1;
    }

    public static void main(String[] args) throws InvalidPointerException, OutOfMemoryException {
        HeapWithThread heapSimple = new HeapWithThread(maxSize);
        ArrayDeque<Block> blocks = new ArrayDeque<>();
        int count = 0;
        int count1 = 0;
        int allocTime = 0;
        int freeTime = 0;
        int allocTime1 = 0;
        int freeTime1 = 0;
        long totaltime;

        long start = System.currentTimeMillis();
        // alloc and free 30% random
        while ((maxSize - allocated) > 3000) {
            long lstart, lstop;
            int size = getRandomSize();
            allocated += size;
            count++;
            lstart = System.currentTimeMillis();
            int ptr = heapSimple.malloc(size);
            lstop = System.currentTimeMillis();
            allocTime += lstop-lstart;
            blocks.offer(new Block(ptr, size));
            int n = Math.abs(ThreadLocalRandom.current().nextInt()%25);
            if (n == 0) {
                //n = Math.abs(ThreadLocalRandom.current().nextInt()%blocks.size());
                for (int i=0; i<5; i++) {
                    Block block = blocks.poll();
                    if (block == null)
                        break;
                    lstart = System.currentTimeMillis();
                    heapSimple.free(block.ptr);
                    lstop = System.currentTimeMillis();
                    freeTime += lstop - lstart;
                    allocated -= block.size;
                }
                //blocks.remove(n);
            }
            n = Math.abs(ThreadLocalRandom.current().nextInt()%100000);
            if (n==0)
                System.out.println(maxSize-allocated);
        }
        long stop = System.currentTimeMillis();
        totaltime = stop-start;

        HeapWithThread heap = new HeapWithThread(maxSize);
        blocks = new ArrayDeque<>();
        allocated = 0;
        start = System.currentTimeMillis();
        // alloc and free 30% random
        while ((maxSize - allocated) > 3000) {
            long lstart, lstop;
            int size = getRandomSize();
            allocated += size;
            count1++;
            lstart = System.currentTimeMillis();
            int ptr = heap.malloc(size);
            lstop = System.currentTimeMillis();
            allocTime1 += lstop-lstart;
            blocks.offer(new HeapWithThreadTest.Block(ptr, size));
            int n = Math.abs(ThreadLocalRandom.current().nextInt()%25);
            if (n == 0) {
                //n = Math.abs(ThreadLocalRandom.current().nextInt()%blocks.size());
                for (int i=0; i<5; i++) {
                    HeapWithThreadTest.Block block = blocks.poll();
                    if (block == null)
                        break;
                    lstart = System.currentTimeMillis();
                    heap.free(block.ptr);
                    lstop = System.currentTimeMillis();
                    freeTime1 += lstop - lstart;
                    allocated -= block.size;
                }
                //blocks.remove(n);
            }
            n = Math.abs(ThreadLocalRandom.current().nextInt()%100000);
            if (n==0)
                System.out.println(maxSize-allocated);
        }
        stop = System.currentTimeMillis();

        System.out.println("HeapSimple malloc time: "+allocTime+" free time: "+freeTime);
        System.out.println("HeapSimple total time: "+totaltime+" count: "+count);


        System.out.println("HeapWithThread malloc time: "+allocTime1+" free time: "+freeTime1);
        System.out.println("HeapWithThread total time: "+(stop-start)+" count: "+count1);
    }
}