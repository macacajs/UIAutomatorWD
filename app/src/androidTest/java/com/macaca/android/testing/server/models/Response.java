package com.macaca.android.testing.server.models;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by xdf on 03/05/2017.
 */

public class Response {

    private int status;

    private Object value;

    private String sessionId;

    public Response(Status status, String sessionId) {
        this.status = status.getStatusCode();
        this.value = status.getStatusDes();
        this.sessionId = sessionId;
    }

    public Response(JSONObject value, String sessionId) {
        this.status = 0;
        this.value = value;
        this.sessionId = sessionId;
    }

    public Response(JSONArray value, String sessionId) {
        this.status = 0;
        this.value = value;
        this.sessionId = sessionId;
    }

    public Response(String value, String sessionId) {
        this.status = 0;
        this.value = value;
        this.sessionId = sessionId;
    }

    public Response(boolean value, String sessionId) {
        this.status = 0;
        this.value = value;
        this.sessionId = sessionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getValue() {
        return value;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        JSONObject res = new JSONObject();
        try {
            res.put("status", this.getStatus());
            res.put("value", this.getValue());
            res.put("sessionId", this.getSessionId());
            return res.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
