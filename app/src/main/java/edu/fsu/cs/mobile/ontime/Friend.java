package edu.fsu.cs.mobile.ontime;

public class Friend {
    private String friendKey;
    private String friendName;

    Friend()
    {

    }

    Friend(String _friendKey, String _friendName)
    {
        friendKey = _friendKey;
        friendName = _friendName;
    }

    public String getFriendKey() {
        return friendKey;
    }

    public String getFriendName() {
        return friendName;
    }
}
