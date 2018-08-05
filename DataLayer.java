package airports_restful;

import static airports_restful.Airport.AirportFromJSON;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataLayer {
    final static String BASE_URL = "http://localhost:8000";

    private enum Command {
        GET, POST, PUT, DELETE
    };
    
    static String getDataFromUrl(String url) throws MalformedURLException, IOException, ParseException {
        URL u = new URL(BASE_URL + url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("GET");
        // we only really establish connection in the line below
        if (conn.getResponseCode() / 100 != 2) {
            throw new RuntimeException("Failed with HTTP error code " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String output = "";
        String line = null;
        while ((line = br.readLine()) != null) {
            output += line;
        }
        conn.disconnect();
        return output;
    }

    static void sendDataToUrl(Airport airport, Command command, String pk) throws MalformedURLException, IOException {
        JSONObject jo = new JSONObject();
        jo.put("code", airport.getCode());
        jo.put("name", airport.getName());
        jo.put("city", airport.getCity());
        jo.put("country", airport.getCountry());
        jo.put("elevation", airport.getElevation());
        jo.put("latitude", airport.getLatitude());
        jo.put("longitude", airport.getLongitude());
        String data = jo.toJSONString();
        String commandString = null;

        switch (command) {
            case PUT:
                commandString = "PUT";
                break;
            case POST:
                commandString = "POST";
                break;
        }

        //
        URL u = null;
        switch (command) {
            case PUT:
                u = new URL(BASE_URL + "/airports/json/" + pk);
                break;
            case POST:
                u = new URL(BASE_URL + "/airports/json");
                break;
        }
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();//only prepares connection
        conn.setRequestMethod(commandString);
        conn.setDoOutput(true);
        try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
            outputStream.writeBytes(data);
            outputStream.flush();
        }
        // we only really establish connection in the line below
        if (conn.getResponseCode() / 100 != 2) {
            throw new RuntimeException("Failed with HTTP error code " + conn.getResponseCode());
        }
        conn.disconnect();
    }

    static JSONArray toJSONArray(String s) throws IOException, ParseException {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(s);
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            throw new IOException("Class cast exception while parsing JSON. "
                + "Possibly invalid JSON structure", ex);
        }
    }

    static JSONObject toJSONObject(String s) throws IOException {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(s);
        } catch (ParseException ex) {
            ex.printStackTrace();
            throw new IOException("Class cast exception while parsing JSON. "
                + "Possibly invalid JSON structure", ex);
        }
    }

    static List<Airport> getAllAirports() throws IOException, MalformedURLException, ParseException {
        List<Airport> airportList = new ArrayList<>();
        String airportsString;
        JSONArray ja;
        airportsString = getDataFromUrl("/airports/json");
        ja = toJSONArray(airportsString);
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            //The try-catch ensures only properly formed airports are displayed
            try {
            airportList.add(AirportFromJSON(jo));
            } catch (IllegalArgumentException e) {
            }
        }
        return airportList;
    }
    
    //The method below is used for testing
    static Airport getAirport(String code) throws IOException, MalformedURLException, ParseException {
        String airportsString;
        JSONObject jo;
        airportsString = getDataFromUrl("/airports/json/" + code);
        jo = toJSONObject(airportsString);
        return AirportFromJSON(jo);
    }

    static void createAirport(Airport airport) throws MalformedURLException, IOException {
        sendDataToUrl(airport, Command.POST, null);
    }

    static void updateAirport(Airport airport, String code) throws IOException, MalformedURLException {
        sendDataToUrl(airport, Command.PUT, code);
    }

    static void deleteAirport(String code) throws MalformedURLException, IOException {
        URL u = new URL(BASE_URL + "/airports/json/" + code);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestMethod("DELETE");
        if (conn.getResponseCode() / 100 != 2) {
            throw new RuntimeException("Failed with HTTP error code " + conn.getResponseCode());
        }
        conn.disconnect();
    }
}
