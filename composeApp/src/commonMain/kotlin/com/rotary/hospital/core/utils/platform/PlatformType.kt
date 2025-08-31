package com.rotary.hospital.core.utils.platform

enum class PlatformType {
    IOS,
    ANDROID
}


expect val platformType:PlatformType


val PlatformType.isIos: Boolean
    get() = this == PlatformType.IOS
val PlatformType.isAndroid: Boolean
    get() = this == PlatformType.ANDROID