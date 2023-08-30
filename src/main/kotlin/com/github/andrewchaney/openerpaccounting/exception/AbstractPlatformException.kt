package com.github.andrewchaney.openerpaccounting.exception

abstract class AbstractPlatformException(
    val platformException: PlatformExceptionConfiguration
) : RuntimeException(platformException.msg)
