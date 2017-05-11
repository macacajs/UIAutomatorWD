'use strict';

var ADB = require('macaca-adb');

var UIAutomator = require('..');

describe('android runtime socket protocol', function() {

  var client = new UIAutomator();

  it('init uiautomator', function *() {
    client.should.be.ok();
    console.log(ADB);
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
