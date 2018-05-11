package jp.co.soramitsu.iroha.android.sample.registration;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.co.soramitsu.iroha.android.sample.MatcherUtils;
import jp.co.soramitsu.iroha.android.sample.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static jp.co.soramitsu.iroha.android.sample.Const.INVALID_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.Const.VALID_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SAVED_USERNAME;
import static jp.co.soramitsu.iroha.android.sample.PreferencesUtil.SHARED_PREFERENCES_FILE;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationTest {

    @Rule
    public IntentsTestRule<RegistrationActivity> rule = new IntentsTestRule<>(
            RegistrationActivity.class, false, false);

    @Test
    public void ui_AllLabelsAreDisplayed() {
        onView(ViewMatchers.withId(R.id.title)).check(matches(isDisplayed()));
        onView(withId(R.id.subtitle)).check(matches(isDisplayed()));
        onView(withId(R.id.username)).check(matches(isDisplayed()));
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));
        onView(withId(R.id.username_input)).check(matches(MatcherUtils.withHint(R.string.username_hint)));
    }

    @Before
    public void setUp() {
        InstrumentationRegistry.getTargetContext()
                .getSharedPreferences(
                        SHARED_PREFERENCES_FILE,
                        Context.MODE_PRIVATE)
                .edit()
                .remove(SAVED_USERNAME)
                .apply();

        rule.launchActivity(null);
    }

    @Test
    public void changeText_invalidUsername() {
        onView(withId(R.id.username)).perform(typeText(INVALID_USERNAME), closeSoftKeyboard());

        onView(withId(R.id.username)).check(matches(withText("ulat")));
    }

    @Test
    public void changeText_validUsername() {
        onView(withId(R.id.username)).perform(typeText(VALID_USERNAME), closeSoftKeyboard());

        onView(withId(R.id.username)).check(matches(withText(VALID_USERNAME)));
    }

    @Test
    public void register_EmptyUsername() {
        onView(withId(R.id.register_button)).perform(click());

        onView(withText(R.string.error_dialog_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText(R.string.username_empty_error_dialog_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void register_GetAccountError() {

    }

    @Test
    public void register_GetAccountExists() {

    }

    @Test
    public void register_CreateAccountError() {

    }

    @Test
    public void register_AddAssetError() {

    }

    @Test
    public void register_Success() {

    }
}