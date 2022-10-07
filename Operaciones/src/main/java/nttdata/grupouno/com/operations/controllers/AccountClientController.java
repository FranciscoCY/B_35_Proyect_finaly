package nttdata.grupouno.com.operations.controllers;

import javax.validation.Valid;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.dto.AccountClientDto;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.services.IAccountClientService;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/operation/accountClient")
public class AccountClientController {
    @Autowired
    private IAccountClientService accountClientService;

    @Autowired
    private IMasterAccountServices masterAccountServices;

    @PostMapping("/")
    public Mono<ResponseEntity<Mono<AccountClientModel>>> addAccountClient(@Valid @RequestBody AccountClientModel request){
        Mono<AccountClientModel> account = accountClientService.registerClient(request);
        return Mono.just(new ResponseEntity<>(account, account != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<AccountClientModel> findAll(){
        return accountClientService.findAll();
    }


    @PostMapping("/assignPrincipalAccount")
    public Mono<ResponseEntity<Map<String, Object>>> assignPrincipalAccount(@Valid @RequestBody Mono<AccountClientDto> request){
        Map<String, Object> response = new HashMap<>();

        return request.flatMap(x -> accountClientService.assignPrincipalAccount(x)
                        .flatMap(
                                y -> {
                                    response.put("account client", y);
                                    return Mono.just(ResponseEntity.created(URI.create("/operation/accountClient/")).body(response));
                                }
                        )
                        .switchIfEmpty(
                                Mono.just(ResponseEntity.badRequest().body(response))))
                .onErrorResume(ex -> Mono.just(ex).cast(WebExchangeBindException.class)
                        .flatMap(e -> Mono.just(e.getFieldErrors()))
                        .flatMapMany(Flux::fromIterable).map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collectList()
                        .flatMap(list -> {
                            response.put("error", list);
                            return Mono.just(ResponseEntity.badRequest().body(response));
                        }));
    }


    @GetMapping("/findByidCartClient/{idcartclient}")
    public Flux<AccountClientModel> findByidCartClient(@PathVariable("idcartclient") String idcartclient){
        return accountClientService.findByidCartClient(idcartclient);
    }

    @GetMapping("/checkBalancePrincipalAccount/{idcartclient}")
    public Mono<MasterAccountModel> checkBalancePrincipalAccount(@PathVariable("idcartclient") String idCartClient){
        return accountClientService.findByidCartClient(idCartClient).flatMap(
                a -> {
                    if (a.getPrincipalAccount().equals("S")){
                        return masterAccountServices.findByAccount(a.getNumberAccount());
                    }
                    return Mono.empty();
                }).single();
    }

}
