package com.github.andrewchaney.openerpaccounting.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject

fun <R : Any> R.logger(): Lazy<Logger> =
    lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }

fun <T : Any> unwrapCompanionClass(clazz: Class<T>): Class<*> =
    clazz.enclosingClass
        ?.takeIf {
            clazz.enclosingClass.kotlin.companionObject?.java == clazz
        }
        ?: clazz

fun <T : Any> unwrapCompanionClass(clazz: KClass<T>): KClass<*> = unwrapCompanionClass(clazz.java).kotlin
