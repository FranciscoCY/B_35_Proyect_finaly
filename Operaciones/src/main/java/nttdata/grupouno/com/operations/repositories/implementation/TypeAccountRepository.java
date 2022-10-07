package nttdata.grupouno.com.operations.repositories.implementation;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nttdata.grupouno.com.operations.models.TypeModel;

@Repository
public interface TypeAccountRepository extends ReactiveMongoRepository<TypeModel, String> {
    
}
