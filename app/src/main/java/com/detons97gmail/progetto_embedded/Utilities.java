package com.detons97gmail.progetto_embedded;

import android.content.Context;
import android.widget.Toast;

class Utilities {
    static void showToast(Context context, String message, int duration){
        Toast.makeText(context, message, duration).show();
    }
}
