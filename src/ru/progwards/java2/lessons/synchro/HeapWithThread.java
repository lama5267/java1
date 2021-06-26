package progwards.java2.lessons.synchro;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HeapWithThread {
    class Mark {
        private int sizeBlock;
        private Integer startMark;

        public Mark(int sizeBlock) {
            this.sizeBlock = sizeBlock;
            startMark = null;
        }

        void setStartMark (Integer startMark) {
            this.startMark = startMark;
        }

        void setSizeBlock (int sizeBlock) {
            this.sizeBlock = sizeBlock;
        }
    }

    class freeRunnable implements Runnable {
        @Override
        public void run() {
            int ptr1 = ptrMarkMap.poll();
            try {
                ptr1 = checkPtr(ptr1);
            } catch (InvalidPointerException e) {
                e.printStackTrace();
            }
            int size1 = markMap.get(ptr1).sizeBlock;
            // в цикле проверяем указатели освободившихся блоков
            for (int i = 0; i < MAX_FREE_BLOCK; i++) {
                int ptr2 = ptrMarkMap.poll();
                try {
                    ptr2 = checkPtr(ptr2);
                } catch (InvalidPointerException e) {
                    e.printStackTrace();
                }
                int size2 = markMap.get(ptr2).sizeBlock;
                if (ptr1 + size1 == ptr2) {     // ексли указатели на соседние блоки - объединяем
                    size1 += size2;
                    markMap.remove(ptr2);
                    if (i == MAX_FREE_BLOCK - 1) {          // последний записывем в freeblock
                        markMap.remove(ptr1);
                        addFreeBlock(size1, ptr1);
                    }
                } else {                         // если следующий не соседний - добавляем текущий в freeblock
                    markMap.remove(ptr1);
                    addFreeBlock(size1, ptr1);
                    ptr1 = ptr2;
                    size1 = size2;
                }
            }
        }
    }

    private byte[] bytes;
    static ConcurrentSkipListMap<Integer, TreeSet<Integer>> freeBlock = new ConcurrentSkipListMap<>();      // количество/множество указателей
    static TreeMap<Integer, Mark> markMap = new TreeMap<>();                    // указатель/размер блока и свободен ли
    HashMap<Integer, LinkedList<Integer>> codeMark = new HashMap<>();    // перекодированные указатели
    TreeMap<Integer, Integer> freeMarks = new TreeMap<>();
    Integer markBlock;
    ArrayDeque<Integer> ptrMarkMap = new ArrayDeque<>();                // очередь освободившихся указателей
    AtomicInteger countFree = new AtomicInteger();                      // счетчик освободившихся блоков (указателей)
    final int MAX_FREE_BLOCK = 50000;                                    // количество указателей для которых создается новый поток во free
    int maxHeapSize;

    HeapWithThread(int maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
        freeBlock.put(maxHeapSize, new TreeSet<>(Set.of(0)));
    }

    public int malloc(int size) {
        Integer sizeBlock = freeBlock.ceilingKey(size);             // находим подходящий блок
        if (sizeBlock == null) {
            compact();
            sizeBlock = freeBlock.ceilingKey(size);
            if (sizeBlock == null)
                throw new OutOfMemoryException(size);
        }
        markBlock = freeBlock.get(sizeBlock).pollFirst();           // получаем его указатель
        if (freeBlock.get(sizeBlock).isEmpty())                     // если дальше указателей на блок такого размера нет - удаляем
            freeBlock.remove(sizeBlock);
        markMap.put(markBlock, new Mark(size));                     // в указатель записанных
        if (size != sizeBlock)                                        // если блок больше заданного
            addFreeBlock(sizeBlock-size, markBlock + size); // добавляем остаток в свободные блоки
        return markBlock;
    }

    public void free(int ptr) {
        countFree.incrementAndGet();
        boolean doFree = countFree.compareAndSet(MAX_FREE_BLOCK, 0);
        ptrMarkMap.add(ptr);                                             // записываем указатель на свободный блок в очередь
        if (doFree) {                                                    // делаем свободные блоки лоступными, объединяя соседние
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(new freeRunnable());
            service.shutdown();
        }
    }

    public void addFreeBlock (int sizeB, int mark) {
        TreeSet<Integer> tempSet = new TreeSet<>();
        tempSet.add(mark);
        tempSet = freeBlock.putIfAbsent(sizeB, tempSet);
        synchronized (this) {
            if (tempSet!= null) {
                tempSet.add(mark);
                freeBlock.put(sizeB, tempSet);
            }
        }
    }

    public void defrag() {
        for (Map.Entry<Integer, TreeSet<Integer>> entry: freeBlock.entrySet())
            while (!entry.getValue().isEmpty())
                freeMarks.put(entry.getValue().pollFirst(),entry.getKey());
        TreeMap<Integer, Integer> freeMarksTemp = new TreeMap<>();
        int prevM = 0, prevS = 0;
        for (Map.Entry<Integer, Integer> entry: freeMarks.entrySet()){
            if (entry.getKey() == prevS && prevS!=0) {
                freeMarksTemp.put(prevM, freeMarksTemp.get(prevM) + entry.getValue());
                prevS += entry.getValue();
            } else {
                prevM = entry.getKey();
                freeMarksTemp.put(prevM, entry.getValue());
                prevS = entry.getKey()+entry.getValue();
            }
        }
        freeMarks.clear();
        freeBlock.clear();
        for (Map.Entry<Integer, Integer> entry: freeMarksTemp.entrySet())
            addFreeBlock(entry.getValue(),entry.getKey());
    }

    public void compact() {
        System.out.println("Запустилась компактизация");
        int nowMark = 0;
        TreeMap<Integer, Mark> markMapTemp = new TreeMap<>();
        for (Map.Entry<Integer, Mark> entry: markMap.entrySet()){
            markMapTemp.put(nowMark, entry.getValue());
            if (nowMark != entry.getKey() && entry.getValue().startMark == null) {  // если первый раз мняется указатель
                if (!codeMark.containsKey(entry.getKey())) {
                    LinkedList<Integer> sameMark = new LinkedList<>();
                    sameMark.push(nowMark);
                    codeMark.put(entry.getKey(), sameMark);                         // первая перекодировка  - старый/новый
                } else {
                    codeMark.get(entry.getKey()).push(nowMark);
                }
                markMapTemp.get(nowMark).setStartMark(entry.getKey());
            }
            else if (nowMark != entry.getKey()) {
                codeMark.get(entry.getValue().startMark).remove(entry.getKey());     // повторная перекодировка
                codeMark.get(entry.getValue().startMark).push(nowMark);
            }
            nowMark += entry.getValue().sizeBlock;
        }
        markMap.clear();
        markMap.putAll(markMapTemp);
        freeBlock.clear();
        addFreeBlock(maxHeapSize, nowMark);
    }

    public void getBytes(int ptr, byte[] bytes) {
        //System.arraycopy(this.bytes, ptr, bytes, 0, size);
        for (int i = 0; i < markMap.get(ptr).sizeBlock; i++) {
            bytes[i] = this.bytes[i+ptr];
        }
    }

    public void setBytes(int ptr, byte[] bytes) {
        //System.arraycopy(bytes, 0, this.bytes, ptr, size);
        for (int i = 0; i < markMap.get(ptr).sizeBlock; i++) {
            this.bytes[i+ptr] = bytes[i];
        }
    }

    // проверка - не менялся ли указатель
    int checkPtr (int ptr) throws InvalidPointerException {
        if (codeMark.containsKey(ptr)) {
            int tempPtr = codeMark.get(ptr).poll();
            if (codeMark.get(ptr).isEmpty())
                codeMark.remove(ptr);
            ptr = tempPtr;
        } else if (!markMap.containsKey(ptr))
            throw new InvalidPointerException(ptr);
        return ptr;
    }
}
