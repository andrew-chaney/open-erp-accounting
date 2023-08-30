package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.AbstractBaseFT
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.path.json.JsonPath
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class LedgerGetByIdFT : AbstractBaseFT() {

    @Autowired
    private lateinit var ledgerRepository: LedgerRepository

    private lateinit var entry: JsonPath

    @BeforeEach
    fun refresh() {
        ledgerRepository.deleteAll()

        entry = Given {
            accept(ContentType.JSON)
            contentType(ContentType.JSON)
            body(
                LedgerEntryRequest(
                    title = "Test",
                    type = EntryType.REVENUE,
                    associatedCompany = "Yahoo Inc.",
                    amount = BigDecimal("12500.00"),
                    notes = "some notes about this entry here",
                    tags = setOf("test", "services rendered", "yahoo"),
                )
            )
        } When {
            post("/ledger")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            body()
            jsonPath()
        }
    }

    @Test
    fun `can get an entry by its id`() {
        Given {
            accept(ContentType.JSON)
        } When {
            get(entry.getString("_links.self.href")) // get by the self reference link returned in the response
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("ledgerId", equalTo(entry.getString("ledgerId")))
            body("title", equalTo(entry.getString("title")))
            body("_links.self.href", equalTo(entry.getString("_links.self.href")))
        }
    }

    @Test
    fun `attempting to get an event with an invalid id returns 404`() {
        Given {
            param("id", UUID.randomUUID())
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
        }
    }

    @Test
    fun `id param that is anything but a UUID returns a 400`() {
        Given {
            param("id", "I'm a string, not UUID")
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
        }
    }
}
