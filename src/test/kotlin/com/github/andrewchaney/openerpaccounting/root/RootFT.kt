package com.github.andrewchaney.openerpaccounting.root

import com.github.andrewchaney.openerpaccounting.AbstractBaseFT
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.commons.validator.routines.UrlValidator
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RootFT : AbstractBaseFT() {

    private val validator = UrlValidator(UrlValidator.ALLOW_LOCAL_URLS)

    @Test
    fun `get call on root of api returns valid api links`() {
        val response = Given {
            accept(ContentType.JSON)
        } When {
            get("/")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body()
            jsonPath()
        }

        assertThat(validator.isValid(response.getString("_links.self.href"))).isTrue()
        assertThat(validator.isValid(response.getString("_links.getAllLedgerEntries.href"))).isTrue()
    }
}
