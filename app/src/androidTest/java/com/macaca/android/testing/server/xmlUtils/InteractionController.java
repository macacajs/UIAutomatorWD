/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macaca.android.testing.server.xmlUtils;

import static com.macaca.android.testing.server.xmlUtils.ReflectionUtils.invoke;
import static com.macaca.android.testing.server.xmlUtils.ReflectionUtils.method;

public class InteractionController {

    private static final String CLASS_INTERACTION_CONTROLLER = "com.android.uiautomator.core.InteractionController";
    private static final String METHOD_SEND_KEY = "sendKey";

    private final Object interactionController;

    public InteractionController(Object interactionController) {
        this.interactionController = interactionController;
    }

    public boolean sendKey(int keyCode, int metaState) throws Exception {
        return (Boolean) invoke(method(CLASS_INTERACTION_CONTROLLER, METHOD_SEND_KEY, int.class, int.class), interactionController, keyCode, metaState);
    }

}
