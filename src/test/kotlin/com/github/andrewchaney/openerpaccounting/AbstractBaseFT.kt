package com.github.andrewchaney.openerpaccounting

import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractBaseFT {

    @LocalServerPort
    val port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.reset()
        RestAssured.port = port
    }
}
