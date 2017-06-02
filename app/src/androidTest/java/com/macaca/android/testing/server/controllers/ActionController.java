package com.macaca.android.testing.server.controllers;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.test.uiautomator.UiDevice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.macaca.android.testing.server.Utils;
import com.macaca.android.testing.server.common.Element;
import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.models.Response;
import com.macaca.android.testing.server.models.Status;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xdf on 02/05/2017.
 */

public class ActionController extends RouterNanoHTTPD.DefaultHandler {

    public static ActionController actions;

    private static UiDevice mDevice = Elements.getGlobal().getmDevice();
    private static Elements elements = Elements.getGlobal();

    static {
        actions = new ActionController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                Map<String, String> body = new HashMap<String, String>();
                JSONObject response = null;
                try {
                    session.parseBody(body);
                    String postData = body.get("postData");
                    JSONObject jsonObj = JSON.parseObject(postData);
                    JSONArray actions = (JSONArray)jsonObj.get("actions");
                    List<JSONArray> queue = new ArrayList<JSONArray>();
                    String lastGesture = "";
                    for (int i = 0; i < actions.size(); i++) {
                        JSONObject action = actions.getJSONObject(i);
                        String type = action.getString("type");
                        if (type.isEmpty()) {
                            continue;
                        }
                        if (type.equals(lastGesture)) {
                            if (queue.size() > 0) {
                                JSONArray arr = queue.get(queue.size() - 1);
                                arr.add(action);
                            }
                        } else {
                            JSONArray arr = new JSONArray();
                            arr.add(action);
                            queue.add(arr);
                        }

                        lastGesture = type;
                    }
                    for (JSONArray action: queue) {
                        JSONObject first = action.getJSONObject(0);
                        String type = first.getString("type");
                        boolean result = false;
                        if (type.equals("tap")) {
                            result = tap(action);
                        } else if (type.equals("doubleTap")) {
                            result = doubleTap(action);
                        } else if (type.equals("press")) {
                            result = press(action);
                        } else if (type.equals("pinch")) {
                            result = pinch(action);
                        } else if (type.equals("drag")) {
                            result = drag(action);
                        }
                        if(!result) {
                            throw new Exception("Fail to execute action: " + type);
                        }
                    }
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(response, sessionId).toString());
                } catch (final Exception e) {
                    Utils.print(e.getMessage());
                    e.printStackTrace();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };
    }

    @Override
    public String getMimeType() {
        return "";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    private static boolean tap(JSONObject args) throws Exception {
        String elementId = args.getString("element");
        if (elementId != null) {
            Element el = elements.getElement(elementId);
            return el.tap();
        } else {
            int x = args.getInteger("x");
            int y = args.getInteger("y");
            return mDevice.click(x, y);
        }
    }

    private static boolean tap(JSONArray actions) throws Exception {
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if(!tap(action)) {
                return false;
            }
        }
        return true;
    }

    private static boolean doubleTap(JSONObject args) throws Exception {
        String elementId = args.getString("element");
        if (elementId != null) {
            Element el = elements.getElement(elementId);
            return el.doubleTap();
        } else {
            int x = args.getInteger("x");
            int y = args.getInteger("y");
            mDevice.click(x, y);
            Thread.sleep(100);
            mDevice.click(x, y);
            return true;
        }
    }

    private static boolean doubleTap(JSONArray actions) throws Exception {
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if(!doubleTap(action)) {
                return false;
            }
        }
        return true;
    }

    private static boolean press(JSONObject args) throws Exception {
        String elementId = args.getString("element");
        double duration = args.getDoubleValue("duration");
        int steps = (int) Math.round(duration * 40);
        if (elementId != null) {
            Element el = elements.getElement(elementId);
            Rect elRect = el.getUiObject().getVisibleBounds();
            return mDevice.swipe(elRect.centerX(), elRect.centerY(),  elRect.centerX(), elRect.centerY(), steps);
        } else {
            int x = args.getInteger("x");
            int y = args.getInteger("y");
            return mDevice.swipe(x, y, x, y, steps);
        }
    }

    private static boolean press(JSONArray actions) throws Exception {
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if(!press(action)) {
                return false;
            }
        }
        return true;
    }

    private static boolean pinch(JSONObject args) throws Exception {
        String elementId = args.getString("element");
        Element el;
        if (elementId == null) {
            el = elements.getElement("1");
        } else {
            el = elements.getElement(elementId);
        }
        String direction = args.getString("direction");
        float percent = args.getFloat("percent");
        int steps = args.getInteger("steps");
        return el.pinch(direction, percent, steps);
    }

    private static boolean pinch(JSONArray actions) throws Exception {
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if(!pinch(action)) {
                return false;
            }
        }
        return true;
    }

    private static boolean drag(JSONObject args) throws Exception {
        String elementId = args.getString("element");
        Double fromX = args.getDouble("fromX");
        Double fromY = args.getDouble("fromY");
        Double toX = args.getDouble("toX");
        Double toY = args.getDouble("toY");
        double duration = args.getDoubleValue("duration");
        int steps = (int) Math.round(duration * 40);
        if (elementId != null) {
            Element el = elements.getElement(elementId);
            return el.drag(toX.intValue(), toY.intValue(), steps);
        } else {
            boolean res = mDevice.drag(fromX.intValue(), fromY.intValue(), toX.intValue(), toY.intValue(), steps);
            Thread.sleep(steps * 100);
            return res;
        }
    }

    private static boolean drag(JSONArray actions) throws Exception {
        if (actions.size() == 1) {
            JSONObject action = actions.getJSONObject(0);
            return drag(action);
        }

        Point[] allPoint = new Point[actions.size() + 1];
        int steps = 0;
        for (int i = 0; i < actions.size(); i++) {
            JSONObject action = actions.getJSONObject(i);
            if (i == 0) {
                String elementId = action.getString("element");
                steps = action.getInteger("steps");
                if (steps == 0) {
                    double duration = action.getDoubleValue("duration");
                    steps = (int) Math.round(duration * 40);
                }
                if (elementId != null) {
                    Element el = elements.getElement(elementId);
                    Rect elRect = el.getUiObject().getVisibleBounds();
                    Point p = new Point(elRect.centerX(), elRect.centerY());
                    allPoint[0] = p;
                } else {
                    Double fromX = action.getDouble("fromX");
                    Double fromY = action.getDouble("fromY");
                    Point p = new Point(fromX.intValue(), fromY.intValue());
                    allPoint[0] = p;
                }
            }
            Double toX = action.getDouble("toX");
            Double toY = action.getDouble("toY");
            Point p = new Point(toX.intValue(), toY.intValue());
            allPoint[i + 1] = p;
        }
        return mDevice.swipe(allPoint, steps);
    }
}
