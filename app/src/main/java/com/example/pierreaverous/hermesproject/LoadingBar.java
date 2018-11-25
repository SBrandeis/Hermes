package com.example.pierreaverous.hermesproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * A loading bar, to show progress when looking up firebase information.
 * Deprecated class ProgressDialog is here being used because it forces
 * the user to wait for the progress to be finished to continue interacting
 * with app.
 * It can be a problem in some cases, bun in our case it is exactly what
 * we need.
 */
public class LoadingBar extends ProgressDialog {

    int max, progress;
    DownloadDialogFragment dialogFragment;
    Context mContext;

    public LoadingBar(Context context) {
        super(context);
        mContext = context;
        this.setMessage("Checking file info, please wait...");
        this.setTitle("Checking stuff...");
        this.setProgressStyle(STYLE_HORIZONTAL);
        this.max = 10;
        this.progress = 0;
    }

    public LoadingBar(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(mContext.getClass() == QueryResultActivity.class) {
            dialogFragment = new DownloadDialogFragment();
            dialogFragment.show(((Activity) mContext).getFragmentManager(), "download_fragment");
        }
        super.onStop();
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        this.max = max;
    }

    @Override
    public void incrementProgressBy(int diff) {
        super.incrementProgressBy(diff);
        this.progress += diff;
        if( progress == max ) {
            this.dismiss();
        }
    }
}