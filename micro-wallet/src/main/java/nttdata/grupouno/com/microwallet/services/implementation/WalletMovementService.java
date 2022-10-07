package nttdata.grupouno.com.microwallet.services.implementation;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import nttdata.grupouno.com.microwallet.models.WalletMovementModel;
import nttdata.grupouno.com.microwallet.models.dto.WalletMovementDto;
import nttdata.grupouno.com.microwallet.repositories.IClientWalletRepositories;
import nttdata.grupouno.com.microwallet.repositories.IWalletMovementRepository;
import nttdata.grupouno.com.microwallet.services.IClientWalletService;
import nttdata.grupouno.com.microwallet.services.IWalletMovementService;
import nttdata.grupouno.com.microwallet.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;

@Service
public class WalletMovementService implements IWalletMovementService {
    @Autowired
    private IWalletMovementRepository IWalletMovementRepository;

    @Autowired
    private IClientWalletRepositories clientWalletRepositories;

    @Override
    public Mono<WalletMovementModel> registerMovement(WalletMovementDto walletMovementDto) {
                return clientWalletRepositories.findByNumberPhone(walletMovementDto.getNumberPhone())
                .flatMap(x -> {
                    double newAmount = 0.0;
                    if(walletMovementDto.getMovementType().equals("E")){
                        if(x.getAmount()<walletMovementDto.getAmount()){
                            //saldo insuficiente para pago
                            return Mono.empty();
                        }
                        newAmount = x.getAmount() - walletMovementDto.getAmount();
                        return Mono.just(x);
                    }
                    else{
                        newAmount  = x.getAmount() + walletMovementDto.getAmount();
                    }
                    x.setAmount(newAmount);
                    return Mono.just(x);
                })
                .flatMap(x -> clientWalletRepositories.save(x))
                .map(x ->  new WalletMovementModel(UUID.randomUUID().toString(), x.getId() , Util.dateTimeToString(new Date()), walletMovementDto.getAmount(), walletMovementDto.getMovementType(), walletMovementDto.getCurrency(), Util.getMonth(new Date()), Util.getYear(new Date()) ))
                .flatMap(x -> IWalletMovementRepository.save(x))
                .switchIfEmpty(Mono.empty());
    }
}
