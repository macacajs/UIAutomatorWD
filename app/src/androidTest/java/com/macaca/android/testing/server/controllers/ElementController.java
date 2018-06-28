package com.macaca.android.testing.server.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.macaca.android.testing.server.models.Response;
import com.macaca.android.testing.server.models.Status;
import com.macaca.android.testing.server.common.Element;
import com.macaca.android.testing.server.common.Elements;
import com.macaca.android.testing.server.xmlUtils.InteractionController;
import com.macaca.android.testing.server.xmlUtils.NodeInfoList;
import com.macaca.android.testing.server.xmlUtils.ReflectionUtils;
import com.macaca.android.testing.server.xmlUtils.UiAutomatorBridge;
import com.macaca.android.testing.server.xmlUtils.XPathSelector;
import com.macaca.android.testing.server.xmlUtils.MUiDevice;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.InstrumentationUiAutomatorBridge;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by xdf on 02/05/2017.
 */

public class ElementController extends RouterNanoHTTPD.DefaultHandler {

    public static ElementController click;
    public static ElementController findElement;
    public static ElementController findElements;
    public static ElementController setValue;
    public static ElementController getText;
    public static ElementController clearText;
    public static ElementController isDisplayed;
    public static ElementController getAttribute;
    public static ElementController getComputedCss;
    public static ElementController getRect;

    private static Elements elements = Elements.getGlobal();

    static {
        click = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = urlParams.get("elementId");
                JSONObject result = null;
                try {
                    if (elementId != null) {
                        Element el = getElements().getElement(elementId);
                        el.click();
                    } else {
                        Element el = getElements().getElement("1");
                        el.click();
                    }
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                }

            }
        };

        findElement = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                Map<String, String> body = new HashMap<String, String>();
                JSONObject result = new JSONObject();
                try {
                    session.parseBody(body);
                    String value = body.get("postData");
                    JSONObject postData = JSON.parseObject(value);
                    String strategy = (String) postData.get("using");
                    String text = (String) postData.get("value");
                    strategy = strategy.trim().replace(" ", "_").toUpperCase();
                    if (strategy.equals("XPATH")) {
                        try {
                            UiObject2 uiObject2 = getXPathUiObject(text);
                            Element element = getElements().addElement(uiObject2);
                            result.put("ELEMENT", element.getId());
                        } catch (Exception e) {
                            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                        }
                    } else {
                        try {
                            BySelector selector = getSelector(strategy, text);
                            result = getOneElement(selector);
                        } catch (Exception e) {
                            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.InvalidSelector, sessionId).toString());
                        }
                    }
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());

                } catch (Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                }
            }
        };

        findElements = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                Map<String, String> body = new HashMap<String, String>();
                JSONArray result = null;
                try {
                    session.parseBody(body);
                    String value = body.get("postData");
                    JSONObject postData = JSON.parseObject(value);
                    String strategy = (String) postData.get("using");
                    String text = (String) postData.get("value");
                    strategy = strategy.trim().replace(" ", "_").toUpperCase();
                    if (strategy.equals("XPATH")) {
                        List<UiObject2> uiObject2s = getXPathUiObjects(text);
                        List<Element> elements = getElements().addElements(uiObject2s);
                        result = elementsToJSONArray(elements);
                    } else {
                        try {
                            BySelector selector = getSelector(strategy, text);
                            result = getMultiElements(selector);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.InvalidSelector, sessionId).toString());
                        }
                    }
                } catch (Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(new JSONArray(), sessionId).toString());
                }
                return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
            }
        };

        setValue = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = urlParams.get("elementId");
                Map<String, String> body = new HashMap<>();
                JSONObject result = null;
                try {

                    /**
                     * Code in UIObject2 line 601, currentText cause npe:
                     * if (!node.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args) &&
                     * currentText.length() > 0) {
                     * */
                    Element element = getElements().getElement(elementId);
                    AccessibilityNodeInfo info = (AccessibilityNodeInfo)ReflectionUtils.getField(UiObject2.class,"mCachedNode",element.element);
                    if (info != null && info.getText() == null) {
                        ReflectionUtils.setField(AccessibilityNodeInfo.class,"mText", info, "");
                    }

                    // Conduct regular text set
                    session.parseBody(body);
                    String postData = body.get("postData");
                    JSONObject jsonObj = JSON.parseObject(postData);
                    JSONArray values = (JSONArray) jsonObj.get("value");
                    for (Iterator iterator = values.iterator(); iterator.hasNext(); ) {
                        String value = (String) iterator.next();
                        element.setText(value);
                    }
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                } catch (final UiObjectNotFoundException e) {
                    e.printStackTrace();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                } catch (final Exception e) {
                    e.printStackTrace();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        getText = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                try {
                    String elementId = urlParams.get("elementId");
                    Element element = Elements.getGlobal().getElement(elementId);
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(element.getText(), sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        clearText = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = (String) urlParams.get("elementId");
                Element el = Elements.getGlobal().getElement(elementId);
                JSONObject result = null;
                try {
                    el.clearText();
                    if (el.getText() == null) {
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                    }
                    if (el.getText().isEmpty()) {
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                    }
                    if (hasHintText(el)) {
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                    }
                    if (sendDeleteKeys(el)) {
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                    }
                    if (!el.getText().isEmpty()) {
                        if (hasHintText(el)) {
                            System.out.println("The text should be the hint text");
                        } else if (!el.getText().isEmpty()) {
                            System.out.println("oh my god. Can't clear the Text");
                        }
                        return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                    }
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(result, sessionId).toString());
                } catch (final UiObjectNotFoundException e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        isDisplayed = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = urlParams.get("elementId");
                try {
                    Element el = Elements.getGlobal().getElement(elementId);
                    boolean isDisplayed = el.isDisplayed();
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(isDisplayed, sessionId).toString());
                } catch (final UiObjectNotFoundException e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.NoSuchElement, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        getAttribute = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = urlParams.get("elementId");
                try {
                    Element el = Elements.getGlobal().getElement(elementId);
                    JSONObject props = new JSONObject();
                    props.put("text", el.element.getText());
                    props.put("description", el.element.getContentDescription());
                    props.put("enabled", el.element.isEnabled());
                    props.put("checkable", el.element.isCheckable());
                    props.put("checked", el.element.isChecked());
                    props.put("clickable", el.element.isClickable());
                    props.put("focusable", el.element.isFocusable());
                    props.put("focused", el.element.isFocused());
                    props.put("longClickable", el.element.isLongClickable());
                    props.put("scrollable", el.element.isScrollable());
                    props.put("selected", el.element.isSelected());
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(props, sessionId).toString());
                } catch (final Exception e) {
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(Status.UnknownError, sessionId).toString());
                }
            }
        };

        getRect = new ElementController() {
            @Override
            public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
                String sessionId = urlParams.get("sessionId");
                String elementId = urlParams.get("elementId");
                try {
                    Element el = Elements.getGlobal().getElement(elementId);
                    final Rect rect = el.element.getVisibleBounds();
                    JSONObject res = new JSONObject();
                    res.put("x", rect.left);
                    res.put("y", rect.top);
                    res.put("height", rect.height());
                    res.put("width", rect.width());
                    return NanoHTTPD.newFixedLengthResponse(getStatus(), getMimeType(), new Response(res, sessionId).toString());
                } catch (final Exception e) {
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

    private static JSONObject getOneElement(final BySelector sel) throws Exception {
        final JSONObject res = new JSONObject();
        final Element element = getElements().getElement(sel);
        res.put("ELEMENT", element.getId());
        return res;
    }

    private static JSONArray getMultiElements(final BySelector sel) throws Exception {
        JSONArray res;
        List<Element> foundElements = new ArrayList<Element>();
        final List<Element> elementsFromSelector = getElements().getMultiElement(sel);
        foundElements.addAll(elementsFromSelector);
        res = elementsToJSONArray(foundElements);
        return res;
    }

    private static UiObject2 getXPathUiObject(String expression) throws Exception {
        final NodeInfoList nodeList = XPathSelector.getNodesList(expression);
        if (nodeList.size() == 0) {
            throw new Exception(Status.XPathLookupError.getStatusDes());
        }
        return MUiDevice.getInstance().findObject(nodeList);
    }

    private static List<UiObject2> getXPathUiObjects(String expression) throws Exception {
        final NodeInfoList nodeList = XPathSelector.getNodesList(expression);
        if (nodeList.size() == 0) {
            throw new Exception(Status.XPathLookupError.getStatusDes());
        }
        return MUiDevice.getInstance().findObjects(nodeList);
    }

    private static BySelector getSelector(String strategy, String text) throws Exception {
        BySelector selector = null;
        switch (strategy) {
            case "CLASS_NAME":
                selector = By.clazz(text);
                break;
            case "NAME":
                selector = By.desc(text);
                if(selector == null || elements.getmDevice().findObject(selector) == null){
                    selector = By.text(text);
                }
                break;
            case "ID":
                selector = By.res(text);
                break;
            case "TEXT_CONTAINS":
                selector = By.textContains(text);
                break;
            case "DESC_CONTAINS":
                selector = By.descContains(text);
                break;
        }
        return selector;
    }

    private static JSONArray elementsToJSONArray(final List<Element> elems)
            throws JSONException {
        JSONArray resArray = new JSONArray();
        for (Element element : elems) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ELEMENT", element.getId());
            resArray.add(jsonObject);
        }
        return resArray;
    }

    private static boolean hasHintText(Element el)
            throws UiObjectNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {

        String currText = el.getText();
        try {
            if (!el.getUiObject().isFocused()) {
                System.out.println("Could not check for hint text because the element is not focused!");
                return false;
            }
        } catch (final Exception e) {
            System.out.println("Could not check for hint text: " + e.getMessage());
            return false;
        }

        try {
            InteractionController interactionController = UiAutomatorBridge.getInstance().getInteractionController();
            interactionController.sendKey(KeyEvent.KEYCODE_DEL, 0);
            interactionController.sendKey(KeyEvent.KEYCODE_FORWARD_DEL, 0);
        } catch (Exception e) {
            System.out.println("UiAutomatorBridge.getInteractionController error happen!");
        }

        return currText.equals(el.getText());
    }

    private static boolean sendDeleteKeys(Element el)
            throws UiObjectNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        String tempTextHolder = "";

        while (!el.getText().isEmpty() && !tempTextHolder.equalsIgnoreCase(el.getText())) {
            el.click();

            for (int key : new int[]{KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_FORWARD_DEL}) {
                tempTextHolder = el.getText();
                final int length = tempTextHolder.length();
                for (int count = 0; count < length; count++) {
                    try {
                        InteractionController interactionController = UiAutomatorBridge.getInstance().getInteractionController();
                        interactionController.sendKey(key, 0);
                    } catch (Exception e) {
                        System.out.println("UiAutomatorBridge.getInteractionController error happen!");
                    }
                }
            }
        }
        return el.getText().isEmpty();
    }
    public static Elements getElements() {
        return elements;
    }
}

