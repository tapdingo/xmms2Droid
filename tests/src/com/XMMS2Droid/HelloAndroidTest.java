package com.XMMS2Droid;

import android.test.ActivityInstrumentationTestCase;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.XMMS2Droid.HelloAndroidTest \
 * com.XMMS2Droid.tests/android.test.InstrumentationTestRunner
 */
public class HelloAndroidTest extends ActivityInstrumentationTestCase<HelloAndroid> {

    public HelloAndroidTest() {
        super("com.XMMS2Droid", HelloAndroid.class);
    }

}
