package com.macaca.android.testing.server.controllers;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

import com.alibaba.fastjson.JSONObject;
import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.models.Response;
import com.macaca.android.testing.server.models.Status;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.util.List;
import java.util.Map;

/**
 * Created by xdf on 02/05/2017.
 */

public class AlertController extends RouterNanoHTTPD.DefaultHandler {

    public static AlertController acceptAlert;
    public static AlertController dismissAlert;
    public static AlertController alertText;
    public static AlertController alertKeys;

    static {
        acceptAlert = new AlertController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                JSONObject result = null;
                try {
                    acceptAlert();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        dismissAlert = new AlertController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                JSONObject result = null;
                try {
                    dismissAlert();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        //TODO
        alertText = new AlertController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
            }
        };

        //TODO
        alertKeys = new AlertController() {
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

    private static UiObject2 getAlertButton(String alertType) throws Exception {
        UiDevice mDevice = Elements.getGlobal().getmDevice();
        int buttonIndex;
        if (alertType.equals("accept")) {
            buttonIndex = 1;
        } else if (alertType.equals("dismiss")) {
            buttonIndex = 0;
        } else {
            throw new Exception("alertType can only be 'accept' or 'dismiss'");
        }

        List<UiObject2> alertButtons = mDevice.findObjects(By.clazz("android.widget.Button").clickable(true).checkable(false));
        if (alertButtons.size() == 0) {
            return null;
        }
        UiObject2 alertButton = alertButtons.get(buttonIndex);

        return alertButton;
    }

    private static void acceptAlert() throws Exception {
        UiObject2 alertButton = getAlertButton("accept");
        if (alertButton != null) {
            alertButton.click();
        }
    }

    private static void dismissAlert() throws Exception {
        UiObject2 alertButton = getAlertButton("dismiss");
        if (alertButton != null) {
            alertButton.click();
        }
    }
}