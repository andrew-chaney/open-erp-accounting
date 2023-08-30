package com.github.andrewchaney.openerpaccounting.exception

import org.springframework.http.HttpStatus

enum class PlatformExceptionConfiguration(val status: HttpStatus, val msg: String) {
    ENTRY_NOT_FOUND_EXCEPTION(
        HttpStatus.NOT_FOUND,
        "Entry with the given parameters not found",
    ),
}
