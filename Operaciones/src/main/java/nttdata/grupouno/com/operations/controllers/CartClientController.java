package nttdata.grupouno.com.operations.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import nttdata.grupouno.com.operations.models.CartClientModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.services.ICartClientService;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/operation/cartClient")
public class CartClientController {
    @Autowired
    private ICartClientService cartClientService;
    @Autowired
    private IMovementDetailService movementDetailService;

    @PostMapping("/createCard")
    public Mono<ResponseEntity<Map<String, Object>>> createCard(@Valid @RequestBody Mono<CartClientModel> request){
        Map<String, Object> response = new HashMap<>();
        
        return request.flatMap(x -> cartClientService.registerCardNumber(x)
        .flatMap(
            y -> {
                response.put("tarjet", y);
                return Mono.just(ResponseEntity.created(URI.create("/operation/cartClient/")).body(response));
            }
        )
        .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body(response))))
        .onErrorResume(ex -> Mono.just(ex).cast(WebExchangeBindException.class)
        .flatMap(e -> Mono.just(e.getFieldErrors()))
        .flatMapMany(Flux::fromIterable).map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collectList()
        .flatMap(list -> {
            response.put("error", list);
            return Mono.just(ResponseEntity.badRequest().body(response));
        })); 
    }

    @GetMapping("/{id}")
    public Mono<CartClientModel> findById(@PathVariable("id") String id){
        return cartClientService.findById(id);
    }

    @GetMapping("/hs/{hash}")
    public Mono<CartClientModel> findByHashNumber(@PathVariable("hash") String hash){
        return cartClientService.findByHashCartNumber(hash);
    }

    @GetMapping("/card/{number}")
    public Mono<CartClientModel> findByCartNumber(@PathVariable("number") String number){
        return cartClientService.findByCartNumber(number);
    }

    @GetMapping("/movements/{cartNumber}")
    public Flux<MovementDetailModel> listMovementsByTarjet(@PathVariable("cartNumber") String number){
        return cartClientService.findByCartNumber(number)
                .flux()
                .flatMap(x -> 
                    movementDetailService.findByClient(x.getCodeClient()).take(10)
                ).switchIfEmpty(Flux.empty());
    }
}
