package com.macaca.android.testing;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.macaca.android.testing.server.Utils;
import com.macaca.android.testing.server.common.Elements;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UIAutomatorWD {
    @Test
    public void MacacaTestRunner() throws Exception {
        Bundle args = InstrumentationRegistry.getArguments();

        int port = 9001;
        if (args.containsKey("port")) {
            port = Integer.parseInt(args.getString("port"));
        }

        UIAutomatorWDServer server = UIAutomatorWDServer.getInstance(port);
        Utils.print("UIAutomatorWD->" + "http://localhost:" + server.getListeningPort() + "<-UIAutomatorWD");

        if (args.containsKey("permissionPattern")) {
            JSONArray permissionPatterns = JSON.parseArray(args.getString("permissionPattern"));
            skipPermission(permissionPatterns, 15);
        }

        while (true) {
            SystemClock.sleep(1000);
        }
    }

    public void skipPermission(JSONArray permissionPatterns, int scanningCount) {
        UiDevice mDevice = Elements.getGlobal().getmDevice();

        // if permission list is empty, avoid execution
        if (permissionPatterns.size() == 0) {
            return;
        }

        // regular check for permission scanning
        try {
            for (int i = 0; i < scanningCount; i++) {
                inner:
                for (int j = 0; j < permissionPatterns.size(); j++) {
                    String text = permissionPatterns.getString(j);
                    UiObject object = mDevice.findObject(new UiSelector().text(text));
                    if (object.exists()) {
                        object.click();
                        break inner;
                    }
                }

                Thread.sleep(3000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().toString());
        }
    }
}