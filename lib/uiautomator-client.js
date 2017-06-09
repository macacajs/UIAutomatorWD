'use strict';

const detectPort = require('detect-port');

const logger = require('./logger');
const WDProxy = require('./proxy');
const UIAUTOMATORWD = require('./uiautomatorwd');

function UIAutomator(options) {
  this.adb = null;
  this.proxy = null;
  Object.assign(this, {
    proxyHost: '127.0.0.1',
    proxyPort: process.env.UIAUTOMATOR_PORT || 9001,
    urlBase: 'wd/hub'
  }, options || {});
}

UIAutomator.prototype.init = function *(adb) {
  this.adb = adb;
  this.initProxy();
  this.proxyPort = yield detectPort(this.proxyPort);
  yield this.adb.forward(this.proxyPort, this.proxyPort);
  const ANDROID_TMP_DIR = this.adb.getTmpDir();
  yield this.adb.shell(`pm uninstall "${UIAUTOMATORWD.TEST_PACKAGE}"`);
  yield this.adb.shell(`pm uninstall "${UIAUTOMATORWD.TEST_PACKAGE}.test"`);
  yield this.adb.push(UIAUTOMATORWD.TEST_APK_BUILD_PATH, `${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}`);
  yield this.adb.shell(`pm install -r "${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}"`);
  yield this.adb.push(UIAUTOMATORWD.APK_BUILD_PATH, `${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}.test`);
  yield this.adb.shell(`pm install -r "${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}.test"`);
  yield this.adb.shell(`am force-stop ${UIAUTOMATORWD.TEST_PACKAGE}`);
  yield this.adb.shell(`am force-stop ${UIAUTOMATORWD.TEST_PACKAGE}.test`);
  yield this.startServer();
};

UIAutomator.prototype.initProxy = function() {
  this.proxy = new WDProxy({
    proxyHost: this.proxyHost,
    proxyPort: this.proxyPort,
    urlBase: this.urlBase
  });
};

UIAutomator.prototype.startServer = function() {
  return new Promise(resolve => {
    let args = `shell am instrument -w -r -e port ${this.proxyPort} -e class ${UIAUTOMATORWD.PACKAGE_NAME} ${UIAUTOMATORWD.TEST_PACKAGE}.test/${UIAUTOMATORWD.RUNNER_CLASS}`.split(' ');

    var proc = this.adb.spawn(args, {
      path: process.cwd(),
      env: process.env
    });

    proc.stderr.setEncoding('utf8');
    proc.stdout.setEncoding('utf8');
    proc.stdout.on('data', data => {
      logger.debug(data);
      let match = UIAUTOMATORWD.SERVER_URL_REG.exec(data);

      if (match) {
        const url = match[1];

        if (url.startsWith('http://')) {
          logger.info('UIAutomatorWD http server ready');
          resolve();
        }
      }
    });
    proc.stderr.on('data', data => {
      logger.info(data);
    });
  });
};

UIAutomator.prototype.sendCommand = function(url, method, body) {
  return this.proxy.send(url, method, body);
};

module.exports = UIAutomator;

module.exports.UIAUTOMATORWD = UIAUTOMATORWD;
