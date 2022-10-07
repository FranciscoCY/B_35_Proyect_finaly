package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMovementDetailService {

    void createMovement(MovementDetailModel movement);
    Mono<MovementDetailModel> findById(Integer id);
    Flux<MovementDetailModel> findAllMovements();

    Flux<MovementDetailModel> findByAccount(String account);
    Flux<MovementDetailModel> findByClient(String codeClient);

    Mono<MasterAccountModel> checkBalance(String id);

    Mono<MasterAccountModel> depositAmount(String id, Double amount);

    Mono<MasterAccountModel> withdrawAmount(String id, Double amount);

    Mono<Integer> countByAccountMonthYear(String numberAccount, String month, String year);

    Mono<MasterAccountModel> chargeMaintenace(String numberAccount);

}
