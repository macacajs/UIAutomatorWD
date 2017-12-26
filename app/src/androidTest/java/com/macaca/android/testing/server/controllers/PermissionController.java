package com.macaca.android.testing.server.controllers;

import android.os.AsyncTask;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.models.Response;
import com.macaca.android.testing.server.models.Status;

import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * created by Zhao Yue, at 2017/12/25 - 下午9:54
 * for further issue, please contact: zhaoy.samuel@gmail.com
 */

public class PermissionController extends RouterNanoHTTPD.DefaultHandler {

    public static PermissionController skipPermission;
    protected UiDevice mDevice;


    static {
        skipPermission = new PermissionController() {
            @Override
            public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                try {
                    mDevice = Elements.getGlobal().getmDevice();
                    Map<String, String> body = new HashMap<>();
                    session.parseBody(body);
                    String postData = body.get("postData");
                    JSONObject jsonObj = JSON.parseObject(postData);
                    new ScanPermissinAsyncTask().execute(jsonObj);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println(e.getCause().toString());
                }

                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.Success, "").toString());
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

    private class ScanPermissinAsyncTask extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            System.out.println("async execution of tasks");
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(JSONObject jsonObj) {
            super.onPostExecute(jsonObj);
            try {
                JSONArray texts = (JSONArray) jsonObj.get("value");
                int repeat = jsonObj.getInteger("repeat");
                for (int i = 0; i < repeat; i++) {
                    iteratePermissionTexts(texts);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        private void iteratePermissionTexts(JSONArray texts) throws Exception {
            for (int j = 0; j < texts.size(); j++) {
                String text = texts.getString(j);
                UiObject object = mDevice.findObject(new UiSelector().text(text));
                if (object.exists()) {
                    object.click();
                    break;
                }
            }

            Thread.sleep(3000);
        }
    }
}



