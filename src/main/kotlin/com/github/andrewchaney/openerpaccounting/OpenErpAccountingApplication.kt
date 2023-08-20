package com.github.andrewchaney.openerpaccounting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OpenErpAccountingApplication

fun main(args: Array<String>) {
	runApplication<OpenErpAccountingApplication>(*args)
}
