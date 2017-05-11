package com.macaca.android.testing.server;

import android.app.Instrumentation;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

/**
 * Created by xdf on 07/05/2017.
 */

public class Utils {
    public static void print(String str) {
        Bundle b = new Bundle();
        b.putString(Instrumentation.REPORT_KEY_STREAMRESULT, "\n" + str);
        InstrumentationRegistry.getInstrumentation().sendStatus(0, b);
    }
}