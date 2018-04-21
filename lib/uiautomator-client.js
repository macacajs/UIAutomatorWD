'use strict';

const _ = require('./helper');
const logger = require('./logger');
const WDProxy = require('./proxy');
const UIAUTOMATORWD = require('./uiautomatorwd');

const detectPort = _.detectPort;

function UIAutomator(options) {
  this.adb = null;
  this.proxy = null;
  Object.assign(this, {
    proxyHost: '127.0.0.1',
    proxyPort: process.env.UIAUTOMATOR_PORT || 9001,
    urlBase: 'wd/hub'
  }, options || {});
}

UIAutomator.prototype.init = function *(adb, permissionPatterns) {
  this.adb = adb;
  this.permissionPatterns = permissionPatterns;
  this.proxyPort = yield detectPort(this.proxyPort);
  this.initProxy();
  yield this.adb.forward(this.proxyPort, this.proxyPort);
  const ANDROID_TMP_DIR = this.adb.getTmpDir();
  
  //处理 com.macaca.android.testing,已经安装的包不再重新安装，
  try {
    yield this.adb.shell(`pm list packages | grep ${UIAUTOMATORWD.TEST_PACKAGE}$`); //如果包没安装，会走异常流程
    logger.debug(`${UIAUTOMATORWD.TEST_PACKAGE}，包已存在，无需重新安装，执行清理缓存操作`)

    yield this.adb.shell(`pm clear "${UIAUTOMATORWD.TEST_PACKAGE}"`);
  } catch (e) {
    //上传包，安装包
    logger.debug(`${UIAUTOMATORWD.TEST_PACKAGE}，包不存在，执行安装操作`)
    if (_.isExistedFile(UIAUTOMATORWD.TEST_APK_BUILD_PATH)) {
      yield this.adb.push(UIAUTOMATORWD.TEST_APK_BUILD_PATH, `${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}`);
    } else {
      logger.error(`${UIAUTOMATORWD.TEST_APK_BUILD_PATH} not found, please resolve and reinstall android driver`);
    }

    yield this.adb.shell(`pm install -r "${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}"`);
  }


  //处理 com.macaca.android.testing.test,已经安装的包不再重新安装，
  try {
    yield this.adb.shell(`pm list packages | grep ${UIAUTOMATORWD.TEST_PACKAGE}.test$`);//如果包没安装，会走异常流程
    logger.debug(`${UIAUTOMATORWD.TEST_PACKAGE}.test，包已存在，无需重新安装，执行清理缓存操作`)

    yield this.adb.shell(`pm clear "${UIAUTOMATORWD.TEST_PACKAGE}.test"`);
  } catch (e) {
    //上传包，安装包
    logger.debug(`${UIAUTOMATORWD.TEST_PACKAGE}.test，包不存在，执行安装操作`)
    if (_.isExistedFile(UIAUTOMATORWD.APK_BUILD_PATH)) {
      yield this.adb.push(UIAUTOMATORWD.APK_BUILD_PATH, `${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}.test`);
    } else {
      logger.error(`${UIAUTOMATORWD.APK_BUILD_PATH} not found, please resolve and reinstall android driver`);
    }

    yield this.adb.shell(`pm install -r "${ANDROID_TMP_DIR}/${UIAUTOMATORWD.TEST_PACKAGE}.test"`);
  }
  
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
    let permissionPattern = this.permissionPattern ? `-e permissionPattern ${this.permissionPatterns}` : '';
    let args = `shell am instrument -w -r ${permissionPattern} -e port ${this.proxyPort} -e class ${UIAUTOMATORWD.PACKAGE_NAME} ${UIAUTOMATORWD.TEST_PACKAGE}.test/${UIAUTOMATORWD.RUNNER_CLASS}`.split(' ');

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

UIAutomator.prototype.sendCommand = function *(url, method, body) {
  let isServerStillAlive = true;

  try {
    let pids = yield this.adb.getPIds(UIAUTOMATORWD.TEST_PACKAGE);
    // If the pids array is empty, that means the WD server is killed
    isServerStillAlive = pids.length > 0;
  } catch (e) {
    // Unable to get the pid info, assume the server is still alive
    logger.warn(`get pids of ${UIAUTOMATORWD.TEST_PACKAGE} failed, ignore this log`);
  }

  if (!isServerStillAlive) {
    logger.info('restart UIAutomatorWD server');
    // restart the WD server
    yield this.startServer();
  }
  return this.proxy.send(url, method, body);
};

module.exports = UIAutomator;
module.exports.UIAUTOMATORWD = UIAUTOMATORWD;
