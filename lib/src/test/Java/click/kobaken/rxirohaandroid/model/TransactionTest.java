package click.kobaken.rxirohaandroid.model;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionTest {

    private Transaction transaction;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        transaction = createStub("value", "sender", Calendar.getInstance().getTimeInMillis() / 1000);
    }

    @Test
    public void modifyDisplayDate_show_now() throws Exception {
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("now"));
    }

    @Test
    public void modifyDisplayDate_show_sec() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 10;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("10sec"));
    }

    @Test
    public void modifyDisplayDate_show_min() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 60;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("1min"));
    }

    @Test
    public void modifyDisplayDate_show_hour() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 3600;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("1hour"));
    }

    @Test
    public void modifyDisplayDate_show_day() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 3600 * 24;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("1day"));
    }

    @Test
    public void modifyDisplayDate_show_days() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 3600 * 24 * 2;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("2days"));
    }

    @Test
    public void modifyDisplayDate_show_before_month_ago() throws Exception {
        final long monthAgo = Calendar.getInstance().getTimeInMillis() / 1000 - 3600 * 24 * 31;

        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 - 3600 * 24 * 31;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is(new SimpleDateFormat("yyyy/MM/dd", Locale.JAPAN).format(new Timestamp(monthAgo * 1000))));
    }

    @Test
    public void modifyDisplayDate_show_future() throws Exception {
        transaction.params.timestamp = Calendar.getInstance().getTimeInMillis() / 1000 + 3600 * 24 * 31;
        String result = transaction.modifyDisplayDate();
        assertThat(result, is("future"));
    }

    @Test
    public void modifyDisplayDate_when_OperationParameter_is_null() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("OperationParameter should not be null");

        transaction.params = null;
        transaction.modifyDisplayDate();
    }

    @Test
    public void isSender_when_sender_is_mine() throws Exception {
        boolean result = transaction.isSender("sender");
        assertTrue(result);
    }

    @Test
    public void isSender_when_sender_is_other() throws Exception {
        final String otherPublicKey = "otherPubKey";
        boolean result = transaction.isSender(otherPublicKey);
        assertFalse(result);
    }

    @Test
    public void isSender_when_OperationParameter_is_null() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("OperationParameter should not be null");

        transaction.params = null;
        transaction.isSender("sender");
    }

    @Test
    public void isSender_when_sender_is_null() throws Exception {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("OperationParameter should not be null");

        transaction.params.sender = null;
        transaction.isSender("sender");
    }

    @Test
    public void isSender_when_args_is_null() throws Exception {
        boolean result = transaction.isSender(null);
        assertFalse(result);
    }

    private Transaction createStub(String value, String sender, long timestamp) {
        return new Transaction() {{
            this.params = new OperationParameter();
            this.params.value = value;
            this.params.sender = sender;
            this.params.timestamp = timestamp;
        }};
    }
}
