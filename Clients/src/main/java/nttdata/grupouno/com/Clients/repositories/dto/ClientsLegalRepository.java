package nttdata.grupouno.com.Clients.repositories.dto;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import nttdata.grupouno.com.Clients.models.dto.ClientsLegal;

public interface ClientsLegalRepository  extends ReactiveMongoRepository<ClientsLegal,String> {
}
