package nttdata.grupouno.com.Clients.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import nttdata.grupouno.com.Clients.convert.NaturalClientsConvert;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.NaturalPerson;
import nttdata.grupouno.com.Clients.models.dto.NaturalClients;
import nttdata.grupouno.com.Clients.services.NaturalPersonService;
import nttdata.grupouno.com.Clients.services.implementation.ClientServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/naturalPerson")
public class NaturalPersonController {

    @Autowired
    NaturalPersonService personService;
    @Autowired
    private ClientServiceImpl clientService;
    @Autowired
    private NaturalClientsConvert naturalClientsConvert;

    @GetMapping
    public Flux<NaturalPerson> findAll() {
        return personService.listAllNaturalPerson();
    }

    @GetMapping("/{id}")
    public Mono<NaturalPerson> findAllById(@PathVariable String id) {
        return personService.findAllById(id).flatMap(Mono::just);
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> addNaturalPerson(@Valid @RequestBody final Mono<NaturalPerson> person) {

        Map<String, Object> respuesta = new HashMap<>();
        return person.flatMap(p -> {

            return personService.findByDocumentNumber(p.getDocumentNumber()).flatMap(a -> {
                respuesta.put("Persona Natural Duplicada", a);
                return Mono.just(ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(respuesta));
            }).switchIfEmpty(
                    personService.createNaturalPerson(p).flatMap(s -> {
                        Clients clients = new Clients();
                        clients.setIdTypePerson(1L);
                        clients.setIdPerson(s.getId());
                        return clientService.createClient(clients).map(c -> {
                            NaturalClients naturalClients = naturalClientsConvert.convertNaturalClient(c);
                            naturalClients.setPerson(s);
                            respuesta.put("Persona Natural Creada", naturalClients);
                            return ResponseEntity.created(URI.create("/api/clients"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(respuesta);
                        });
                    })
            );
        }).onErrorResume(ex -> {
            return Mono.just(ex).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(Flux::fromIterable).map(fieldError ->
                            fieldError.getDefaultMessage()).collectList()
                    .flatMap(list -> {
                        respuesta.put("errors", list);
                        return Mono.just(ResponseEntity.badRequest().body(respuesta));
                    });
        });
    }

    @PutMapping
    public Mono<ResponseEntity<NaturalPerson>> updatePersona(@RequestBody NaturalPerson persona) {
        return personService.updateNaturalPerson(persona)
                .map(person -> ResponseEntity.created(
                                URI.create("/api/naturalPerson"))
                        .contentType(MediaType.APPLICATION_JSON).body(person))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePersona(@PathVariable("id") String id) {
        return personService.findAllById(id).flatMap(person -> {
            return personService.deleteNaturalPerson(person.getId())
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/findByDocumentNumber/{documentNumber}")
    public Mono<NaturalPerson> findByDocumentNumber(@PathVariable Long documentNumber) {
        return personService.findByDocumentNumber(documentNumber).flatMap(Mono::just);
    }

    @GetMapping("/findByNames/{names}")
    public Flux<NaturalPerson> findByNames(@PathVariable String names) {
        return personService.findByNames(names).flatMap(Mono::just);
    }

    @GetMapping("/findByLastNames/{lastNames}")
    public Flux<NaturalPerson> findByLastNames(@PathVariable String lastNames) {
        return personService.findByLastNames(lastNames).flatMap(Mono::just);
    }
}
