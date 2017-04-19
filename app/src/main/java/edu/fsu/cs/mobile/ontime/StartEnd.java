package edu.fsu.cs.mobile.ontime;

public class StartEnd
{
    private String days;
    private String start;
    private String end;

    StartEnd()
    {

    }

    StartEnd(String _days, String _start, String _end)
    {
        days = _days;
        start = _start;
        end = _end;
    }

    public String getDays() {
        return days;
    }

    public String getStart()
    {
        return start;
    }

    public String getEnd()
    {
        return end;
    }
}
