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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal
import java.util.*

class LedgerDeleteByIdFT : AbstractBaseFT() {

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
    fun `can delete an existing entry by its ID and get back a 204`() {
        When {
            delete(entry.getString("_links.delete.href"))
        } Then {
            statusCode(HttpStatus.SC_NO_CONTENT)
        }
    }

    @Test
    fun `deleting an entry that does not exist still returns a 204`() {
        Given {
            pathParam("id", UUID.randomUUID())
        } When {
            delete("/ledger/{id}")
        } Then {
            statusCode(HttpStatus.SC_NO_CONTENT)
        }
    }
}
