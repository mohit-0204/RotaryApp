package com.rotary.hospital.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun dial(phone: String, context: PlatformContext?): Boolean {
    val url = NSURL(string = "tel://$phone")
    return if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
        true
    } else {
        false
    }
}