package com.example.demo.entity;


public class Coordinate {

    private Double latitude;
    private Double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Coordinate){
            Coordinate coordinate = (Coordinate) o;
            return (Double.compare(coordinate.getLatitude(), latitude) ==0  && Double.compare(coordinate.getLongitude(), longitude)==0);
        }
        return false;
    }

    @Override
    public int hashCode(){
        Double d = (latitude*100000+ longitude*100000);
        return d.intValue();
    }
}
