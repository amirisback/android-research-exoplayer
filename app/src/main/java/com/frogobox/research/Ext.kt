package com.frogobox.research

import android.content.Context

// Created by (M. Faisal Amir) on 09/08/22.

fun Context.showToast(message: String) {
    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
}
