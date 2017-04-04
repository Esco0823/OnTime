package edu.fsu.cs.mobile.ontime;

/**
 * Created by Dante on 3/30/2017.
 */

public class Users {
    private String latitude;
    private String longitude;
    private String password;
    private String visible;


    Users()
    {

    }

    Users(String _latitude, String _longitude, String _password, String _visible)
    {
        latitude = _latitude;
        longitude = _longitude;
        password = _password;
        visible = _visible;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPassword()
    {
        return password;
    }

    public String getVisible()
    {
        return visible;
    }

}