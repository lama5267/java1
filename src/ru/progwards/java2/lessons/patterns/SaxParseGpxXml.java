package progwards.java2.lessons.patterns;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// парсит файл *.gpx (xml), выбирая широту, долготу и время

public class SaxParseGpxXml {
    private static ArrayList<GPS> gpspoints = new ArrayList<>();

    public static ArrayList<GPS> parsing(String name) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        XMLHandler handler = new XMLHandler();
        try {
            parser = factory.newSAXParser();
            parser.parse(new File(name), handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return gpspoints;
    }

    private static class XMLHandler extends DefaultHandler {
        Double lat, lon;
        Long time;
        private String elementName;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes){
            elementName = qName;
            if (qName.equals("trkpt")) {
                lat = Double.parseDouble(attributes.getValue("lat"));
                lon = Double.parseDouble(attributes.getValue("lon"));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length){
            String value = new String(ch, start, length);
            value = value.replace("\n", "").trim();
            DateTimeFormatter format;
            if (!value.isEmpty()) {
                if (elementName.equals("time")) {
                    format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.from(format.parse(value)), ZoneId.systemDefault());
                    time = zdt.toEpochSecond();
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            if (qName.equals("trkpt"))
                gpspoints.add(new GPS(lat, lon, time));
        }
    }
}
