package edu.fsu.cs.mobile.ontime;

/**
 * Created by Dante on 3/31/2017.
 */

public class Study {
    private String course;
    private String date;
    private String end;
    private String start;

    Study()
    {

    }

    Study(String _course, String _date, String _end, String _start)
    {
        course = _course;
        date = _date;
        end = _end;
        start = _start;
    }

    public String getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }
}
