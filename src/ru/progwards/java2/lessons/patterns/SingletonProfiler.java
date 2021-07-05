package progwards.java2.lessons.patterns;

import java.util.*;

public enum  SingletonProfiler {
    INSTANSE ();

    private long timeTemp = 0;   // - время работы вложенной секции
    private Integer countVlozSec = 0;  // - счетчик работающих вложенных секций
    private boolean vlozInc;                      // вход в новую секцию?

    private TreeMap<String, StatisticInfo> profilMapResult = new TreeMap<>();
    private Map<String, Long> rabota = new TreeMap<>();
    private Map <String, LinkedList<Long>> vlozTempTime = new HashMap<>();

    SingletonProfiler(){}


    public void enterSection(String name, Thread thread) {
        name += " " + thread.getName();
        countVlozSec++;
        if (countVlozSec > 1 && !vlozInc) {    // если при открытых секциях после закрытия не всех снова пошёл вход
            for (var entry: vlozTempTime.entrySet()) {           // сохраняем время работы вложенных секций до этого
                for (Long time: entry.getValue()) {
                    time += timeTemp;
                }
            }
            timeTemp = 0;
        }
        LinkedList <Long> list = vlozTempTime.containsKey(name) ? vlozTempTime.get(name): new LinkedList<>();
        list.push(0L);
        vlozTempTime.put(name, list);
        rabota.put(name, System.nanoTime());
        vlozInc = true;
    }

    public void exitSection(String name, Thread thread) {
        name += " " + thread.getName();
        countVlozSec--;
        long fulltimeS = System.nanoTime()-rabota.get(name);
        long selftimeS = fulltimeS - timeTemp - vlozTempTime.get(name).pop();
        if (vlozTempTime.get(name).isEmpty())
            vlozTempTime.remove(name);
        timeTemp = countVlozSec != 0 ? fulltimeS : 0;

        StatisticInfo stillNot = profilMapResult.putIfAbsent(name, new StatisticInfo(name, fulltimeS, selftimeS, 1));
        if (stillNot != null) {
            profilMapResult.get(name).fullTime += fulltimeS;
            profilMapResult.get(name).selfTime += selftimeS;
            profilMapResult.get(name).count++;
        }
        vlozInc = false;
    }

    public List<StatisticInfo> getStatisticInfo() {
        ArrayList<StatisticInfo> itog = new ArrayList<>();
        for (var entry: profilMapResult.entrySet())
            itog.add(entry.getValue());
        return itog;
    }
}
