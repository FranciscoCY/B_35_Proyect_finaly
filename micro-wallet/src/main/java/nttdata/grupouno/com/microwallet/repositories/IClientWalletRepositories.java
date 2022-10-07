package nttdata.grupouno.com.microwallet.repositories;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IClientWalletRepositories extends ReactiveMongoRepository<ClientWalletModel, String> {
    Mono<ClientWalletModel> findByNumberDocumentAndTypeDocument(String number, String type);
    Mono<ClientWalletModel> findByNumberPhone(String numberPhone);
}
