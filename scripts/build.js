#!/usr/bin/env node

'use strict';

const path = require('path');
const gradle = require('gradle');

const cwd = path.join(__dirname, '..');

gradle({
  cwd: cwd,
  args: ['assembleDebug', 'assembleDebugAndroidTest']
});
