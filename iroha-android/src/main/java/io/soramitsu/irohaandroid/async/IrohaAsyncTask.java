package io.soramitsu.irohaandroid.async;

import android.os.AsyncTask;

import io.soramitsu.irohaandroid.callback.Callback;

public abstract class IrohaAsyncTask<T> extends AsyncTask<Void, Void, Void> {

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
            callback.onFailure(exception);
            return;
        }

        callback.onSuccessful(result);
    }
}
