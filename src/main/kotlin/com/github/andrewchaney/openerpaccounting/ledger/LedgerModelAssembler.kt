package com.github.andrewchaney.openerpaccounting.ledger

import com.github.andrewchaney.openerpaccounting.ledger.view.Ledger
import com.github.andrewchaney.openerpaccounting.ledger.wire.LedgerEntryResponse
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class LedgerModelAssembler : RepresentationModelAssemblerSupport<Ledger, LedgerEntryResponse>(
    Ledger::class.java, LedgerEntryResponse::class.java
) {

    override fun instantiateModel(entity: Ledger): LedgerEntryResponse = LedgerEntryResponse(entity)

    override fun toModel(entity: Ledger): LedgerEntryResponse {
        return instantiateModel(entity)
            .add(
                linkTo<LedgerController> { getLedgerEntryById(entity.ledgerId) }.withSelfRel()
            )
    }
}
