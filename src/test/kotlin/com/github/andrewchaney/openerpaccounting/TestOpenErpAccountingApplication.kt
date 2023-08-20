package com.github.andrewchaney.openerpaccounting

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.with

@TestConfiguration(proxyBeanMethods = false)
class TestOpenErpAccountingApplication

fun main(args: Array<String>) {
	fromApplication<OpenErpAccountingApplication>().with(TestOpenErpAccountingApplication::class).run(*args)
}
