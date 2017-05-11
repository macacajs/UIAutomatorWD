package com.macaca.android.testing.server.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiObject2;

public class Elements {

	private static Elements global;
	private Hashtable<String, Element> elems;
	private UiDevice mDevice;

	public Elements() {
		elems = new Hashtable<String, Element>();
		mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
	}

	public static Elements getGlobal() {
		if (Elements.global == null) {
			Elements.global = new Elements();
		}
		return Elements.global;
	}

	public Element addElement(UiObject2 element) {
		Element elem = new Element("1", element);
		getElems().put("1", elem);
		return elem;
	}

	public List<Element> addElements(List<UiObject2> elements) {
		List<Element> elems = new ArrayList<Element>();
		for(int i = 0; i < elements.size(); i++) {
			int index = i + 1;
			Element elem = new Element(index + "", elements.get(i));
			getElems().put(index + "", elem);
			elems.add(elem);
		}

		return elems;
	}

	public Element getElement(String key) {
		return getElems().get(key);
	}

	public Element getElement(BySelector sel) throws Exception {
		UiObject2 el = mDevice.findObject(sel);
		Element result = addElement(el);
		if (el != null) {
			return result;
		} else {
			throw new Exception("not found");
		}
	}

	public List<Element> getMultiElement(BySelector sel) throws Exception {
		List<UiObject2> el = mDevice.findObjects(sel);
		List<Element> result = addElements(el);
		if (result != null) {
			return result;
		} else {
			throw new Exception("not found");
		}
	}

	public Hashtable<String, Element> getElems() {
		return elems;
	}

	public UiDevice getmDevice() {
		return this.mDevice;
	}
}