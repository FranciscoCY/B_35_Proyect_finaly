package nttdata.grupouno.com.microwallet.services.implementation;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.microwallet.models.BootCoinDetailModel;
import nttdata.grupouno.com.microwallet.repositories.IBootCoinDetailRepositories;
import nttdata.grupouno.com.microwallet.services.IBootCoinDetailService;
import nttdata.grupouno.com.microwallet.util.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BootCoinDetailService implements IBootCoinDetailService {
    @Autowired
    private IBootCoinDetailRepositories bootCoinDetailRepositorie;

    @Override
    public Flux<BootCoinDetailModel> getAll() {
        return bootCoinDetailRepositorie.findAll();
    }

    @Override
    public Mono<BootCoinDetailModel> getById(String id) {
        return bootCoinDetailRepositorie.findById(id);
    }

    @Override
    public Flux<BootCoinDetailModel> getByCodeWalletRequestAndStatus(String codeWallet, String status) {
        return bootCoinDetailRepositorie.findByCodeWalletRequestAndStatus(codeWallet, status);
    }

    @Override
    public Flux<BootCoinDetailModel> getByCodeWalletResponseAndStatus(String codeWallet, String status) {
        return bootCoinDetailRepositorie.findByCodeWalletResponseAndStatus(codeWallet, status);
    }

    @Override
    public Mono<BootCoinDetailModel> register(BootCoinDetailModel model) {
        model.setId(UUID.randomUUID().toString());
        model.setStatus("S");
        model.setDateRequest(Util.dateToString(new Date()));
        return bootCoinDetailRepositorie.save(model);
    }

    @Override
    public Mono<BootCoinDetailModel> update(BootCoinDetailModel model) {
        return bootCoinDetailRepositorie.save(model);
    }

}
