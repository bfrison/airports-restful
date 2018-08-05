package airports_restful;

import javax.swing.DefaultListModel;
import org.json.simple.JSONObject;

public class Airport {

    private String code, name, city, country;
    private int elevation;
    private double latitude, longitude;

    public Airport(String code, String name, String city, String country,
        int elevation, double latitude, double longitude) {
        if (!code.matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid airport code", code));
        }
        this.code = code.toUpperCase();
        this.name = name;
        if (!city.matches("[A-Z]([a-z])+(\\.?)(\\x20[A-Z]([a-z])+){0,2}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid city name", city));
        }
        this.city = city;
        if (!country.matches("[A-Z]([a-z])+(\\.?)(\\x20[A-Z]([a-z])+){0,2}")) {
            throw new IllegalArgumentException(String.format("%s is not a "
                + "valid country name", country));
        }
        this.country = country;
        if (elevation < -10000 || elevation > 10000) {
            throw new IllegalArgumentException(String.format("%d is not a "
                + "valid elevation", elevation));
        }
        this.elevation = elevation;
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException(String.format("%f is not a "
                + "valid latitude", latitude));
        }
        this.latitude = latitude;
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException(String.format("%f is not a "
                + "valid longitude", longitude));
        }
        this.longitude = longitude;
    }

    public static Airport AirportFromJSON(JSONObject jsonObject) {
        String code = (String) jsonObject.get("code");
        String name = (String) jsonObject.get("name");
        String city = (String) jsonObject.get("city");
        String country = (String) jsonObject.get("country");
        int elevation;
        try {
            elevation = Integer.parseInt((String) jsonObject.get("elevation"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not a valid "
                + "integer for elevation", jsonObject.get("elevation")), e);
        }
        double latitude;
        try {
            latitude = Double.parseDouble((String) jsonObject.get("latitude"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not a valid "
                + "double for latitude", jsonObject.get("latitude")), e);
        }
        double longitude;
        try {
            longitude = Double.parseDouble((String) jsonObject.get("longitude"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("%s is not a valid "
                + "double for longitude", jsonObject.get("longitude")), e);
        }
        return new Airport(code, name, city, country, elevation, latitude, longitude);
    }

    public double getDistance(Airport otherAirport) {
        return DistanceCalculator.distance(this.latitude, this.longitude,
            otherAirport.latitude, otherAirport.longitude,
            "K");
    }

    public Airport getNearestAirport(DefaultListModel<Airport> list) {
        double nearestDistance = Double.MAX_VALUE;
        Airport nearestAirport = null;
        Object items[] = list.toArray();
        for (Object item : items) {
            Airport newAirport = (Airport) item;
            double newDistance = DistanceCalculator.distance(this.latitude,
                this.longitude, newAirport.latitude, newAirport.longitude,
                "K");
            if (newDistance < nearestDistance && !newAirport.code.equals(this.code)) {
                nearestDistance = newDistance;
                nearestAirport = newAirport;
            }
        }
        return nearestAirport;
    }

    @Override
    public String toString() {
        return String.format("%s - %s in %s, %s", code, name, city, country);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getElevation() {
        return elevation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    
}
