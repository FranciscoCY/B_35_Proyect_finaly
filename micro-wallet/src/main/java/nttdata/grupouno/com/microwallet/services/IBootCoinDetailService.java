package nttdata.grupouno.com.microwallet.services;

import nttdata.grupouno.com.microwallet.models.BootCoinDetailModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IBootCoinDetailService {
    Flux<BootCoinDetailModel> getAll();
    Mono<BootCoinDetailModel> getById(String id);
    Flux<BootCoinDetailModel> getByCodeWalletRequestAndStatus(String codeWallet, String status); 
    Flux<BootCoinDetailModel> getByCodeWalletResponseAndStatus(String codeWallet, String status); 
    Mono<BootCoinDetailModel> register(BootCoinDetailModel model);
    Mono<BootCoinDetailModel> update(BootCoinDetailModel model);
}
