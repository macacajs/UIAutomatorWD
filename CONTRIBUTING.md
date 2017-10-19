# Contributing to UIAutomatorWD

We love pull requests from everyone.

## Link Global To Local

``` bash
$ cd path/to/macaca-android
$ npm link path/to/UIAutomatorWD
# now project UIAutomatorWD is linked to macaca-android
```

## Run with Android Studio

## Restful Sample

``` bash
$ adb forward tcp:9001 tcp:9001
$ curl -l -H "Content-type: application/json" -X POST -d '{"value": "//*[@resource-id=\"android:id/tabs\"]/android.widget.LinearLayout[2]/android.widget.ImageView[1]","using":"xpath"}' http://localhost:9001/wd/hub/session/xxxxxxxx/element/1/click
```
