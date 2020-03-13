package com.ride.me.utils;

import androidx.annotation.StringRes;
import android.view.View;

/**
 * Created by David Studio on 10/17/2017.
 */

public interface SnackbarController {
    void showSnackbar(@StringRes int stringRes, int duration, @StringRes int actionResText, View.OnClickListener onClickListener);
}
