package com.github.andrewchaney.openerpaccounting.root

import com.github.andrewchaney.openerpaccounting.ledger.LedgerController
import com.github.andrewchaney.openerpaccounting.root.wire.RootResponse
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class RootController {

    @GetMapping
    fun getRootLinks(): ResponseEntity<RootResponse> {
        return ResponseEntity
            .ok()
            .body(
                RootResponse()
                    .add(linkTo<RootController> { getRootLinks() }.withSelfRel())
                    .add(
                        linkTo<LedgerController> {
                            getAllLedgerEntries(null, null, null, 0, 100)
                        }
                            .withRel("getAllLedgerEntries")
                            .expand(null)
                    )
            )
    }
}
