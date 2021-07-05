package progwards.java2.lessons.patterns;

public interface Speed {
    //парсинг gps-трекера в xml
    public void parse (String name);
    //расстояние между двумя точками в метрах
    public double distBetween(GPS gps1, GPS gps2);
    //скорость между двумя точками
    public double speed (GPS gps);
    //обработка точек
    public void track (String name);
}
