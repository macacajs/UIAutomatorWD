package com.macaca.android.testing;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.macaca.android.testing.server.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UIAutomatorWD {
    @Test
    public void MacacaTestRunner() throws Exception {
        Bundle args = InstrumentationRegistry.getArguments();

        int port = 9001;
        if (args.get("port") != null) {
            port = Integer.parseInt((String)args.get("port"));
        }
        UIAutomatorWDServer server = UIAutomatorWDServer.getInstance(port);
        Utils.print("UIAutomatorWD->" + "http://localhost:" + server.getListeningPort() + "<-UIAutomatorWD");
        while (true) {
            SystemClock.sleep(1000);
        }
    }

}
