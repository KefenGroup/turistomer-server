package etu.kefengroup.turistomer.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class GeoLocation {

    private final static double EARTH_RADIUS = 6371.0; // Radius of the Earth in kilometers

    public static GeoLocationRange getRange(double centerLatitude, double centerLongitude, double rangeInKm) {
        double rangeInRadians = rangeInKm / EARTH_RADIUS;

        double centerLatitudeRadians = Math.toRadians(centerLatitude);
        double centerLongitudeRadians = Math.toRadians(centerLongitude);

        double minLatitude = Math.toDegrees(centerLatitudeRadians - rangeInRadians);
        double maxLatitude = Math.toDegrees(centerLatitudeRadians + rangeInRadians);

        double deltaLongitude = Math.asin(Math.sin(rangeInRadians) / Math.cos(centerLatitudeRadians));
        double minLongitude = Math.toDegrees(centerLongitudeRadians - deltaLongitude);
        double maxLongitude = Math.toDegrees(centerLongitudeRadians + deltaLongitude);

        return new GeoLocationRange(minLongitude, maxLongitude, minLatitude, maxLatitude);
    }

    // Inner class to represent a range of geo locations
    @Getter @Setter @ToString
    @AllArgsConstructor
    public static class GeoLocationRange {
        private final double minLongitude;
        private final double maxLongitude;
        private final double minLatitude;
        private final double maxLatitude;
    }
}