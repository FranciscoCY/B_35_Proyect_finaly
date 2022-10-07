package nttdata.grupouno.com.Clients.services.dto;

import nttdata.grupouno.com.Clients.models.LegalPerson;
import nttdata.grupouno.com.Clients.models.MasterAccount;
import nttdata.grupouno.com.Clients.models.MovementDetail;
import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientsLegalService {

    Flux<LegalPerson> listAllLegalPerson();
    Mono<ClientsLegal> findAllById(String id);
    Mono<LegalPerson> createLegalPerson(LegalPerson legalPerson);
    Mono<LegalPerson> updateLegalPerson(LegalPerson legalPerson,String id);
    Mono<Void> deleteLegalPerson(String id);
    Mono<ClientsLegal> findByRuc(Long ruc);
    Flux<ClientsLegal> findByBusinessName(String businessName);
    Flux<MasterAccount> findAccountByRuc(Long ruc);
    Flux<MovementDetail> findMovementByRuc(Long ruc);
}
