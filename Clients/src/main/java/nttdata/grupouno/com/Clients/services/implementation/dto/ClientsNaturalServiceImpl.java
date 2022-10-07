package nttdata.grupouno.com.Clients.services.implementation.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import nttdata.grupouno.com.Clients.convert.ClientsConvert;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.MasterAccount;
import nttdata.grupouno.com.Clients.models.MovementDetail;
import nttdata.grupouno.com.Clients.models.dto.NaturalClients;
import nttdata.grupouno.com.Clients.repositories.NaturalPersonRepository;
import nttdata.grupouno.com.Clients.services.ClientsService;
import nttdata.grupouno.com.Clients.services.dto.ClientsNaturalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientsNaturalServiceImpl implements ClientsNaturalService {

    @Autowired
    private NaturalPersonRepository naturalPersonRepository;

    @Autowired
    private ClientsConvert clientsConvert;

    @Autowired
    private ClientsService clientsService;

    private final WebClient webClient;

    public ClientsNaturalServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8010")
                .build();
    }

    @Override
    public Mono<NaturalClients> findAllById(String id) {
        return naturalPersonRepository.findById(id)
                .flatMap(naturalPerson -> {
            NaturalClients naturalClients = clientsConvert.convertNaturalClient(naturalPerson);

            Mono<Clients> clientsMono =  clientsService.findByIdPerson(naturalClients.getIdPerson());

            return clientsMono.flatMap(clients -> {
                naturalClients.setId(clients.getId());
                naturalClients.setIdTypePerson(clients.getIdTypePerson());
                naturalClients.setIdPerson(clients.getIdPerson());
                return Mono.just(naturalClients);
            });
        });
    }

    @Override
    public Mono<NaturalClients> findByDocumentNumber(Long documentNumber) {
        return naturalPersonRepository.findByDocumentNumber(documentNumber)
                .flatMap(naturalPerson -> {
            NaturalClients naturalClients = clientsConvert.convertNaturalClient(naturalPerson);

            Mono<Clients> clientsMono = clientsService.findByIdPerson(naturalClients.getIdPerson());

            return clientsMono.flatMap(clients -> {
                naturalClients.setId(clients.getId());
                naturalClients.setIdTypePerson(clients.getIdTypePerson());
                naturalClients.setIdPerson(clients.getIdPerson());
                return Mono.just(naturalClients);
            });
        });
    }

    @Override
    public Flux<NaturalClients> findByNames(String names) {
        return naturalPersonRepository.findByNames(names)
                .flatMap(naturalPerson -> {
            NaturalClients naturalClients = clientsConvert.convertNaturalClient(naturalPerson);

            Mono<Clients> clientsMono = clientsService.findByIdPerson(naturalClients.getIdPerson());

            return clientsMono.flatMap(clients -> {
                naturalClients.setId(clients.getId());
                naturalClients.setIdTypePerson(clients.getIdTypePerson());
                naturalClients.setIdPerson(clients.getIdPerson());
                return Mono.just(naturalClients);
            });
        });
    }

    @Override
    public Flux<NaturalClients> findByLastNames(String lastNames) {
        return naturalPersonRepository.findByLastNames(lastNames)
                .flatMap(naturalPerson -> {
            NaturalClients naturalClients = clientsConvert.convertNaturalClient(naturalPerson);

            Mono<Clients> clientsMono = clientsService.findByIdPerson(naturalClients.getIdPerson());

            return clientsMono.flatMap(clients -> {
                naturalClients.setId(clients.getId());
                naturalClients.setIdTypePerson(clients.getIdTypePerson());
                naturalClients.setIdPerson(clients.getIdPerson());
                return Mono.just(naturalClients);
            });
        });
    }

    @Override
    public Flux<MasterAccount> findAccountByDocumentNumber(Long documentNumber) {
        return findByDocumentNumber(documentNumber).flux()
                .flatMap(naturalClients -> this.webClient.get()
                .uri("/operation/account/client/{codeClient}", naturalClients.getId())
                .retrieve().bodyToFlux(MasterAccount.class));
    }

    @Override
    public Flux<MovementDetail> findMovementByDocumentNumber(Long documentNumber) {
        return findByDocumentNumber(documentNumber).flux()
                .flatMap(naturalClients -> this.webClient.get()
                .uri("/operation/movement/client/{codeClient}",
                        naturalClients.getId())
                .retrieve().bodyToFlux(MovementDetail.class));
    }
}
