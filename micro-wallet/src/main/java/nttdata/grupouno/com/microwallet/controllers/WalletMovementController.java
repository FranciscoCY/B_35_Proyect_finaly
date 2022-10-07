package nttdata.grupouno.com.microwallet.controllers;

import nttdata.grupouno.com.microwallet.models.WalletMovementModel;
import nttdata.grupouno.com.microwallet.models.dto.WalletMovementDto;
import nttdata.grupouno.com.microwallet.services.IWalletMovementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/wallet/movement")
public class WalletMovementController {
    @Autowired
    private IWalletMovementService walletMovementService;

    /**
     * @param walletMovementDto
     * @return
     */
    @PutMapping
    public Mono<ResponseEntity<WalletMovementModel>> registerMovement(@RequestBody final WalletMovementDto walletMovementDto) {
        return walletMovementService.registerMovement(walletMovementDto)
                .map(c -> ResponseEntity.created(URI.create("/wallet/movement/".concat(c.getId().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}