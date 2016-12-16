package io.soramitsu.irohaandroid.async;

import android.os.AsyncTask;
import android.util.Log;

import io.soramitsu.irohaandroid.callback.Callback;

public abstract class IrohaAsyncTask<T> extends AsyncTask<Void, Void, Void> {
    public static final String TAG = IrohaAsyncTask.class.getSimpleName();

    private final Callback<T> callback;

    private T result;
    private Exception exception;

    protected IrohaAsyncTask(Callback<T> callback) {
        this.callback = callback;
    }

    protected abstract T onBackground() throws Exception;

    @Override
    protected Void doInBackground(Void... ts) {
        try {
            result = onBackground();
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void res) {
        if (exception != null) {
            Log.e(TAG, "Iroha:  throw exception!", exception);
            callback.onFailure(exception);
            return;
        }

        callback.onSuccessful(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "onCancelled: ");
    }
}
