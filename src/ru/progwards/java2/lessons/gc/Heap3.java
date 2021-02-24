package progwards.java2.lessons.gc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;

public class Heap3 {
    final static byte FREE = 0;
    final static byte BUSY = 1;
    final static byte MAGIC = (byte)0xAA;
    final static int HEADER_SIZE = 10;
    final static int SEARCH_DEEP = 1;

    /*class Block {
        int size;
        int ptr;
    }*/

    byte[] bytes;
    int maxsize;
    int lastptr;

    int maxasize;

    Heap3(int maxHeapSize) {
        bytes = new byte[maxHeapSize+maxHeapSize/10];
        int n = maxHeapSize/10/HEADER_SIZE;
        maxsize = bytes.length;
        int ptr = HEADER_SIZE;
        setHeaderMagic(ptr,MAGIC);
        setHeaderBusy(ptr, FREE);
        setHeaderPrev(ptr, 0);
        setHeaderSize(ptr, maxsize-HEADER_SIZE);
        lastptr = HEADER_SIZE;
    }

    public int malloc(int size) throws InvalidPointerException {
        int ptr = findBlockFull(size);
        int current = getHeaderSize(ptr);
        int curnext = ptr + current + HEADER_SIZE;
        setHeaderBusy(ptr, BUSY);
        if (current > size+HEADER_SIZE) {
            setHeaderSize(ptr, size);
            int nextptr = ptr + size + HEADER_SIZE;
            setHeaderMagic(nextptr, MAGIC);
            setHeaderBusy(nextptr, FREE);
            setHeaderPrev(nextptr, size);
            int newsize = current-size-HEADER_SIZE;
            setHeaderSize(nextptr, newsize);
            fixNextSize(nextptr, newsize);
        }
        return ptr;
    }

    public void free(int ptr) throws InvalidPointerException {
        if (bad(ptr) || getHeaderBusy(ptr) != BUSY)
            throw new InvalidPointerException();
        setHeaderBusy(ptr, FREE);
        defrag(ptr);
    }

    int findBlock(int size) throws InvalidPointerException {
        size += HEADER_SIZE;
        int ptr = lastptr;
        int current = getHeaderSize(ptr);
        int best = Integer.MAX_VALUE;
        int bestptr = ptr;
        int i = 0;
        while (i < SEARCH_DEEP) {
            //System.out.println("search "+size);
            if (getHeaderBusy(ptr) == FREE) {
                if(size == current)
                    return ptr;
                if (size < current) {
                    if (best < current-size) {
                        best = current-size;
                        bestptr = ptr;
                    }
                }
                i++;
            }
            ptr += current + HEADER_SIZE;
            if (ptr > maxsize) {
                ptr = HEADER_SIZE;
            }
            if (ptr == lastptr)
                if (best == Integer.MIN_VALUE) {
                    throw new InvalidPointerException();//OutOfMemoryError();
                } else {
                    lastptr = bestptr;
                    return bestptr;
                }
            current = getHeaderSize(ptr);
        }
        lastptr = bestptr;
        return bestptr;
    }

    void defrag(int ptr) throws InvalidPointerException {
        int size = getHeaderSize(ptr);
        int prev = getHeaderPrev(ptr);
        // if not first block defrag prev
        if (prev != 0) {
            int prevptr = ptr - prev - HEADER_SIZE;
            if (bad(prevptr))
                throw new InvalidPointerException();
            if (getHeaderBusy(prevptr) == FREE) {
                size += prev + HEADER_SIZE;
                setHeaderSize(prevptr, size);
                fixNextSize(prevptr, size);
                ptr = prevptr;
            }
        }
        int nextptr = ptr + size + HEADER_SIZE;
        // if not last block defrag next
        if (nextptr <  maxsize) {
            if (bad(nextptr))
                throw new InvalidPointerException();
            if (getHeaderBusy(nextptr) == FREE) {
                size += getHeaderSize(nextptr) + HEADER_SIZE;
                setHeaderSize(ptr, size);
                fixNextSize(ptr, size);
            }
        }
    }

    void fixNextSize(int ptr, int size) {
        int nextptr = ptr + size + HEADER_SIZE;
        if (nextptr < maxsize)
            setHeaderPrev(nextptr, size);
        else if (getHeaderBusy(ptr) == FREE)
            lastptr = ptr;
    }

    boolean bad(int ptr) {
        return  ptr < HEADER_SIZE || getHeaderMagic(ptr) != MAGIC || ptr + getHeaderSize(ptr) > maxsize;
    }

    /*
    Header
    [0] - MAGIC
    [1] - FREE / BUSY
    [2] --
    [3]
    [4]
    [5] prev_size
    [6]
    [7]
    [8]
    [9] next_size
     */


    byte getHeaderMagic(int ptr) {
        return bytes[ptr-HEADER_SIZE];
    }

    byte getHeaderBusy(int ptr) {
        return bytes[ptr-HEADER_SIZE+1];
    }

    int getHeaderPrev(int ptr) {
        return bytes2int(ptr-8);
    }

    int getHeaderSize(int ptr) {
        return bytes2int(ptr-4);
    }

    void setHeaderMagic(int ptr, byte magic) {
        bytes[ptr-HEADER_SIZE] = magic;
    }

    void setHeaderBusy(int ptr, byte busy) {
        bytes[ptr-HEADER_SIZE+1] = busy;
    }

    void setHeaderPrev(int ptr, int prev) {
        int2bytes(prev,ptr-8);
    }

    void setHeaderSize(int ptr, int size) {
        if (size == 0)
            System.out.println("size=0");
        int2bytes(size,ptr-4);
    }

    void int2bytes(int n, int index) {
        bytes[index++] = (byte)n;
        n >>= 8;
        bytes[index++] = (byte)n;
        n >>= 8;
        bytes[index++] = (byte)n;
        n >>= 8;
        bytes[index] = (byte)n;
    }

    int bytes2int(int index) {
        int n = bytes[index+3] & 0xFF;
        n <<= 8;
        n |= bytes[index+2] & 0xFF;
        n <<= 8;
        n |= bytes[index+1] & 0xFF;
        n <<= 8;
        n |= bytes[index] & 0xFF;
        return n;
    }

    public void getBytes(int ptr, byte[] bytes) {
        //System.arraycopy(this.bytes, ptr, bytes, 0, size);
    }

    public void setBytes(int ptr, byte[] bytes) {
        //System.arraycopy(bytes, 0, this.bytes, ptr, size);
    }


    void defrag() throws InvalidPointerException {
        int ptr = HEADER_SIZE;
        while (ptr < maxsize) {
            if (getHeaderBusy(ptr) == FREE) {
                defrag(ptr);
            }
            ptr += getHeaderSize(ptr) + HEADER_SIZE;
        }
    }

    int findBlockFull(int size) throws InvalidPointerException {

        return findBlock(size+HEADER_SIZE);
    }

    int lastBlock() {
        int ptr = HEADER_SIZE, prev = ptr;
        while (ptr < maxsize) {
            prev = ptr;
            ptr += getHeaderSize(ptr) + HEADER_SIZE;
        }
        return prev;
    }

    int checkHeap() {
        int ptr = HEADER_SIZE;
        while (ptr < maxsize) {
            if (bad(ptr))
                return ptr;
            ptr += getHeaderSize(ptr) + HEADER_SIZE;
        }
        return 0;
    }

    public void logFreeBlocks(int size) {
        int ptr = HEADER_SIZE;
        while (ptr < maxsize) {
            if (getHeaderBusy(ptr) == FREE) {
                if(getHeaderSize(ptr) > size+HEADER_SIZE)
                    System.out.println(getHeaderSize(ptr)+HEADER_SIZE);
                logBlock(ptr);
            }
            ptr += getHeaderSize(ptr) + HEADER_SIZE;
        }
    }

    public void logBlocks() {
        //log(Arrays.toString(Arrays.copyOf(bsize,asize)));
        //log(Arrays.toString(Arrays.copyOf(bptr,asize)));
        int ptr = HEADER_SIZE;
        while (ptr < maxsize) {
            logBlock(ptr);
            ptr += getHeaderSize(ptr) + HEADER_SIZE;
        }
    }

    void printBlock(int ptr) {
        System.out.println("addr: "+ptr+" magic: "+getHeaderMagic(ptr)+" busy: "+getHeaderBusy(ptr)+" prev: "+getHeaderPrev(ptr)+" size: "+getHeaderSize(ptr));
    }

    void logBlock(int ptr) {
        log("addr: "+ptr+" magic: "+getHeaderMagic(ptr)+" busy: "+getHeaderBusy(ptr)+" prev: "+getHeaderPrev(ptr)+" size: "+getHeaderSize(ptr));
    }

    public static PrintWriter logfile = null;
    public static void initLog(String filename) {
        try {
            logfile = new PrintWriter(new FileOutputStream(new File(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeLog() {
        if (logfile != null)
            logfile.close();
    }

    public static void log(String str) {
        log2(str, true);
    }

    public static void log2(String str, boolean log) {
        //System.out.println(str);
        if (log) {
            try {
                logfile.println(LocalDateTime.now() + ": " + str);
            } catch (Exception e) {
                e.printStackTrace();
                logfile.close();
            }
        }
    }


}
