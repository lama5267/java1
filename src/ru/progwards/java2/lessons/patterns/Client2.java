package progwards.java2.lessons.patterns;

public class Client2 {
    public static void main(String[] args) {
        SpeedGps speedGPS = new SpeedGps();
        FilterSpeedGps filterSpeedGPS = new FilterSpeedGps(speedGPS);
        filterSpeedGPS.track("D:/geoObjects.gpx");


    }
}