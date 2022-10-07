package nttdata.grupouno.com.operations.controllers;

import nttdata.grupouno.com.operations.models.DebtClientModel;
import nttdata.grupouno.com.operations.services.IDebtClientService;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import nttdata.grupouno.com.operations.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/operation/debt")
public class DebtClientController {

    @Autowired
    private IDebtClientService debtClientService;
    @Autowired
    private IMasterAccountServices accountServices;
    @Autowired
    private IMovementDetailService movementDetailService;

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createDebt(@Valid @RequestBody Mono<DebtClientModel> request){
        Map<String, Object> response = new HashMap<>();
        return request.flatMap(debtClientModel -> debtClientService.createdDebt(debtClientModel)
                .flatMap(debtCreated -> {
                    response.put("debt", debtCreated);
                    return Mono.just(ResponseEntity.created(URI.create("/operation/debt"))
                            .body(response));
                }))
                .onErrorResume(ex -> Mono.just(ex).cast(WebExchangeBindException.class)
                        .flatMap(e -> Mono.just(e.getFieldErrors()))
                        .flatMapMany(Flux::fromIterable).map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collectList()
                        .flatMap(list -> {
                            response.put("errors", list);
                            return Mono.just(ResponseEntity.badRequest().body(response));
                        })).log();
    }

    @GetMapping("/all")
    @ResponseBody
    public Flux<DebtClientModel> findAllDebt(){
        return debtClientService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DebtClientModel>> findById(@PathVariable("id") String id){
        return debtClientService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pendingDebt/{codCliente}")
    public Mono<ResponseEntity<Flux<DebtClientModel>>> findPendingDebt(@PathVariable("codCliente") String codCliente){
        Flux<DebtClientModel> clientModelFlux = debtClientService.findPendingDebt(codCliente);
        return Mono.just(new ResponseEntity<>(clientModelFlux, clientModelFlux != null ? HttpStatus.OK : HttpStatus.NOT_FOUND));
    }

    @PutMapping("/pay/{id}/{codeClient}")
    public Mono<ResponseEntity<Map<String, Object>>> payDebt(@PathVariable("id") final String id,
                                                             @PathVariable("codeClient") final String codeClient){
        Map<String, Object> response = new HashMap<>();
        String uriPayDebt= "/operation/debt/pay".concat(id);
        Date today = new Date();
        return debtClientService.findById(id)
                .flatMap(a -> {
                    if (a.getState().equals("C")){
                        response.put("msg","La deuda se encuentra cancelada");
                        return Mono.just(ResponseEntity.created(URI.create(uriPayDebt))
                                .body(response));
                    }
                    return accountServices.findByClient(codeClient)
                            .take(1).next()
                            .flatMap(b -> {
                                if (b.getAmount() < a.getAmount()){
                                    response.put("msg","El Cliente no cuenta con saldo suficiente");
                                    return Mono.just(ResponseEntity.created(URI.create(uriPayDebt))
                                            .body(response));
                                }
                                return movementDetailService.withdrawAmount(b.getId(),a.getAmount())
                                        .flatMap(c -> {
                                            a.setState("C");
                                            a.setPaymentDate(Util.dateToString(today));
                                            a.setPaidBy(codeClient);
                                            return debtClientService.updatedDebt(id ,a)
                                                    .flatMap(debt -> {
                                                        response.put("msg", "Deuda cancelada con exitó");
                                                        response.put("dbt", debt);
                                                        return Mono.just(ResponseEntity.created(URI.create(uriPayDebt))
                                                                .body(response));
                                                    });
                                        });
                            });
                });
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDebt(@PathVariable("id") final String id){
        return debtClientService.findById(id)
                .flatMap(debtClientModel -> debtClientService.deleteDebtById(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
