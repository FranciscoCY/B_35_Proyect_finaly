package nttdata.grupouno.com.microwallet.repositories;

import nttdata.grupouno.com.microwallet.models.WalletMovementModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWalletMovementRepository extends ReactiveMongoRepository<WalletMovementModel, String> {
    
}
