package nttdata.grupouno.com.Clients.services.implementation.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import nttdata.grupouno.com.Clients.convert.ClientsConvert;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.LegalPerson;
import nttdata.grupouno.com.Clients.models.MasterAccount;
import nttdata.grupouno.com.Clients.models.MovementDetail;
import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;
import nttdata.grupouno.com.Clients.repositories.LegalPersonRepository;
import nttdata.grupouno.com.Clients.services.ClientsService;
import nttdata.grupouno.com.Clients.services.dto.ClientsLegalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ClientsLegalServiceImpl implements ClientsLegalService {

    @Autowired
    private LegalPersonRepository legalPersonRepository;

    @Autowired
    private ClientsConvert clientsConvert;

    @Autowired
    private ClientsService clientsService;

    private final WebClient webClient;

    public ClientsLegalServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8010").build();
    }

    @Override
    public Flux<LegalPerson> listAllLegalPerson() {
        return null;
    }

    @Override
    public Mono<ClientsLegal> findAllById(String id) {
        return legalPersonRepository.findById(id).flatMap(legal -> {
            ClientsLegal dto = clientsConvert.convertDtoLegal(legal);
            Mono<Clients> clientsMono = clientsService.findByIdPerson(dto.getIdPerson());

            return clientsMono.flatMap(x -> {
                dto.setId(x.getId());
                dto.setIdTypePerson(x.getIdTypePerson());
                dto.setIdPerson(x.getIdPerson());
                return Mono.just(dto);
            });
        });
    }

    @Override
    public Mono<LegalPerson> createLegalPerson(LegalPerson legalPerson) {
        return null;
    }

    @Override
    public Mono<LegalPerson> updateLegalPerson(LegalPerson legalPerson, String id) {
        return null;
    }

    @Override
    public Mono<Void> deleteLegalPerson(String id) {
        return null;
    }

    @Override
    public Mono<ClientsLegal> findByRuc(Long ruc) {
        return legalPersonRepository.findByRuc(ruc).flatMap(legal -> {
            ClientsLegal dto = clientsConvert.convertDtoLegal(legal);
            Mono<Clients> clientsMono = clientsService.findByIdPerson(dto.getIdPerson());

            return clientsMono.flatMap(x -> {
                dto.setId(x.getId());
                dto.setIdTypePerson(x.getIdTypePerson());
                dto.setIdPerson(x.getIdPerson());
                return Mono.just(dto);
            });
        });
    }

    @Override
    public Flux<ClientsLegal> findByBusinessName(String businessName) {
        return legalPersonRepository.findByBusinessName(businessName).flatMap(legal -> {
            ClientsLegal dto = clientsConvert.convertDtoLegal(legal);
            Mono<Clients> clientsMono = clientsService.findByIdPerson(dto.getIdPerson());

            return clientsMono.flatMap(x -> {
                dto.setId(x.getId());
                dto.setIdTypePerson(x.getIdTypePerson());
                dto.setIdPerson(x.getIdPerson());
                return Mono.just(dto);
            });
        });
    }

    @Override
    public Flux<MasterAccount> findAccountByRuc(Long ruc) {
        return findByRuc(ruc).flux().flatMap(clientsLegal  -> this.webClient.get()
                .uri("/api/account/client/{codeClient}",clientsLegal.getId())
                .retrieve().bodyToFlux(MasterAccount.class));
    }

    @Override
    public Flux<MovementDetail> findMovementByRuc(Long ruc) {
        return findByRuc(ruc).flux().flatMap(clientsLegal  -> this.webClient.get()
                .uri("/api/movement/client/{codeClient}",clientsLegal.getId())
                .retrieve().bodyToFlux(MovementDetail.class));
    }
}
