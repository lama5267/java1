package progwards.java2.lessons.gc;

import java.util.*;

public class Heap {
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

    private byte[] bytes;
    TreeMap<Integer, TreeSet<Integer>> freeBlock = new TreeMap<>();      // количество/множество указателей
    TreeMap<Integer, Mark> markMap = new TreeMap<>();                    // указатель/размер блока и свободен ли
    HashMap<Integer, LinkedList<Integer>> codeMark = new HashMap<>();    // перекодированные указатели
    TreeMap<Integer, Integer> freeMarks = new TreeMap<>();
    Integer markBlock;

    Heap(int maxHeapSize) {
        bytes = new byte[maxHeapSize];
        freeBlock.put(bytes.length, new TreeSet<>(Set.of(0)));
    }

    public int malloc(int size) throws OutOfMemoryException {
        Integer sizeBlock = freeBlock.ceilingKey(size);             // находим подходящий блок
        if (sizeBlock == null) {
            compact();
            sizeBlock = freeBlock.ceilingKey(size);
            if (sizeBlock == null)
                throw new OutOfMemoryException();
        }
        markBlock = freeBlock.get(sizeBlock).pollFirst();           // получаем его указатель
        if (freeBlock.get(sizeBlock).isEmpty())                     // если дальше указателей на блок такого размера нет - удаляем
            freeBlock.remove(sizeBlock);
        markMap.put(markBlock, new Mark(size));                     // в указатель записанных
        if (size != sizeBlock)                                        // если блок больше заданного
            addFreeBlock(sizeBlock-size, markBlock + size); // добавляем остаток в свободные блоки
//        setBytes(markBlock, bytes);                              // имитация копирования
        return markBlock;
    }

    public void free(int ptr) throws InvalidPointerException {
        if (codeMark.containsKey(ptr)) {                           // проверка - не менялся ли указатель
            int tempPtr = codeMark.get(ptr).poll();
            if (codeMark.get(ptr).isEmpty())
                codeMark.remove(ptr);
            ptr = tempPtr;
        } else if (!markMap.containsKey(ptr))
            throw new InvalidPointerException();
//        getBytes(ptr, bytes);                                       // имитация копирования
        addFreeBlock(markMap.get(ptr).sizeBlock, ptr);
        markMap.remove(ptr);
    }

    public void addFreeBlock (int sizeB, int mark) {
        TreeSet<Integer> tempSet = (freeBlock.containsKey(sizeB)) ? freeBlock.get(sizeB) : new TreeSet<>();
        tempSet.add(mark);
        freeBlock.put(sizeB, tempSet);
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
        addFreeBlock(bytes.length-nowMark, nowMark);
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
}
