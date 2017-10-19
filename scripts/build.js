#!/usr/bin/env node

'use strict';

const path = require('path');
const gradle = require('gradle');

const cwd = path.join(__dirname, '..');

var args = [
  'assembleDebug',
  'assembleDebugAndroidTest'
];

args.push(`-PmavenMirrorUrl=${process.env.MAVEN_MIRROR_URL || ''}`);

gradle({
  cwd: cwd,
  args: args
});
