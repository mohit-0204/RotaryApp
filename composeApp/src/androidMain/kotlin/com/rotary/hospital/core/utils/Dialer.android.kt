package com.rotary.hospital.core.utils

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

actual fun dial(phone: String, context: PlatformContext?): Boolean {
    val ctx = context?.context ?: return false // Access the 'context' property of PlatformContext
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = ("tel:" + Uri.encode(phone)).toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return try {
        ctx.startActivity(intent)
        true
    } catch (_: Throwable) {
        false
    }
}