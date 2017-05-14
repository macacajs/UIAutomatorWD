'use strict';

require('should');
const _ = require('xutil');
//const ADB = require('macaca-adb');

const UIAutomator = require('..');

describe('uiautomator', function() {

  var client = new UIAutomator();

  it('should be ok', function() {
    client.should.be.ok();

    if (!_.isExistedFile(UIAutomator.UIAUTOMATORWD.APK_BUILD_PATH)) {
      throw 'apk file build failed';
    }
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
