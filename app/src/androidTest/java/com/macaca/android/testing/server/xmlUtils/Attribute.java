package com.macaca.android.testing.server.xmlUtils;

/**
 * Created by xdf on 09/05/2017.
 */
public enum Attribute {
    CHECKABLE("checkable"),
    CHECKED("checked"),
    CLASS("class"),
    CLICKABLE("clickable"),
    CONTENT_DESC("content-desc"),
    ENABLED("enabled"),
    FOCUSABLE("focusable"),
    FOCUSED("focused"),
    LONG_CLICKABLE("long-clickable"),
    PACKAGE("package"),
    PASSWORD("password"),
    RESOURCE_ID("resource-id"),
    SCROLLABLE("scrollable"),
    SELECTION_START("selection-start"),
    SELECTION_END("selection-end"),
    SELECTED("selected"),
    TEXT("text"),
    BOUNDS("bounds"),
    INDEX("index");

    private final String name;

    private Attribute(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
