package com.functionality;

import android.content.Context;
import android.os.AsyncTask;

public class BackgroundTask extends AsyncTask<Integer, Void, Integer> {

    private Context mContext;
    private OnTaskCompleted mListener;
    //private static final int MAX_TICK = 5;

    BackgroundTask(Context applicationContext, OnTaskCompleted listener) {
        mContext = applicationContext;
        mListener = listener;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        int old_nb_step = integers[0];
        int nb_step = integers[1];
        int currentTick = integers[2];
            if (old_nb_step < nb_step) {
                currentTick = 0;
            }
            else {
                currentTick++;
            }
        return currentTick;
    }

    @Override
    protected void onPostExecute(Integer result) {
        mListener.onTaskCompleted(result);
        super.onPostExecute(result);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(Integer result);
    }

}
