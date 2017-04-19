package edu.fsu.cs.mobile.ontime;

public class Request {
    private String requestKey;
    private String requestSender;

    Request()
    {

    }

    Request(String _requestKey, String _requestSender)
    {
        requestKey = _requestKey;
        requestSender = _requestSender;
    }

    public String getRequestKey() {
        return requestKey;
    }

    public String getRequestSender() {
        return requestSender;
    }
}
