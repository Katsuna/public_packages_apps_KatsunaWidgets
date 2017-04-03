package com.katsuna.widgets.commons.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class EmailSender {

    public static void sendLogFile(Context context, String email, String subject, String filename) {
        if (filename == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filename));
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached."); // do this so some email clients don't complain about empty body.
        context.startActivity(intent);
    }

}
