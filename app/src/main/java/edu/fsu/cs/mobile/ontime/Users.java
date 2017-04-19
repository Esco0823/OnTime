package edu.fsu.cs.mobile.ontime;

public class Users
{
    private String alertDistance;
    private String latitude;
    private String longitude;
    private String password;
    private String phone;
    private String visible;


    Users()
    {

    }

    Users(String _alertDistance, String _latitude, String _longitude, String _password, String _phone, String _visible)
    {
        alertDistance = _alertDistance;
        latitude = _latitude;
        longitude = _longitude;
        password = _password;
        phone = _phone;
        visible = _visible;
    }

    public String getAlertDistance() { return alertDistance; }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPhone() { return phone; }

    public String getPassword()
    {
        return password;
    }

    public String getVisible()
    {
        return visible;
    }

}