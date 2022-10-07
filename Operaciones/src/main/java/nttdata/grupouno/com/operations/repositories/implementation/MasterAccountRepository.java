package nttdata.grupouno.com.operations.repositories.implementation;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MasterAccountRepository extends ReactiveMongoRepository<MasterAccountModel, String> {
    Flux<MasterAccountModel> findByStartDate(String startDate);
    Mono<MasterAccountModel> findByNumberAccount(String numberAccount);
}
