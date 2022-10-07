package nttdata.grupouno.com.microwallet.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nttdata.grupouno.com.microwallet.models.TypeChangeModel;
import reactor.core.publisher.Mono;

@Repository
public interface ITypeChangeRepositories extends ReactiveMongoRepository<TypeChangeModel, String>{
    Mono<TypeChangeModel> findByDateAndTypeCoin(String date, String typeCoin);
}
