package com.example.demo.util;

import com.example.demo.entity.Coordinate;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class CommonUtil {

    public void calculateandplot(DecimalFormat df, Set<Coordinate> coordinates, Double lat1, Double lon1, Double lat2, Double lon2, int i){
        double radius = 6371e3; // metres
        double latDegrees1 = lat1 * Math.PI / 180; // φ, λ in radians
        double latDegrees2 = lat2 * Math.PI / 180;
        double lonran1 = lon1 * Math.PI / 180;
        double lonran2 = lon2 * Math.PI / 180;
        double deltaLatDegrees = (lat2 - lat1) * Math.PI / 180;
        double deltaLonDegrees = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(deltaLatDegrees / 2) * Math.sin(deltaLatDegrees / 2) +
                Math.cos(latDegrees1) * Math.cos(latDegrees2) *
                        Math.sin(deltaLonDegrees / 2) * Math.sin(deltaLonDegrees / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c; // in metres
        double ad= d/ radius;
        while((50*i)<=d) {
            double f = ((50 * i) / d);
            double a1 = (Math.sin((1 - f) * ad)) / Math.sin(ad);
            double a2 = (Math.sin((f) * ad)) / Math.sin(ad);
            double x1 = a1 * Math.cos(latDegrees1) * Math.cos(lonran1) + a2 * Math.cos(latDegrees1) * Math.cos(lonran2);
            double x2 = a1 * Math.cos(latDegrees1) * Math.sin(lonran1) + a2 * Math.cos(latDegrees2) * Math.sin(lonran2);
            double z1 = a1 * Math.sin(latDegrees1) + a2 * Math.sin(latDegrees2);
            double newlat1 = Math.atan2(z1, Math.sqrt(x1 * x1 + x2 * x2));
            double newlon1 = Math.atan2(x2, x1);
            double plot1 = newlat1 * 180 / Math.PI;
            double plot2 = newlon1 * 180 / Math.PI;
            coordinates.add(new Coordinate(Double.parseDouble(df.format(plot1)), Double.parseDouble(df.format(plot2))));
            i++;
        }
    }

    public static Set<Coordinate> decode(final String encodedPath) {
        int len = encodedPath.length();
        final Set<Coordinate> path = new LinkedHashSet<>();
        int index = 0;
        int lat = 0;
        int lng = 0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(5);
        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);
            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new Coordinate(Double.parseDouble(df.format(lat * 1e-5)), Double.parseDouble(df.format(lng* 1e-5))));
        }
        return path;
    }
}
