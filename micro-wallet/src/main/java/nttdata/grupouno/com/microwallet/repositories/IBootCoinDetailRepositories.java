package nttdata.grupouno.com.microwallet.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import nttdata.grupouno.com.microwallet.models.BootCoinDetailModel;
import reactor.core.publisher.Flux;

@Repository
public interface IBootCoinDetailRepositories extends ReactiveMongoRepository<BootCoinDetailModel, String> {
    Flux<BootCoinDetailModel> findByCodeWalletRequestAndStatus(String walletRequest, String status);
    Flux<BootCoinDetailModel> findByCodeWalletResponseAndStatus(String walletResponse, String status);
}
