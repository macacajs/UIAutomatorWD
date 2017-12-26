package com.macaca.android.testing;

import com.macaca.android.testing.server.controllers.*;
import com.macaca.android.testing.server.models.Methods;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Created by xdf on 02/05/2017.
 */

public class UIAutomatorWDServer extends RouterNanoHTTPD {

    private static volatile UIAutomatorWDServer singleton;

    private String sessionRoutePrefix = "/wd/hub/session/:sessionId";

    private UIAutomatorWDServer(int port) throws IOException {
        super(port);

        //Home
        //addRoute(sessionRoutePrefix + "/", Methods.GET, HomeController.home);

        //SessionRouter
        //addRoute("/wd/hub/session", Methods.POST, SessionController.createSession);
        //addRoute("/wd/hub/sessions", Methods.GET, SessionController.getSessions);
        //addRoute("/wd/hub/session/:sessionId", Methods.DELETE, SessionController.delSession);

        //Window Router
        addRoute(sessionRoutePrefix + "/window_handle", Methods.GET, WindowController.getWindow);
        addRoute(sessionRoutePrefix + "/window_handles", Methods.GET, WindowController.getWindows);
        addRoute(sessionRoutePrefix + "/window", Methods.POST, WindowController.setWindow);
        addRoute(sessionRoutePrefix + "/window", Methods.DELETE, WindowController.deleteWindow);
        addRoute(sessionRoutePrefix + "/window/:windowHandle/size", Methods.POST, WindowController.setWindowSize);
        addRoute(sessionRoutePrefix + "/window/:windowHandle/size", Methods.GET, WindowController.getWindowSize);
        addRoute(sessionRoutePrefix + "/window/:windowHandle/maximize", Methods.POST, WindowController.maximize);
        addRoute(sessionRoutePrefix + "/frame", Methods.POST, WindowController.setFrame);

        //ContextRouter
        addRoute(sessionRoutePrefix + "/context", Methods.GET, ContextController.getContext);
        addRoute(sessionRoutePrefix + "/context", Methods.POST, ContextController.setContext);
        addRoute(sessionRoutePrefix + "/contexts", Methods.GET, ContextController.getContexts);

        //AlertRouter
        addRoute(sessionRoutePrefix + "/accept_alert", Methods.POST, AlertController.acceptAlert);
        addRoute(sessionRoutePrefix + "/dismiss_alert", Methods.POST, AlertController.dismissAlert);
        addRoute(sessionRoutePrefix + "/alert_text", Methods.GET, AlertController.alertText);
        addRoute(sessionRoutePrefix + "/alert_text", Methods.POST, AlertController.alertKeys);

        //ElementRouter
        addRoute(sessionRoutePrefix + "/click", Methods.POST, ElementController.click);
        addRoute(sessionRoutePrefix + "/element", Methods.POST, ElementController.findElement);
        addRoute(sessionRoutePrefix + "/elements", Methods.POST, ElementController.findElements);
        addRoute(sessionRoutePrefix + "/element/:elementId/element", Methods.POST, ElementController.findElement);
        addRoute(sessionRoutePrefix + "/element/:elementId/elements", Methods.POST, ElementController.findElements);
        addRoute(sessionRoutePrefix + "/element/:elementId/value", Methods.POST, ElementController.setValue);
        addRoute(sessionRoutePrefix + "/element/:elementId/click", Methods.POST, ElementController.click);
        addRoute(sessionRoutePrefix + "/element/:elementId/text", Methods.GET, ElementController.getText);
        addRoute(sessionRoutePrefix + "/element/:elementId/clear", Methods.POST, ElementController.clearText);
        addRoute(sessionRoutePrefix + "/element/:elementId/displayed", Methods.GET, ElementController.isDisplayed);
        addRoute(sessionRoutePrefix + "/element/:elementId/attribute/:name", Methods.GET, ElementController.getAttribute);
        addRoute(sessionRoutePrefix + "/element/:elementId/property/:name", Methods.GET, ElementController.getAttribute);
        addRoute(sessionRoutePrefix + "/element/:elementId/css/:propertyName", Methods.GET, ElementController.getComputedCss);
        addRoute(sessionRoutePrefix + "/element/:elementId/rect", Methods.GET, ElementController.getRect);

        //ScreenshotRouter
        addRoute(sessionRoutePrefix + "/screenshot", Methods.GET, ScreenshotController.getScreenshot);

        //SourceRouter
        addRoute(sessionRoutePrefix + "/source", Methods.GET, SourceController.source);

        //KeysRouter
        addRoute(sessionRoutePrefix + "/keys", Methods.POST, KeysController.keys);

        //TimeoutsRouter
        addRoute(sessionRoutePrefix + "/timeouts/implicit_wait", Methods.POST, TimeoutsController.implicitWait);

        //UrlRouter
        addRoute(sessionRoutePrefix + "/url", Methods.POST, UrlController.getUrl);
        addRoute(sessionRoutePrefix + "/url", Methods.GET, UrlController.url);
        addRoute(sessionRoutePrefix + "/forward", Methods.POST, UrlController.forward);
        addRoute(sessionRoutePrefix + "/back", Methods.POST, UrlController.back);
        addRoute(sessionRoutePrefix + "/refresh", Methods.POST, UrlController.refresh);

        //ActionRouter
        addRoute(sessionRoutePrefix + "/actions", Methods.POST, ActionController.actions);

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8080/ \n");
    }

    public static UIAutomatorWDServer getInstance(int port) {
        if (singleton == null) {
            synchronized (UIAutomatorWDServer.class) {
                if (singleton == null) {
                    try {
                        singleton = new UIAutomatorWDServer(port);
                    } catch (IOException ioe) {
                        System.err.println("Couldn't start server:\n" + ioe);
                    }
                }
            }
        }
        return singleton;
    }
}
