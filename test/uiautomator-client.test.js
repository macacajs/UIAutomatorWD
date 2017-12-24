'use strict';

const _ = require('xutil');
const assert = require('assert');

const UIAutomator = require('..');

describe('uiautomator', function() {

  it('should be ok', function() {

    assert(_.isExistedFile(UIAutomator.UIAUTOMATORWD.APK_BUILD_PATH), true);
    /*
    var adb = new ADB();
    var devices = yield ADB.getDevices();

    if (!devices.length) {
      done();
    }

    var device = devices[0];
    adb.setDeviceId(device.udid);
    yield client.init(adb);
    */
  });
});
