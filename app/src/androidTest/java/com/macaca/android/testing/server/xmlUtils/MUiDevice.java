package com.macaca.android.testing.server.xmlUtils;

/**
 * Created by xdf on 09/05/2017.
 */

import android.app.Instrumentation;
import android.os.SystemClock;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.view.accessibility.AccessibilityNodeInfo;

import com.macaca.android.testing.server.common.Elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MUiDevice {

    private static final String FIELD_M_INSTRUMENTATION = "mInstrumentation";
    private static final String FIELD_API_LEVEL_ACTUAL = "API_LEVEL_ACTUAL";

    private static MUiDevice INSTANCE = new MUiDevice();
    private Method METHOD_FIND_MATCH;
    private Method METHOD_FIND_MATCHS;
    private Class ByMatcher;
    private Instrumentation mInstrumentation;
    private Object API_LEVEL_ACTUAL;

    private UiDevice uiDevice = Elements.getGlobal().getmDevice();

    /**
     * UiDevice in android open source project will Support multi-window searches for API level 21,
     * which has not been implemented in UiAutomatorViewer capture layout hierarchy, to be in sync
     * with UiAutomatorViewer customizing getWindowRoots() method to skip the multi-window search
     * based user passed property
     */
    public MUiDevice() {
        try {
            this.mInstrumentation = (Instrumentation) ReflectionUtils.getField(UiDevice.class, FIELD_M_INSTRUMENTATION, uiDevice);
            this.API_LEVEL_ACTUAL = ReflectionUtils.getField(UiDevice.class, FIELD_API_LEVEL_ACTUAL, uiDevice);
            METHOD_FIND_MATCH = ReflectionUtils.method("android.support.test.uiautomator.ByMatcher", "findMatch", UiDevice.class, BySelector.class, AccessibilityNodeInfo[].class);
            METHOD_FIND_MATCHS = ReflectionUtils.method("android.support.test.uiautomator.ByMatcher", "findMatches", UiDevice.class, BySelector.class, AccessibilityNodeInfo[].class);
            ByMatcher = ReflectionUtils.getClass("android.support.test.uiautomator.ByMatcher");
        } catch (Error error) {
            throw error;
        } catch (Exception error) {
            throw new Error(error);
        }
    }

    public static MUiDevice getInstance() {
        return INSTANCE;
    }

    private UiObject2 doFindObject(Object selector, AccessibilityNodeInfo node) throws Exception {

        Class uiObject2 = Class.forName("android.support.test.uiautomator.UiObject2");
        Constructor cons = uiObject2.getDeclaredConstructors()[0];
        cons.setAccessible(true);
        Object[] constructorParams = {uiDevice, selector, node};

        final long timeoutMillis = 1000;
        long end = SystemClock.uptimeMillis() + timeoutMillis;
        while (true) {
            UiObject2 object2 = (UiObject2) cons.newInstance(constructorParams);
            if (object2 != null) {
                return object2;
            }
            long remainingMillis = end - SystemClock.uptimeMillis();
            if (remainingMillis < 0) {
                return null;
            }
            SystemClock.sleep(Math.min(200, remainingMillis));
        }
    }


    /**
     * Returns the first object to match the {@code selector} criteria.
     */
    public UiObject2 findObject(Object selector) throws Exception {
        AccessibilityNodeInfo node;
        uiDevice.waitForIdle();
        node = ((NodeInfoList) selector).getNodeList().size() > 0 ? ((NodeInfoList) selector).getNodeList().get(0) : null;
        selector = By.clazz(node.getClassName().toString());
        if (node == null) {
            return null;
        }
        return doFindObject(selector, node);
    }

    public List<UiObject2> findObjects(Object selector) throws Exception {
        uiDevice.waitForIdle();
        ArrayList<AccessibilityNodeInfo> accessibilityNodeInfos = ((NodeInfoList) selector).getNodeList();
        int size = accessibilityNodeInfos.size();
        List<UiObject2> list = new ArrayList<UiObject2>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                AccessibilityNodeInfo node = accessibilityNodeInfos.get(i);
                if (node == null) {
                    continue;
                }
                selector = By.clazz(node.getClassName().toString());
                UiObject2 uiObject2 = doFindObject(selector, node);
                list.add(uiObject2);
            }
        } else {
            return null;
        }
        return list;
    }
}
