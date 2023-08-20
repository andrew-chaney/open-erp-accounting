package com.github.andrewchaney.openerpaccounting

import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test


class HealthFT : AbstractBaseFT() {

    @Test
    fun `Health check returns 200 and relevant statuses`() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/actuator/health")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("status", equalTo("UP"))
            body("components.db.status", equalTo("UP"))
        }
    }
}
