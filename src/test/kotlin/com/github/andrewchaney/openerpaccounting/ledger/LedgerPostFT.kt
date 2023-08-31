package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.AbstractBaseFT
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import com.github.andrewchaney.openerpaccounting.ledger.view.TagRepository
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.commons.validator.routines.UrlValidator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class LedgerPostFT : AbstractBaseFT() {

    @Autowired
    private lateinit var ledgerRepository: LedgerRepository

    @Autowired
    private lateinit var tagRepository: TagRepository

    @BeforeEach
    fun reset() {
        ledgerRepository.deleteAll()
    }

    @Test
    fun `can create an entry and get back the valid response`() {
        val request = LedgerEntryRequest(
            title = "Test",
            type = EntryType.EXPENSE,
            associatedCompany = "Google Inc.",
            amount = BigDecimal("350.00"),
            notes = null,
            tags = setOf("test", "gas", "google"),
        )

        val expectedTags = request.tags?.toList()

        val response = Given {
            accept(ContentType.JSON)
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
            body("ledgerId", notNullValue())
            body("title", equalTo(request.title))
            body("type", equalTo(request.type.name))
            body("amount", equalTo(request.amount.toFloat()))
            body("notes", equalTo(request.notes))
            body("createdTsEpoch", notNullValue())
            body("updatedTsEpoch", notNullValue())
            body("tags.any { it.name == '${expectedTags?.get(0)}' }", `is`(true))
            body("tags.any { it.name == '${expectedTags?.get(1)}' }", `is`(true))
            body("tags.any { it.name == '${expectedTags?.get(2)}' }", `is`(true))
        } Extract {
            body()
            jsonPath()
        }

        assertThat(
            UrlValidator(UrlValidator.ALLOW_LOCAL_URLS).isValid(response.getString("_links.self.href"))
        ).isTrue()
    }

    @Test
    fun `cannot create an entry without a title`() {
        val request = """
            {
                "type": "EXPENSE",
                "associatedCompany": "Google Inc.",
                "amount": 350.00
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }

    @Test
    fun `cannot create an entry without an entry type`() {
        val request = """
            {
                "title": "Test",
                "associatedCompany": "Google Inc.",
                "amount": 350.00
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }

    @Test
    fun `cannot create an entry without a valid entry type`() {
        val request = """
            {
                "title": "Test",
                "type": "ASSET",
                "associatedCompany": "Google Inc.",
                "amount": 350.00
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }

    @Test
    fun `cannot create an entry without an associated company`() {
        val request = """
            {
                "title": "Test",
                "type": "EXPENSE",
                "amount": 350.00
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }

    @Test
    fun `cannot create an entry without an amount`() {
        val request = """
            {
                "title": "Test",
                "type": "EXPENSE",
                "associatedCompany": "Google Inc.",
            }
        """.trimIndent()

        Given {
            contentType(ContentType.JSON)
            body(request)
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }
}
