package nttdata.grupouno.com.Clients.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import nttdata.grupouno.com.Clients.models.LegalPerson;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LegalPersonRepository extends ReactiveMongoRepository<LegalPerson,String> {

    Mono<LegalPerson> findByRuc(Long ruc);
    Flux<LegalPerson> findByBusinessName(String businessName);
}
