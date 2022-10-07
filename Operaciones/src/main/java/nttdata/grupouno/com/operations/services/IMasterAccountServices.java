package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.dto.AccountDetailDto;
import nttdata.grupouno.com.operations.models.dto.MasterAccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMasterAccountServices {
    Mono<MasterAccountModel> createAccount(MasterAccountModel account, AccountClientModel clientModel);
    Mono<MasterAccountModel> findById(String id);
    Flux<MasterAccountModel> findAllAccount();
    Mono<MasterAccountModel> updateAccount(MasterAccountModel account, String id);
    Mono<Void> deleteBydId(String id);
    Flux<MasterAccountModel> findStartDate(String date);
    Mono<MasterAccountModel> findByAccount(String numberAccount);
    Flux<MasterAccountModel> findByClient(String codeClient);
    Flux<MasterAccountModel> findByStartDateBetween(String from, String to);
    Flux<AccountDetailDto> findByStartDateBetweenDetail(String from, String to);
    Mono<MasterAccountDto> validAccountBalance(String numberAccount, Double amount, Character movementType);
}
