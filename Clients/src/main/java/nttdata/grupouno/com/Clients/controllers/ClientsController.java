package nttdata.grupouno.com.Clients.controllers;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.MasterAccount;
import nttdata.grupouno.com.Clients.models.MovementDetail;
import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;
import nttdata.grupouno.com.Clients.models.dto.ClientsNatural;
import nttdata.grupouno.com.Clients.models.dto.NaturalClients;
import nttdata.grupouno.com.Clients.services.ClientsService;
import nttdata.grupouno.com.Clients.services.dto.ClientsLegalService;
import nttdata.grupouno.com.Clients.services.dto.ClientsNaturalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientsController {

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private ClientsLegalService clientsLegalService;

    @Autowired
    private ClientsNaturalService clientsNaturalService;

    @GetMapping("/legal")
    public Flux<ClientsLegal> findAllLegal() {
        return clientsService.listAllClientsLegal();
    }

    @GetMapping("/legal/{id}")
    public Mono<ClientsLegal> findAllByIdLegal(@PathVariable final String id) {
        return clientsLegalService.findAllById(id);
    }

    @GetMapping("/legal/ruc/{ruc}")
    public Mono<ClientsLegal> findAllByRuc(@PathVariable final Long ruc){
        return clientsLegalService.findByRuc(ruc);
    }

    @GetMapping("/legal/businessName/{businessName}")
    public Flux<ClientsLegal> findAllByBusinessName(@PathVariable final String businessName){
        return clientsLegalService.findByBusinessName(businessName);
    }

    @GetMapping("/natural")
    public Flux<ClientsNatural> findAllNatural(){
        return clientsService.listAllClientsNatural();
    }

    @GetMapping("/natural/{id}")
    public Mono<NaturalClients> findAllByIdNatural(@PathVariable final String id){
        return clientsNaturalService.findAllById(id);
    }

    @GetMapping("/natural/documentNumber/{documentNumber}")
    public Mono<NaturalClients> findAllByDocumentNumberNatural(@PathVariable final Long documentNumber){
        return clientsNaturalService.findByDocumentNumber(documentNumber);
    }

    @GetMapping("/natural/names/{names}")
    public Flux<NaturalClients> findAllByNamesNatural(@PathVariable final String names){
        return clientsNaturalService.findByNames(names);
    }

    @GetMapping("/natural/lastNames/{lastNames}")
    public Flux<NaturalClients> findAllByLastNamesNatural(@PathVariable final String lastNames){
        return clientsNaturalService.findByLastNames(lastNames);
    }

    @GetMapping("/{id}")
    public Mono<Clients> findAllById(@PathVariable final String id){
        return clientsService.findAllById(id);
    }

    @GetMapping("/findByIdTypePerson/{idTypePerson}")
    public Flux<Clients> findByIdTypePerson(@PathVariable(value ="idTypePerson" ) final Long idTypePerson){
        return clientsService.findByIdTypePerson(idTypePerson);
    }

    @GetMapping("/findByIdPerson/{idPerson}")
    public Mono<Clients> findByIdPerson(@PathVariable(value ="idPerson" ) final String idPerson){
        return clientsService.findByIdPerson(idPerson);
    }


    @PostMapping
    public Mono<ResponseEntity<Map<String,Object>>> addClient(@Valid @RequestBody final Mono<Clients> clientsMono){

        Map<String,Object> respuesta=new HashMap<>();
        return  clientsMono.flatMap(clients -> 
            clientsService.createClient(clients).map(s ->{
                respuesta.put("client",s);
                return  ResponseEntity.created(URI.create("/api/clients"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(respuesta);
            })
        ).onErrorResume(ex->
            Mono.just(ex).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(Flux::fromIterable).map(DefaultMessageSourceResolvable::getDefaultMessage).collectList()
                    .flatMap(list -> {
                        respuesta.put("errors", list);
                        return Mono.just(ResponseEntity.badRequest().body(respuesta));
                    })
        );
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Clients>> updateClient(@Valid @RequestBody final Clients client,@PathVariable final String id){
        return clientsService.updateClient(client,id)
                .map(c -> ResponseEntity.created(
                        URI.create("/api/clients/".concat(c.getId())))
                                .contentType(MediaType.APPLICATION_JSON).body(c))
                        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable final String id){
        return clientsService.findAllById(id).flatMap(c ->
            clientsService.deleteClient(c.getId())
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
        ).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping("/natural/movement/{documentNumber}")
    @CircuitBreaker(name="operation", fallbackMethod = "fallBackfindMovementByIdNatural")
    public Flux<MovementDetail> findMovementByIdNatural(@PathVariable final Long documentNumber){
        return clientsNaturalService.findMovementByDocumentNumber(documentNumber);
    }

    @CircuitBreaker(name="operation", fallbackMethod = "fallBackfindMovementByIdLegal")
    @GetMapping("/legal/movement/{ruc}")
    public Flux<MovementDetail> findMovementByIdLegal(@PathVariable final Long ruc){
        return clientsLegalService.findMovementByRuc(ruc);
    }
    @CircuitBreaker(name="operation", fallbackMethod = "fallBackfindAccountByIdNatural")
    @GetMapping("/natural/account/{documentNumber}")
    public Flux<MasterAccount> findAccountByIdNatural(@PathVariable final Long documentNumber){
        return clientsNaturalService.findAccountByDocumentNumber(documentNumber);
    }

    @CircuitBreaker(name="operation", fallbackMethod = "fallBackfindAccountByIdLegal")
    @GetMapping("/legal/account/{ruc}")
    public Flux<MasterAccount> findAccountByIdLegal(@PathVariable final Long ruc){
        return clientsLegalService.findAccountByRuc(ruc);
    }

    public Flux<MovementDetail> fallBackfindMovementByIdNatural(RuntimeException ex){
        MovementDetail details= new MovementDetail();
        details.setCurrency("El microservicio de movimiento no esta disponible. fallBackfindMovementByIdNatural".concat(ex.getMessage()));
        return Flux.just(details);
    }

    public ResponseEntity<String> fallBackfindMovementByIdLegal(@PathVariable final Long ruc, RuntimeException ex){
        return new ResponseEntity<>("El microservicio de movimiento no esta disponible. fallBackfindMovementByIdLegal".concat(ex.getMessage()) + ruc,HttpStatus.OK);
    }

    public ResponseEntity<String> fallBackfindAccountByIdNatural(@PathVariable final Long ruc, RuntimeException ex){
        return  new ResponseEntity<>("El microservicio de movimiento no esta disponible. fallBackfindAccountByIdNatural".concat(ex.getMessage()) + ruc,HttpStatus.OK);

    }

    public ResponseEntity<String> fallBackfindAccountByIdLegal(@PathVariable final Long ruc, RuntimeException ex){
        return  new ResponseEntity<>("El microservicio de movimiento no esta disponible. fallBackfindAccountByIdLegal ".concat(ex.getMessage()) + ruc,HttpStatus.OK);

    }
}
