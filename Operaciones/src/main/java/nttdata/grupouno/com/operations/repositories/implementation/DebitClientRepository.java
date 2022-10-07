package nttdata.grupouno.com.operations.repositories.implementation;

import nttdata.grupouno.com.operations.models.DebtClientModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface DebitClientRepository extends ReactiveMongoRepository<DebtClientModel, String> {

    Flux<DebtClientModel> findByNumberAccountAndState(String numberAccount, String state);
}
