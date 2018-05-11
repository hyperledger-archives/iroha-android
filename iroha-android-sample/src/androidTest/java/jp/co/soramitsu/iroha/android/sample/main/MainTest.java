package jp.co.soramitsu.iroha.android.sample.main;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.co.soramitsu.iroha.android.sample.registration.RegistrationActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainTest {

    @Rule
    public IntentsTestRule<RegistrationActivity> rule = new IntentsTestRule<>(
            RegistrationActivity.class);

    @Before
    public void setUp() {
        // create Account with transactions
    }

    @Test
    public void ui_AllLabelsAreDisplayed() {
        // check username, irh, setDetail hint, to editText, amount editText, send button, qr button

        // click receive tab and check
        // username, irh, setDetail hint, qr image, amount editText


        // click history tab and check
        // username, irh, setDetail hint, given transactions
    }

    @Test
    public void ui_FieldsValidation() {
        // for all editTexts
        // 1) input valid and invalid values
        // 2) check content of editTexts
    }

    @Test
    public void send_EmptyFields() {
        // click send button  and check behavior with
        // 1) both empty fields
        // 2) amount empty field
        // 3) to empty field
    }

    @Test
    public void send_QrDenyAccess() {
        // click qr scan button and deny access
    }

    @Test
    public void send_QrAllowAccess() {
        // click qr scan and allow access - check qr activity is intended
    }

    @Test
    public void send_QrScanQr() {
        // click qr scan and simulate QR scanning.
        // check fields are filled correctly
    }

    @Test
    public void send_Success() {
        // fill to and amount
        // click send
        // check correct behavior
    }

    @Test
    public void send_QrSuccess() {
        // simulate QR scanning
        // click send
        // check correct behavior
    }

    @Test
    public void send_Error() {
        // fill to and amount
        // click send
        // simulate no connection/error
        // check correct behavior
    }

    @Test
    public void history_Error() {
        // simulate no connection
        // click history tab
        // check correct behavior
    }

    @Test
    public void history_AllTransactionsAreDisplayed() {
        // click history tab
        // check transactions are correctly displayed
    }

    @Test
    public void amount_Update() {
        // mock interactor such that on second call it will return different amount
        // check amount
        // swipe to refresh
        // check new amount
    }

    @Test
    public void accountDetails_Error() {
        // input new account details
        // click save
        // error should be thrown by interactor
        // check correct behavior
    }

    @Test
    public void accountDetails_Success() {
        // input new account details
        // click save
        // check correct behavior
    }


    @Test
    public void logout_Success() {
        // click logout
        // check registration screen is displayed
    }
}