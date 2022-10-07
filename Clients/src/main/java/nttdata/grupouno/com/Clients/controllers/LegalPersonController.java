package nttdata.grupouno.com.Clients.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import nttdata.grupouno.com.Clients.convert.ClientsConvert;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.LegalPerson;
import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;
import nttdata.grupouno.com.Clients.services.ClientsService;
import nttdata.grupouno.com.Clients.services.LegalPersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/legalPerson")
public class LegalPersonController {

    @Autowired
    private LegalPersonService legalPersonService;

    @Autowired
    private ClientsService clientsService;

    @Autowired
    private ClientsConvert clientsConvert;

    @GetMapping
    public Flux<LegalPerson> findAll() {
        return legalPersonService.listAllLegalPerson();
    }

    @GetMapping("/{id}")
    public Mono<LegalPerson> findAllById(@PathVariable final String id) {
        return legalPersonService.findAllById(id);
    }

    @GetMapping("/ruc/{ruc}")
    public Mono<LegalPerson> findAllByRuc(@PathVariable final Long ruc) {
        return legalPersonService.findByRuc(ruc);
    }


    @GetMapping("/businessName/{name}")
    public Flux<LegalPerson> findAllByBusinessName(@PathVariable final String name) {
        return legalPersonService.findByBusinessName(name);
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> addLegalPerson(@Valid @RequestBody final Mono<LegalPerson> legalPersonMono) {
        Map<String, Object> respuesta = new HashMap<>();
        return legalPersonMono.flatMap(legalPerson -> {
            return legalPersonService.findByRuc(legalPerson.getRuc()).flatMap(a -> {
                respuesta.put("Persona Juridica Duplicada", a);
                return Mono.just(ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(respuesta));
            }).switchIfEmpty(
                    legalPersonService.createLegalPerson(legalPerson).flatMap(s -> {
                        Clients clients = new Clients();
                        clients.setIdTypePerson(2L);
                        clients.setIdPerson(s.getId());
                        return clientsService.createClient(clients).map(c -> {
                            List<LegalPerson> list=new ArrayList<>();
                            ClientsLegal dto=clientsConvert.convertLegalDTO(c);
                            list.add(s);
                            dto.setLegalPersonList(list);

                            respuesta.put("Cliente Juridico Creado", dto);
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

    @PutMapping("/{id}")
    public Mono<ResponseEntity<LegalPerson>> updateLegalPersona(@Valid @RequestBody final LegalPerson legalPerson, @PathVariable final String id) {
        return legalPersonService.updateLegalPerson(legalPerson, id)
                .map(l -> ResponseEntity.created(
                                URI.create("/api/legalPerson/".concat(l.getId())))
                        .contentType(MediaType.APPLICATION_JSON).body(l))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLegalPerson(@PathVariable final String id) {
        return legalPersonService.findAllById(id).flatMap(l -> {
            return legalPersonService.deleteLegalPerson(l.getId())
                    .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
