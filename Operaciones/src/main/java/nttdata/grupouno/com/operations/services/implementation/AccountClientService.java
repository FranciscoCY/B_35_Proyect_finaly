package nttdata.grupouno.com.operations.services.implementation;

import java.util.Date;
import java.util.UUID;

import nttdata.grupouno.com.operations.models.dto.AccountClientDto;
import nttdata.grupouno.com.operations.repositories.implementation.CartClientRepositorio;
import nttdata.grupouno.com.operations.repositories.implementation.MasterAccountRepository;
import nttdata.grupouno.com.operations.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.repositories.implementation.AccountClientRepositorio;
import nttdata.grupouno.com.operations.services.IAccountClientService;
import nttdata.grupouno.com.operations.services.IWebClientApiService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountClientService implements IAccountClientService {
    private static final Logger logger = LogManager.getLogger(AccountClientService.class);

    @Autowired
    private AccountClientRepositorio accountClientRepositorio;
    @Autowired
    private IWebClientApiService webClient;
    @Autowired
    private CartClientRepositorio cartClientRepositorio;
    @Autowired
    private MasterAccountRepository accountRepository;

    @Override
    public Flux<AccountClientModel> findByCodeClient(String codeClient) {
        return accountClientRepositorio.findByCodeClient(codeClient);
    }

    @Override
    public Flux<AccountClientModel> findByNumBerAccount(String numberAccount) {
        return accountClientRepositorio.findByNumberAccount(numberAccount);
    }

    @Override
    public Mono<AccountClientModel> registerClient(AccountClientModel model) {
        model.setId(UUID.randomUUID().toString());
        return this.webClient.findClient(model.getCodeClient())
            .flatMap(x -> {
                if(!x.getId().equals(model.getCodeClient()))
                    return Mono.empty();
                return accountClientRepositorio.save(model);
        }).onErrorResume(y -> Mono.empty());
    }

    @Override
    public Flux<AccountClientModel> findByClientTypeAccount(String codeCliente, String typeAccount) {
        return accountClientRepositorio.findByNumberAccountAndTypeAccount(codeCliente, typeAccount);
    }

    @Override
    public Flux<AccountClientModel> findAll() {
        return accountClientRepositorio.findAll();
    }

    @Override
    public Mono<Long> countByCodeClientAndTypeAccount(String codeClient, String typeAccount) {
        return accountClientRepositorio.countByCodeClientAndTypeAccount(codeClient, typeAccount);
    }

    @Override
    public Mono<AccountClientModel> findById(String id){
        return accountClientRepositorio.findById(id);
    }

    @Override
    public Mono<Long> countByCodeClientAndTypeAccountAndTypeClient(String codeCliente, String typeAccount, String typeClient) {
        return accountClientRepositorio.countByCodeClientAndTypeAccountAndTypeClient(codeCliente, typeAccount, typeClient);
    }

    @Override
    public Mono<Long> countByCodeClientAndTypeAccountLike(String codeClient, String typeAccount) {
        if(typeAccount == null || typeAccount.isBlank())
            return Mono.just(Long.valueOf("1"));
        return accountClientRepositorio.countByCodeClientAndTypeAccountLike(codeClient, typeAccount);
    }

    @Override
    public Mono<AccountClientModel> assignPrincipalAccount(AccountClientDto model) {
        return cartClientRepositorio.findById(model.getIdCartClient()).flatMap(a -> {
            //Las tarjetas de debito se pueden a sociar a diferentes cuentas y una de ellas ser principal
            if("AHO".equals(a.getTypeCart())) {
                return accountClientRepositorio.findByIdCartClientAndNumberAccount(model.getIdCartClient(), model.getNumberAccount()).flatMap(b -> {
                            b.setPrincipalAccount("S");
                            return accountClientRepositorio.findByIdCartClient(model.getIdCartClient() )
                                    .flatMap(c -> {
                                        c.setPrincipalAccount("N");
                                        return accountClientRepositorio.save(c).flatMap(d -> accountClientRepositorio.save(b));
                                    })
                                    .switchIfEmpty(accountClientRepositorio.save(b))
                                    .single()
                                    .flatMap(e -> Mono.just(b));
                        }) .switchIfEmpty(
                                accountClientRepositorio.findByCodeClientAndNumberAccount(model.getCodeClient(), model.getNumberAccount())
                        )
                        .single()
                        .flatMap(f -> {
                            f.setIdCartClient(model.getIdCartClient());
                            f.setPrincipalAccount("S");

                            return accountClientRepositorio.findByIdCartClient(model.getIdCartClient() )
                                    .flatMap(g -> {
                                        g.setPrincipalAccount("N");
                                        return accountClientRepositorio.save(g).flatMap(h -> accountClientRepositorio.save(f));
                                    }).single()
                                    .switchIfEmpty(Mono.empty());
                        });
            }
            else{
                logger.debug("No es una tarjeta de debito");
                return Mono.empty();
            }
        }).switchIfEmpty(Mono.empty());

    }

    @Override
    public Flux<AccountClientModel> findByidCartClient(String idCartClient) {
        return accountClientRepositorio.findByidCartClient(idCartClient);
    }

    @Override
    public Mono<Long> validCreditAccountUntilToday(String codeClient, String typeAccount, String typeClient) {
        Date today = new Date();
        return accountClientRepositorio.findByCodeClientAndTypeAccountAndTypeClient(codeClient,typeAccount,typeClient)
                .flatMap(a -> accountRepository.findByNumberAccount(a.getNumberAccount()))
                        .filter(b-> Util.stringToDate(b.getStartDate()).compareTo(today) != 1).count()
                .flatMap(aLong -> {
                    if (aLong.intValue() == 0){
                       return Mono.just(0L);
                    }else{
                        return Mono.just(1L);
                    }
                }).defaultIfEmpty(0L);
    }
}
