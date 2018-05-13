package jp.co.soramitsu.iroha.android.sample;

import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


public class MatcherUtils {

    private MatcherUtils() {
        throw new RuntimeException("This utility class shouldn't be instantiated");
    }

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return isHintMatches(view, expectedHint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    public static Matcher<View> withHint(final @StringRes int expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                return isHintMatches(view, view.getResources().getString(expectedHint));
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    private static boolean isHintMatches(View view, final String expectedHint) {
        String hint;
        if (view instanceof EditText) {
            hint = ((EditText) view).getHint().toString();
        } else if (view instanceof TextInputLayout) {
            hint = ((TextInputLayout) view).getHint().toString();
        } else {
            return false;
        }

        return expectedHint.equals(hint);
    }
}
