package com.rotary.hospital.utils// iosMain
import platform.Foundation.NSLog

actual object Logger {
    actual fun d(tag: String, message: String) {
        NSLog("DEBUG [$tag]: $message")
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        NSLog("ERROR [$tag]: $message - ${throwable?.message}")
    }
}
