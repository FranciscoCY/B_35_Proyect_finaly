package nttdata.grupouno.com.microwallet.controllers;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import nttdata.grupouno.com.microwallet.services.IClientWalletService;
import nttdata.grupouno.com.microwallet.services.ITypeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("/wallet/client")
public class ClientWalletController {
    @Autowired
    private IClientWalletService clientWalletService;
    @Autowired
    private ITypeDocumentService typeDocumentService;

    @PostMapping("/")
    public Mono<Map<String, Object>> registerClient(@RequestBody @Valid Mono<ClientWalletModel> request){
        Map<String, Object> response = new HashMap<>();
        return request
                .flatMap(x ->
                        typeDocumentService.getOne(x.getTypeDocument())
                                .map(y -> x)
                                .defaultIfEmpty(new ClientWalletModel())
                )
                .flatMap(x -> {
                        if(x.getEmail() == null || x.getEmail().isBlank())
                        {
                                response.put("valid", "No se logró identificar el tipo de documento");
                                return Mono.just(response);
                        }
                        return clientWalletService.findByNumberDocumentAndTypeDocument(x.getNumberDocument(), x.getTypeDocument())
                                .flatMap(y -> {
                                        response.put("validWallet", "El cliente Wallet ya existe");
                                        response.put("clientWallet", y);
                                        return Mono.just(response);
                                })
                                .switchIfEmpty(
                                        clientWalletService.register(x).flatMap(
                                                y -> {
                                                        response.put("clientWallet", y);
                                                        return Mono.just(response);
                                                }
                                        )
                                );
                });
    }
}
