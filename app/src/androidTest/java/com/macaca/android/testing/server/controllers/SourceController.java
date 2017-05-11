package com.macaca.android.testing.server.controllers;

import android.os.Environment;
import android.support.test.uiautomator.UiDevice;

import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.models.Response;

import org.apache.http.util.EncodingUtils;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


/**
 * Created by xdf on 02/05/2017.
 */

public class SourceController extends RouterNanoHTTPD.DefaultHandler {

    public static SourceController source;
    UiDevice mDevice = Elements.getGlobal().getmDevice();

    static {
        source = new SourceController() {
            private static final String dumpFileName = "macaca-dump.xml";

            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                final File dump = new File(Environment.getDataDirectory() + File.separator + "local" + File.separator + "tmp" + File.separator + dumpFileName);
                try {
                    mDevice.dumpWindowHierarchy(dump);
                } catch (IOException e) {
                    System.out.print(e);
                }
                dump.setReadable(true);
                String res = "";
                try {
                    FileInputStream fin = new FileInputStream(dump.getAbsolutePath());
                    int length = fin.available();
                    byte[] buffer = new byte[length];
                    fin.read(buffer);
                    res = EncodingUtils.getString(buffer, "UTF-8");
                    fin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(res, sessionId).toString());
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