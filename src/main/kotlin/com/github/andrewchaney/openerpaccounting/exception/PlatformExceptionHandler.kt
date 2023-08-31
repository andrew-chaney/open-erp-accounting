package com.github.andrewchaney.openerpaccounting.exception

import com.github.andrewchaney.openerpaccounting.configuration.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class PlatformExceptionHandler {

    private val log by logger()

    @ExceptionHandler(AbstractPlatformException::class)
    fun platformExceptionHandler(exception: AbstractPlatformException): ResponseEntity<String> {
        log.info("handling exception ", exception)

        return ResponseEntity
            .status(exception.platformException.status)
            .body(exception.platformException.msg)
    }
}
