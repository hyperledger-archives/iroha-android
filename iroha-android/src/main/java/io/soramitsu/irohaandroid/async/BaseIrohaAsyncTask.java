package io.soramitsu.irohaandroid.async;

import android.os.AsyncTask;
import android.util.Log;

public abstract class BaseIrohaAsyncTask<T> extends AsyncTask<Void, Void, Void> {
    public static final String TAG = BaseIrohaAsyncTask.class.getSimpleName();

    protected T result;
    protected Exception exception;

    protected abstract T onBackground() throws Exception;
    protected abstract void onMainThread();

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            result = onBackground();
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onMainThread();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "onCancelled: ");
    }
}
