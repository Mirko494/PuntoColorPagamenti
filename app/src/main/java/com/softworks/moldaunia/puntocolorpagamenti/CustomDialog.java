package com.softworks.moldaunia.puntocolorpagamenti;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class CustomDialog extends AlertDialog {
    protected CustomDialog(@NonNull Context context) {
        super(context);
    }

    protected CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
