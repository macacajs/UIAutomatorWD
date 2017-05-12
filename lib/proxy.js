'use strict';

const request = require('request');

const _ = require('./helper');
const logger = require('./logger');

class WDProxy {
  constructor(options) {
    Object.assign(this, {
      scheme: 'http',
      proxyHost: '127.0.0.1',
      proxyPort: 8100,
      urlBase: 'wd/hub',
      sessionId: null,
      originSessionId: null
    }, options);
  }


  handleNewUrl(url) {
    const sessionReg = /\/session\/([^\/]+)/;
    url = `${this.scheme}://${this.proxyHost}:${this.proxyPort}${url}`;

    if (sessionReg.test(url) && this.sessionId) {
      this.originSessionId = url.match(sessionReg)[1];
    }
    return url;
  }

  send(url, method, body) {
    return new Promise((resolve, reject) => {
      method = method.toUpperCase();
      const newUrl = this.handleNewUrl(url);
      const retryCount = 10;
      const retryInterval = 2000;

      const reqOpts = {
        url: newUrl,
        method: method,
        headers: {
          'Content-type': 'application/json;charset=utf-8'
        },
        resolveWithFullResponse: true
      };

      if (body && (method.toUpperCase() === 'POST' || method.toUpperCase() === 'PUT')) {
        if (typeof body !== 'object') {
          body = JSON.parse(body);
        }
        reqOpts.json = body;
      }

      logger.debug(`Proxy: ${url}:${method} to ${newUrl}:${method} with body: ${_.trunc(JSON.stringify(body), 200)}`);

      _.retry(() => {
        return new Promise((_resolve, _reject) => {
          request(reqOpts, (error, res, body) => {
            if (error) {
              logger.debug(`UIAutomatorWD client proxy error with: ${error}`);
              return _reject(error);
            }

            if (!body) {
              logger.debug(`UIAutomatorWD client proxy received no data.`);
              return _reject('No data received from wda.');
            }

            if (typeof body !== 'object') {
              try {
                body = JSON.parse(body);
              } catch (e) {
                logger.debug(`Fail to parse body: ${e}`);
              }
            }

            if (body && body.sessionId) {
              this.sessionId = body.sessionId;
              body.sessionId = this.originSessionId;
            }

            logger.debug(`Got response with status ${res.statusCode}: ${_.trunc(JSON.stringify(body), 200)}`);
            _resolve(body);
          });

        });
      }, retryInterval, retryCount).then(resolve, reject);
    });
  }
}

module.exports = WDProxy;
