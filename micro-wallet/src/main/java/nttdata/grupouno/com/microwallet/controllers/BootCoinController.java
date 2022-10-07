package nttdata.grupouno.com.microwallet.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import nttdata.grupouno.com.microwallet.models.BootCoinDetailModel;
import nttdata.grupouno.com.microwallet.services.IBootCoinDetailService;
import nttdata.grupouno.com.microwallet.services.IClientWalletService;
import nttdata.grupouno.com.microwallet.services.ITypeChangeService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/wallet/bootCoin")
public class BootCoinController {
    @Autowired
    private IBootCoinDetailService bootCoinDetailService;
    @Autowired
    private IClientWalletService clientWalletService;
    @Autowired
    private ITypeChangeService typeChangeService;

    @PostMapping(value="/request")
    public Mono<ResponseEntity<Map<String, Object>>> requestBuy(@RequestBody Mono<BootCoinDetailModel> entity) {
        Map<String, Object> response = new HashMap<>();
        
        return entity
            .flatMap(x -> 
                clientWalletService.findById(x.getCodeWalletRequest())
                    .flatMap(y -> Mono.just(x))
                    .switchIfEmpty(Mono.defer(() -> {
                        response.put("validWalletRequest", "Cliente wallet no existente");
                        return Mono.empty();
                    }))
            )
            .flatMap(x -> 
                clientWalletService.findById(x.getCodeWalletResponse())
                    .flatMap(y -> Mono.just(x))
                    .switchIfEmpty(Mono.defer(() -> {
                        response.put("validWalletResponse", "Cliente de respuesta wallet no existente");
                        return Mono.empty();
                    }))
            )
            .flatMap(x -> {
                if(!x.getModePay().equals("T") && !x.getModePay().equals("Y"))
                    return Mono.empty();
                return Mono.just(x);
            })
            .flatMap(x -> {
                if(!x.getTypeOperation().equals("V") && !x.getTypeOperation().equals("C"))
                    return Mono.empty();
                return Mono.just(x);
            })
            .flatMap(x -> 
                typeChangeService.fingByDateAndTypeCoin(new Date(), "BOC")
                    .flatMap(y -> {
                        if(x.getTypeOperation().equals("C"))
                            x.setAmountTypeChange(y.getAmountBuy());
                        if(x.getTypeOperation().equals("V"))
                            x.setAmountTypeChange(y.getAmountSale());
                        return Mono.just(x);
                    })
                    .switchIfEmpty(Mono.defer(() -> {
                        response.put("validTypeChange", "El tipo de cambio para BootCoin no está disponible");
                        return Mono.empty();
                    }))
            )
            .flatMap(x -> 
                bootCoinDetailService.register(x)
                    .flatMap(y -> {
                        response.put("requestBuy", y);
                        response.put("menssage", "El código de transacción será emitido una vez el usuario confirme la operación.");
                        return Mono.just(ResponseEntity.accepted().body(response));
                    })
            )
            .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body(response)))
            .onErrorResume(ex -> Mono.just(ex).cast(WebExchangeBindException.class)
            .flatMap(e -> Mono.just(e.getFieldErrors()))
            .flatMapMany(Flux::fromIterable).map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collectList()
            .flatMap(list -> {
                response.put("error", list);
                return Mono.just(ResponseEntity.badRequest().body(response));
            })).log();
    }

    @PutMapping(value="/proceedExchange")
    public BootCoinDetailModel postMethodName(@RequestParam String id) {

        return null;
    }
    
    @GetMapping("/all")
    public Flux<BootCoinDetailModel> getAllDetail(){
        return bootCoinDetailService.getAll();
    }

    @GetMapping("/{id}}")
    public Mono<BootCoinDetailModel> getById(@RequestParam String id){
        return bootCoinDetailService.getById(id);
    }
}
