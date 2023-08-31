package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.AbstractBaseFT
import com.github.andrewchaney.openerpaccounting.ledger.model.EntryType
import com.github.andrewchaney.openerpaccounting.ledger.view.LedgerRepository
import com.github.andrewchaney.openerpaccounting.ledger.view.specification.LedgerSpecification
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryRequest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.path.json.JsonPath
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigDecimal

class LedgerGetAllFT : AbstractBaseFT() {

    companion object {
        private val amazonReq = LedgerEntryRequest(
            title = "AmazonEntry",
            type = EntryType.EXPENSE,
            associatedCompany = "Amazon",
            amount = BigDecimal("1100.00"),
            notes = null,
            tags = setOf("Amazon", "cloud infrastructure", "infrastructure", "cloud"),
        )

        private val googleReqA = LedgerEntryRequest(
            title = "GoogleEntryA",
            type = EntryType.EXPENSE,
            associatedCompany = "Google",
            amount = BigDecimal("840.00"),
            notes = null,
            tags = setOf("Google", "cloud infrastructure", "infrastructure", "cloud"),
        )

        private val googleReqB = LedgerEntryRequest(
            title = "GoogleEntryB",
            type = EntryType.EXPENSE,
            associatedCompany = "Google",
            amount = BigDecimal("900.00"),
            notes = null,
            tags = setOf("Google", "cloud infrastructure", "cloud"),
        )

        private val lenovoReq = LedgerEntryRequest(
            title = "Lenovo Thinkpads",
            type = EntryType.EXPENSE,
            associatedCompany = "IBM",
            amount = BigDecimal("1000000.00"),
            notes = "we bought all of the thinkpads on the market",
            tags = setOf("IBM", "thinkpads", "IT"),
        )

        private val saleReq = LedgerEntryRequest(
            title = "Our First Sale",
            type = EntryType.REVENUE,
            associatedCompany = "Mom & Dad",
            amount = BigDecimal("13.00"),
            notes = "after a million dollars of seed funding, we have our first sale to the owner's parents",
            tags = setOf("thanks_mom_and_dad", "big bucks"),
        )
    }

    @Autowired
    private lateinit var ledgerRepository: LedgerRepository

    @Autowired
    private lateinit var ledgerSpecification: LedgerSpecification

    private lateinit var amazonEntry: JsonPath
    private lateinit var googleEntryA: JsonPath
    private lateinit var googleEntryB: JsonPath
    private lateinit var lenovoEntry: JsonPath
    private lateinit var saleEntry: JsonPath

    @BeforeEach
    fun refresh() {
        fun persistEntry(request: LedgerEntryRequest): JsonPath {
            return Given {
                accept(ContentType.JSON)
                contentType(ContentType.JSON)
                body(request)
            } When {
                post("/ledger")
            } Then {
                statusCode(HttpStatus.SC_CREATED)
            } Extract {
                body()
                jsonPath()
            }
        }

        ledgerRepository.deleteAll()

        amazonEntry = persistEntry(amazonReq)
        googleEntryA = persistEntry(googleReqA)
        googleEntryB = persistEntry(googleReqB)
        lenovoEntry = persistEntry(lenovoReq)
        saleEntry = persistEntry(saleReq)
    }

    @Test
    fun `get all returns all entries`() {
        Given {
            accept(ContentType.JSON)
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            // Returned Ledger Test
            body("_embedded.ledgerList.size()", equalTo(5))
            body("_embedded.ledgerList.any { it.title == '${amazonReq.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${googleReqA.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${googleReqB.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${lenovoReq.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${saleReq.title}' }", `is`(true))
            // Page Tests
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(5))
            body("page.totalPages", equalTo(1))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `can get all with specific page sizes`() {
        Given {
            accept(ContentType.JSON)
            param("limit", 2)
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("_embedded.ledgerList.size()", equalTo(2))
            body("_embedded.ledgerList.any { it.title == '${saleReq.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${lenovoReq.title}' }", `is`(true))
            body("page.size", equalTo(2))
            body("page.totalElements", equalTo(5))
            body("page.totalPages", equalTo(3))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `can get all by specific types`() {
        Given {
            accept(ContentType.JSON)
            param("type", EntryType.REVENUE)
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("_embedded.ledgerList.size()", equalTo(1))
            body("_embedded.ledgerList.any { it.title == '${saleReq.title}' }", `is`(true))
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(1))
            body("page.totalPages", equalTo(1))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `can get all by specific companies`() {
        Given {
            accept(ContentType.JSON)
            param("associatedCompany", "Google")
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("_embedded.ledgerList.size()", equalTo(2))
            body("_embedded.ledgerList.any { it.title == '${googleReqA.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${googleReqB.title}' }", `is`(true))
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(2))
            body("page.totalPages", equalTo(1))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `can get all by specific tags`() {
        Given {
            accept(ContentType.JSON)
            param("tags", setOf("infrastructure", "Google"))
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("_embedded.ledgerList.size()", equalTo(1))
            body("_embedded.ledgerList.any { it.title == '${googleReqA.title}' }", `is`(true))
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(1))
            body("page.totalPages", equalTo(1))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `can get all by a combination of types, companies, and tags`() {
        Given {
            accept(ContentType.JSON)
            param("type", EntryType.EXPENSE)
            param("associatedCompany", "Google")
            param("tags", setOf("cloud", "cloud infrastructure"))
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("_embedded.ledgerList.size()", equalTo(2))
            body("_embedded.ledgerList.any { it.title == '${googleReqA.title}' }", `is`(true))
            body("_embedded.ledgerList.any { it.title == '${googleReqB.title}' }", `is`(true))
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(2))
            body("page.totalPages", equalTo(1))
            body("page.number", equalTo(0))
        }
    }

    @Test
    fun `a query with no valid results returns an empty list`() {
        Given {
            accept(ContentType.JSON)
            param("tags", setOf("nonexistent_tag"))
        } When {
            get("/ledger")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("page.size", equalTo(100))
            body("page.totalElements", equalTo(0))
            body("page.totalPages", equalTo(0))
            body("page.number", equalTo(0))
        }
    }
}
