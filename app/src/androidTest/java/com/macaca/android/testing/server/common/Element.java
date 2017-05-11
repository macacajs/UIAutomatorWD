package com.macaca.android.testing.server.common;

import android.graphics.Point;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;

public class Element {

	public UiObject2 element;

	public String id;


	Element(String id, UiObject2 element) {
		this.element = element;
		this.id = id;
	}


	public void click() {
		 element.click();
	}


	public String getId() {
		return id;
	}


	public String getText() throws UiObjectNotFoundException {
		return element.getText();
	}

	public void clearText() throws UiObjectNotFoundException {
		element.clear();
	}

	public void setText(String text) throws UiObjectNotFoundException {
		Configurator config = Configurator.getInstance();
		config.setKeyInjectionDelay(20);
		element.setText(text);
		config.setKeyInjectionDelay(0);
	}

	public boolean tap() throws UiObjectNotFoundException {
		element.click();
		return true;
	}

	public boolean doubleTap() throws UiObjectNotFoundException, Exception {
		element.click();
		Thread.sleep(100);
		element.click();
		return true;
	}

	public boolean isDisplayed() throws UiObjectNotFoundException {
		return true;
	}

	public UiObject2 getUiObject() {
		return this.element;
	}

	public boolean pinch(String direction, float percent, int steps) throws UiObjectNotFoundException {
		if (direction.equals("in")) {
			element.pinchOpen(percent, steps);
		} else if (direction.equals("out")) {
			element.pinchClose(percent, steps);
		}
		return true;
	}

	public boolean drag(int x, int y, int steps) throws UiObjectNotFoundException {
		Point point = new Point(x, y);
		element.drag(point, steps);
		return true;
	}
}