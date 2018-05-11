package jp.co.soramitsu.iroha.android.sample.main;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SAVED_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SHARED_PREFERENCES_FILE;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainIntegrationTest {

    @BeforeClass
    public static void before() {
        InstrumentationRegistry.getTargetContext()
                .getSharedPreferences(
                        SHARED_PREFERENCES_FILE,
                        Context.MODE_PRIVATE)
                .edit()
                .remove(SAVED_USERNAME);

        // sign up
    }

    @Test
    public void send_Success() {
        // fill fields correctly (admin user always exists. you can send to him)
        // click send
        // check correct behavior + amount is decreased appropriately
        // click transactions tab
        // check correct behavior
    }

    @Test
    public void accountDetails_Success() {
        // fill account details
        // click save
        // check correct behavior
    }
}