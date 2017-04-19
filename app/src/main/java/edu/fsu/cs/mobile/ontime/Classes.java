package edu.fsu.cs.mobile.ontime;

public class Classes {
    private String building;
    private String course;
    private String days;
    private String endTime;
    private String room;
    private String startTime;

    Classes()
    {

    }

    Classes(String _building, String _course, String _days, String _endTime, String _room, String _startTime)
    {
        building = _building;
        course = _course;
        days = _days;
        endTime = _endTime;
        room = _room;
        startTime = _startTime;
    }

    public String getDays() {
        return days;
    }

    public String getBuilding() {
        return building;
    }

    public String getCourse() {
        return course;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    public String getStartTime() {
        return startTime;
    }
}
