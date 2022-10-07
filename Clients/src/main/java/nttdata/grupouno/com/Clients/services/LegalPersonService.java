package nttdata.grupouno.com.Clients.services;

import nttdata.grupouno.com.Clients.models.LegalPerson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LegalPersonService {

    Flux<LegalPerson> listAllLegalPerson();
    Mono<LegalPerson> findAllById(String id);
    Mono<LegalPerson> createLegalPerson(LegalPerson legalPerson);
    Mono<LegalPerson> updateLegalPerson(LegalPerson legalPerson,String id);
    Mono<Void> deleteLegalPerson(String id);
    Mono<LegalPerson> findByRuc(Long ruc);
    Flux<LegalPerson> findByBusinessName(String businessName);
}
