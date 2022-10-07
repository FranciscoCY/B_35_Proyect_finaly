package nttdata.grupouno.com.microwallet.services.implementation;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.microwallet.models.BootCoinDetailModel;
import nttdata.grupouno.com.microwallet.repositories.IBootCoinDetailRepositories;
import nttdata.grupouno.com.microwallet.repositories.IClientWalletRepositories;
import nttdata.grupouno.com.microwallet.services.IBootCoinDetailService;
import nttdata.grupouno.com.microwallet.util.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BootCoinDetailService implements IBootCoinDetailService {
    @Autowired
    private IBootCoinDetailRepositories bootCoinDetailRepositorie;
    @Autowired
    private IClientWalletRepositories clienteWallerRepositorie;

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
        model.setNumberTransaction(UUID.randomUUID().toString());

        if(!model.getModePay().equals("Y")){
            return bootCoinDetailRepositorie.save(model)
                .doOnSuccess(x -> {
                    /// Validar la transferencia con kafka
                });
        }

        Double amountCompra = model.getAmountTypeChange() / model.getAmountConvert(); // lo que necesito para comprar BootCoint

        //dsiminuye mi saldo y aumente mi bootcoint
        if(model.getTypeOperation().equals("C")){ 
            return clienteWallerRepositorie.findById(model.getCodeWalletRequest())
                .flatMap(
                    x -> {
                        if(amountCompra < x.getAmount()) Mono.empty();
                        if(x.getAmountBootCoin() == null) x.setAmountBootCoin(0.00);
                        
                        x.setAmount(x.getAmount() - amountCompra);
                        x.setAmountBootCoin(x.getAmountBootCoin() + model.getAmountConvert());
                        return Mono.just(x);
                }).flatMap(x -> {
                        return clienteWallerRepositorie.findById(model.getCodeWalletResponse())
                            .flatMap(y -> {
                                if(y.getAmountBootCoin() < model.getAmountConvert()){
                                    return Mono.empty();
                                }
                                y.setAmountBootCoin(y.getAmountBootCoin() - model.getAmountConvert());
                                y.setAmount(y.getAmount() + amountCompra);
                                return Mono.just(y);
                            })
                            .flatMap(y -> clienteWallerRepositorie.save(x).flatMap(z -> Mono.just(y)))
                            .flatMap(y -> clienteWallerRepositorie.save(y).flatMap(z -> Mono.just(x)))
                            .flatMap(y -> {
                                model.setStatus("A");
                                model.setDateAccept(Util.dateToString(new Date()));
                                return bootCoinDetailRepositorie.save(model);
                            });
                })
                .switchIfEmpty(Mono.empty());
        }

        Double amountVenta = model.getAmountConvert() * model.getAmountConvert(); // lo que voy a vender
        /// BootCoin -> soles

        return clienteWallerRepositorie.findById(model.getCodeWalletResponse())
                .flatMap(
                    x -> {
                        if(amountVenta < x.getAmount()) Mono.empty();
                        if(x.getAmountBootCoin() == null) x.setAmountBootCoin(0.00);
                        
                        x.setAmount(x.getAmount() - amountCompra);
                        x.setAmountBootCoin(x.getAmountBootCoin() + model.getAmountConvert());
                        return Mono.just(x);
                }).flatMap(x -> {
                        return clienteWallerRepositorie.findById(model.getCodeWalletRequest())
                            .flatMap(y -> {
                                if(y.getAmountBootCoin() < model.getAmountConvert()){
                                    return Mono.empty();
                                }
                                y.setAmountBootCoin(y.getAmountBootCoin() - model.getAmountConvert());
                                y.setAmount(y.getAmount() + amountVenta);
                                return Mono.just(y);
                            })
                            .flatMap(y -> clienteWallerRepositorie.save(x).flatMap(z -> Mono.just(y)))
                            .flatMap(y -> clienteWallerRepositorie.save(y).flatMap(z -> Mono.just(x)))
                            .flatMap(y -> {
                                model.setStatus("A");
                                return bootCoinDetailRepositorie.save(model);
                            });
                })
                .switchIfEmpty(Mono.empty());
    }

}
