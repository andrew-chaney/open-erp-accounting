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

class LedgerPutByIdFT : AbstractBaseFT() {

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
    fun `updating an existing ledger entry successfully returns 200`() {
        val updateRequest = LedgerEntryRequest(
            title = "updated test",
            type = EntryType.EXPENSE,
            associatedCompany = "Not Yahoo Inc.",
            amount = BigDecimal("12500.00"),
            notes = "some notes about this entry here",
            tags = setOf("test", "services rendered", "yahoo"),
        )

        Given {
            accept(ContentType.JSON)
            contentType(ContentType.JSON)
            body(updateRequest)
        } When {
            put(entry.getString("_links.self.href"))
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("title", equalTo(updateRequest.title))
            body("type", equalTo(updateRequest.type.name))
            body("associatedCompany", equalTo(updateRequest.associatedCompany))
            body("amount", equalTo(entry.getFloat("amount")))
            body("notes", equalTo(updateRequest.notes))
            body("createdTsEpoch", equalTo(entry.getString("createdTsEpoch")))
        }
    }

    @Test
    fun `attempting to update a ledger entry that does not exist returns 404`() {
        val updateRequest = LedgerEntryRequest(
            title = "updated test",
            type = EntryType.EXPENSE,
            associatedCompany = "Not Yahoo Inc.",
            amount = BigDecimal("12500.00"),
            notes = "some notes about this entry here",
            tags = setOf("test", "services rendered", "yahoo"),
        )

        Given {
            contentType(ContentType.JSON)
            pathParam("id", UUID.randomUUID())
            body(updateRequest)
        } When {
            put("/ledger/{id}")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
        }
    }
}
