package nttdata.grupouno.com.Clients.services;

import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;
import nttdata.grupouno.com.Clients.models.dto.ClientsNatural;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientsService {
    Flux<ClientsLegal> listAllClientsLegal();

    Flux<ClientsNatural> listAllClientsNatural();
    Mono<Clients> findAllById(String id);
    Mono<Clients> createClient(Clients clients);
    Mono<Clients> updateClient(Clients client,String id);
    Mono<Void> deleteClient(String id);
    Flux<Clients> findByIdTypePerson(Long idTypePerson);

    Mono<Clients> findByIdPerson(String id);
}
