package progwards.java2.lessons.sort;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class ExternalSort {
    static int countNum = 0;            // счетчик чисел записанных в память
    static int countFile = 0;           // счетчик записанных файлов
    private static final int MEMORY_SIZE = 10000;    // размер памяти
    private static final String TXT = ".txt";
    static Integer[] memory = new Integer [MEMORY_SIZE]; // массив чисел, считанных в память при обработке data.txt
    static final Path ROOT = Path.of(System.getProperty("user.dir"));
    static ArrayList<LinkedList<Integer>> mergeList = new ArrayList<>();  //массив чисел в памяти при слиянии файлов

    public static void sort (String inFileName, String outFileName) {
        // создаем временный каталог
        File newDir = new File(ROOT + "/Sort");
        boolean dir = newDir.mkdir();
        //разбиваем на размер памяти и сортируем
        splitDataAndSort(inFileName);
        // если нет возможности взять из каждого файла хотя бы по числу на половину памяти - объединяем пары
        while (countFile > MEMORY_SIZE)
            tempMerge(newDir);
        // когда файлов достаточно для записи хотя бы одно числа в память из каждого - проводим объединение
        mergeSortFile(0, countFile, ROOT + "/" + outFileName);
        deleteTempFile(newDir, dir);
    }

    // загрузка большого файла в память по частям, сортировка и запись в файлы
    public static void splitDataAndSort (String inFileName) {
        String fileTemp;
        try (FileReader reader = new FileReader(inFileName); Scanner scanner = new Scanner(reader)){
            while (scanner.hasNextLine()) {
                // считываем число в память
                memory[countNum] = Integer.valueOf(scanner.nextLine());
                // если память заполнена - сортируем и записываем в файл, при обратном - увеличиваем счетчик
                if (countNum == MEMORY_SIZE-1) {
                    fileTemp = "TempSort" + countFile;
                    sortAndWrite(fileTemp, countNum);
                } else
                    countNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // сотрируем и записываем в файл остатки, если память заполнена не полностью
        if (countNum !=0) {
            fileTemp = "TempSort" + countFile;
            sortAndWrite(fileTemp, countNum - 1);
        }
    }

    //объединение пар файлов, если количество файлов больше размера памяти
    public static void tempMerge (File newDir) {
        int countNewFile = 0;
        String fileTemp;
        //объединяем пары файлов
        for (int i=0; i< countFile; i+=2) {
            fileTemp = "TempSort";
            // если есть пара - соединяем
            if (i < countFile-1 && i!=0) {
                mergeSortFile(i, 2, newDir + "/" + fileTemp + (countNewFile) + TXT);
            } else try {
                if (i == 0) {
                    mergeSortFile(i, 2, newDir + "/" + fileTemp + (countFile) + TXT);
                    // копируем результат в первый файл (только для первой пары)
                    Files.copy(new File(newDir + "/" + fileTemp + (countFile) + TXT).toPath(), new File(newDir + "/" + fileTemp + 0 + TXT).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else
                    // оставшийся непарный файл просто копируем ближе к остальным
                    Files.copy(new File(newDir + "/" + fileTemp + i + TXT).toPath(), new File(newDir + "/" + fileTemp + countNewFile + TXT).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException e) {
                e.printStackTrace();
            }
            countNewFile++;
        }
        // обновляем текущее количество нужных файлов
        countFile = countNewFile;
    }

    // сбалансированное многопутевое слияние
    public static void mergeSortFile (int startFile, int allFile, String writeFile) {
        String fileTemp = "TempSort";
        Integer numberIndex = 0;
        //очищаем файл для последующей записи
        try {
            new FileWriter(writeFile).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // создаем массивы reader и scanner
        FileReader [] readers = new FileReader[allFile];
        Scanner [] scanners = new Scanner[allFile];
        for (int i=0; i<allFile; i++) {
            try {
                readers[i] = new FileReader(ROOT+ "/Sort/" +fileTemp + (i + startFile) +".txt");
                scanners[i] = new Scanner(readers[i]);
                mergeList.add(new LinkedList<>());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // сколько записей из файла можем взять за один раз и заполняем в соответствии с этим половину память
        int rate = MEMORY_SIZE/allFile;
        for (Scanner filescanner: scanners) {
            for (int j=0; j<rate; j++) {
                if (filescanner.hasNextLine()) {
                    mergeList.get(numberIndex).addLast(Integer.valueOf(filescanner.nextLine()));
                } else
                    mergeList.get(numberIndex).addLast(null);
            }
            numberIndex++;
        }

        // сравниваем и пишем в файл результат
        try (FileWriter writer = new FileWriter(writeFile, true)) {
            while (numberIndex != null) {
                int currentIndex = 0;
                numberIndex = null;
                for (LinkedList<Integer> list : mergeList) {
                    // еcли есть для сравнения два числа - сравниваем
                    if (numberIndex != null && list.peek() != null) {
                        if (mergeList.get(numberIndex).peek() > list.peek()) {
                            numberIndex = currentIndex;
                        }
                    } else if (list.peek() != null)   // если это первое число - берем его индекс
                        numberIndex = currentIndex;
                    currentIndex++;
                }
                if (numberIndex != null) {
                    // пишем в результирующий файл и если надо добавляем часть данных из источника
                    writer.write(mergeList.get(numberIndex).pop() + "\n");
                    if (mergeList.get(numberIndex).isEmpty()) {
                        // загрузить новую часть данных
                        for (int j=0; j<rate; j++) {
                            if (scanners[numberIndex].hasNextLine()) {
                                mergeList.get(numberIndex).addLast(Integer.valueOf(scanners[numberIndex].nextLine()));
                            } else {
                                mergeList.get(numberIndex).addLast(null);
                                break;
                            }
                        }
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        for (int i=0; i<allFile; i++) {
            try {
                readers[i].close();
                scanners[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mergeList.clear();
    }

    // быстрая сортировка и запись в файл
    public static void sortAndWrite (String fileName, int endIndex) {
        QuickSort.sortHoare(memory, 0, endIndex);
        try (FileWriter writer = new FileWriter(ROOT + "/Sort/" + fileName + ".txt", false)) {
            for (int i = 0; i <= endIndex; i++) {
                writer.write(memory[i] + "\n");
                memory[i] = null;
            }
            countNum = 0;
            countFile++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // удаление временных файлов и каталога
    public static void deleteTempFile (File newDir, boolean dir) {
        Path directory = Paths.get(String.valueOf(newDir));

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**/TempSort*.txt");
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    if (pathMatcher.matches(path))
                        Files.delete(path);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path directory, IOException ioException) throws IOException {
                    if (dir)
                        Files.delete(directory);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sort("data.txt", "Sorted.txt");
    }
}
