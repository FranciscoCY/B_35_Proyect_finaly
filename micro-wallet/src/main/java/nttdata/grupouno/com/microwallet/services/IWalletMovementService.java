package nttdata.grupouno.com.microwallet.services;

import nttdata.grupouno.com.microwallet.models.WalletMovementModel;
import nttdata.grupouno.com.microwallet.models.dto.WalletMovementDto;
import reactor.core.publisher.Mono;

public interface IWalletMovementService {
    Mono<WalletMovementModel> registerMovement(WalletMovementDto walletMovementDto) ;

}
