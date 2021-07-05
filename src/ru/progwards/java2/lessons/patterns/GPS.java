package progwards.java2.lessons.patterns;

public class GPS {
    public double lat; // широта
    public double lon; // долгота
    public long time; // время в мс

    public GPS(double lat, double lon, long time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }
}
