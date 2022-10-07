package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.dto.AccountClientDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountClientService {
    Flux<AccountClientModel> findByCodeClient(String codeClient);
    Flux<AccountClientModel> findByNumBerAccount(String numberAccount);
    Mono<AccountClientModel> registerClient(AccountClientModel model);
    Flux<AccountClientModel> findByClientTypeAccount(String codeClient, String typeAccount);
    Flux<AccountClientModel> findAll();
    Mono<Long> countByCodeClientAndTypeAccount(String codeClient, String typeAccount);
    Mono<Long> countByCodeClientAndTypeAccountAndTypeClient(String codeCliente, String typeAccountm, String typeClient);
    Mono<AccountClientModel> findById(String id);
    Mono<Long> countByCodeClientAndTypeAccountLike(String codeClient, String typeAccount);
    Mono<AccountClientModel> assignPrincipalAccount(AccountClientDto model);
    Flux<AccountClientModel> findByidCartClient(String idCartClient);
    Mono<Long> validCreditAccountUntilToday(String codeClient, String typeAccount, String typeClient);
}
