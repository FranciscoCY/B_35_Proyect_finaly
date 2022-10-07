package nttdata.grupouno.com.Clients.services;

import nttdata.grupouno.com.Clients.models.NaturalPerson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NaturalPersonService  {

    Flux<NaturalPerson> listAllNaturalPerson();
    Mono<NaturalPerson> findAllById(String id);
    Mono<NaturalPerson> createNaturalPerson(NaturalPerson naturalPerson);
    Mono<NaturalPerson> updateNaturalPerson(NaturalPerson naturalPerson);
    Mono<Void> deleteNaturalPerson(String id);
    Mono<NaturalPerson> findByDocumentNumber(Long documentNumber);
    Flux<NaturalPerson> findByNames(String names);
    Flux<NaturalPerson> findByLastNames(String lastNames);
}
