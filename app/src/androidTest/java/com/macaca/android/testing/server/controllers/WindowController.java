package com.macaca.android.testing.server.controllers;

import android.support.test.uiautomator.UiDevice;

import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.models.Response;
import com.macaca.android.testing.server.models.Status;

import com.alibaba.fastjson.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.Map;

/**
 * Created by xdf on 02/05/2017.
 */

public class WindowController extends RouterNanoHTTPD.DefaultHandler {

    public static WindowController getWindow;
    public static WindowController getWindows;
    public static WindowController setWindow;
    public static WindowController deleteWindow;
    public static WindowController getWindowSize;
    public static WindowController setWindowSize;
    public static WindowController maximize;
    public static WindowController setFrame;

    static {
        //TODO
        getWindow = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(new JSONObject(), sessionId).toString());
            }
        };

        //TODO
        getWindows = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        //TODO
        setWindow = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        //TODO
        deleteWindow = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        getWindowSize = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                try {
                    UiDevice mDevice = Elements.getGlobal().getmDevice();
                    Integer width = mDevice.getDisplayWidth();
                    Integer height = mDevice.getDisplayHeight();
                    JSONObject size = new JSONObject();
                    size.put("width", width);
                    size.put("height", height);
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(size, sessionId).toString());
                } catch(Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        //TODO
        setWindowSize = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        //TODO
        maximize = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        //TODO
        setFrame = new WindowController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
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
}