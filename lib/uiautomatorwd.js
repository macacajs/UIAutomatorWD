'use strict';

const path = require('path');

exports.APK_BUILD_PATH = path.join(__dirname, '..', 'app', 'build', 'outputs', 'apk', 'app-debug-androidTest.apk');
exports.TEST_APK_BUILD_PATH = path.join(__dirname, '..', 'app', 'build', 'outputs', 'apk', 'app-debug.apk');
exports.PACKAGE_NAME = 'com.macaca.android.testing.UIAutomatorWD';
exports.TEST_PACKAGE = 'com.macaca.android.testing';
exports.RUNNER_CLASS = 'android.support.test.runner.AndroidJUnitRunner';
exports.SERVER_URL_REG = /UIAutomatorWD->(.*)<-UIAutomatorWD/;
