package nttdata.grupouno.com.operations.services.implementation;

import nttdata.grupouno.com.operations.convert.AccountConvert;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.models.dto.AccountDetailDto;
import nttdata.grupouno.com.operations.models.dto.MasterAccountDto;
import nttdata.grupouno.com.operations.repositories.implementation.*;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import nttdata.grupouno.com.operations.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MasterAccountServices implements IMasterAccountServices {

    private static final Logger logger = LogManager.getLogger(MasterAccountServices.class);

    @Autowired
    private MasterAccountRepository accountRepository;
    @Autowired
    private TypeAccountRepository typeAccountRepository;
    @Autowired
    private AccountClientRepositorio accountClientRepositorio;
    @Autowired 
    private CartClientRepositorio cartClientRepositorio;
    @Autowired
    private WebClientApiService webClientApiService;
    @Autowired
    private MovementDetailRepository movementDetailRepository;
    @Autowired
    private AccountConvert accountConvert;


    @Override
    public Mono<MasterAccountModel> createAccount(MasterAccountModel account, AccountClientModel clientModel) {
        account.setId(UUID.randomUUID().toString());
        account.setType(new TypeModel(account.getType().getCode(), null, null, null, null, null, null, null, null,null,null));

        return webClientApiService.findClient(clientModel.getCodeClient()).
            flatMap(x -> {
                if(!x.getId().equals(clientModel.getCodeClient())) return Mono.empty();
                
                return accountRepository.save(account)
                    .flatMap(a -> typeAccountRepository
                        .findById(a.getType().getCode())
                        .map(b -> {
                            a.setType(b);
                            return a;
                        })
                    )
                    .map(a -> {
                        clientModel.setId(UUID.randomUUID().toString());
                        clientModel.setNumberAccount(a.getNumberAccount());
                        clientModel.setStatus("T");
                        clientModel.setTypeAccount(a.getType().getCode());
                        clientModel.setOpeningDate(a.getStartDate());
                        return a;
                    })
                    .flatMap(a -> accountClientRepositorio
                        .save(clientModel)
                        .flatMap(b -> cartClientRepositorio
                            .countByCodeClientAndTypeCartAndCodeStatus(b.getCodeClient(), b.getTypeAccount().substring(0, 3), "A")
                            .flatMap(z -> {
                                if(z.longValue() != 1) return Mono.just(b);
                                
                                return cartClientRepositorio
                                    .findByCodeClientAndTypeCartAndCodeStatus(b.getCodeClient(), b.getTypeAccount().substring(0, 3), "A")
                                    .single()
                                    .flatMap(c -> {
                                        b.setIdCartClient(c.getId());
                                        return accountClientRepositorio.save(b);
                                    })
                                    .switchIfEmpty(Mono.just(b));
                            })
                        ).flatMap(b -> Mono.just(a))
                        .switchIfEmpty(accountRepository.deleteById(a.getId()).flatMap(c ->  Mono.empty()))
                        .onErrorResume(b -> accountRepository.deleteById(a.getId()).flatMap(c -> Mono.empty()))
                    );
            })
            .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<MasterAccountModel> findById(String id) {
        return accountRepository.findById(id)
                .flatMap(c -> typeAccountRepository.findById(c.getType().getCode()).flatMap(x -> {
                    c.setType(x);
                    return Mono.just(c);
                }));
    }

    @Override
    public Flux<MasterAccountModel> findAllAccount() {
        return accountRepository.findAll()
                .flatMap(c -> typeAccountRepository.findById(c.getType().getCode()).flatMap(x -> {
                    c.setType(x);
                    return Mono.just(c);
                }));
    }

    @Override
    public Mono<MasterAccountModel> updateAccount(MasterAccountModel account, String id) {
        //return accountRepository.findById(id).flatMap(c -> accountRepository.save(account));
        return accountRepository.findById(id).flatMap(c -> {
            c.setAmount(account.getAmount());
            c.setStatus(account.getStatus());
            c.setStartDate(account.getStartDate());
            c.setEndDate(account.getEndDate());
            c.setCoinType(account.getCoinType());
            return accountRepository.save(c);
        });
    }

    @Override
    public Mono<Void> deleteBydId(String id) {
        return accountRepository.deleteById(id);
    }

    @Override
    public Flux<MasterAccountModel> findStartDate(String date) {
        return accountRepository.findByStartDate(date);
    }

    @Override
    public Mono<MasterAccountModel> findByAccount(String numberAccount) {
        return accountRepository.findByNumberAccount(numberAccount)
                .flatMap(masterAccountModel -> typeAccountRepository.findById(masterAccountModel.getType().getCode())
                        .flatMap(typeModel -> {
                            masterAccountModel.setType(typeModel);
                            return Mono.just(masterAccountModel);
                        }));
    }

    @Override
    public Flux<MasterAccountModel> findByClient(String codeClient) {
        return accountClientRepositorio.findByCodeClient(codeClient)
                .flatMap(accountClientModel -> findByAccount(accountClientModel.getNumberAccount()));
    }

    @Override
    public Flux<MasterAccountModel> findByStartDateBetween(String from, String to) {
        return accountRepository.findAll()
                .filter(m -> Util.stringToDate(m.getStartDate()).after(Util.addDay(Util.stringToDate(from),-1)))
                .filter(n -> Util.stringToDate(n.getStartDate()).before(Util.addDay(Util.stringToDate(to),1)));
    }

    @Override
    public Flux<AccountDetailDto> findByStartDateBetweenDetail(String from, String to) {
        return findByStartDateBetween(from, to)
                .flatMap(masterAccountModel -> {
                    AccountDetailDto detailDto = accountConvert.accountToDetail(masterAccountModel);
                    return Flux.just(detailDto)
                            .flatMap(accountDetailDto -> movementDetailRepository.findByNumberAccount(accountDetailDto.getNumberAccount())
                                    .collectList().flux().flatMap(movementDetailModels -> {
                                        List<MovementDetailModel> movementDetails = movementDetailModels;
                                        accountDetailDto.setMovements(movementDetails);
                                        return Flux.just(accountDetailDto);
                                    }));
                });
    }

    @Override
    public Mono<MasterAccountDto> validAccountBalance(String numberAccount, Double amount, Character movementType) {
        logger.debug("Cuenta " + numberAccount);
        MasterAccountDto masterAccountDto = new MasterAccountDto();
        Date today = new Date();

        // validamos si superó la cantidad de transacciones para considerar la comisión
        return movementDetailRepository.countByNumberAccountAndMovementTypeAndMonthAndYear(numberAccount,'D', Util.getMonth(today),Util.getYear(today)).flatMap(x -> {

            return movementDetailRepository.countByNumberAccountAndMovementTypeAndMonthAndYear(numberAccount,'R',Util.getMonth(today),Util.getYear(today))
                    .flatMap(y -> {
                        Integer total = y.intValue() + x.intValue();
                        logger.debug("total movimientos " + total);

                        return findByAccount(numberAccount).flatMap(
                                z -> {
                                    Double commision = 0.0;
                                    logger.debug("Monto cuenta " + z.getAmount());
                                    if(z.getType().getCountLimitOperation()<=total){
                                        commision =  z.getType().getAmountCommission();
                                    }
                                    if (z.getAmount() - commision >=amount){
                                        z.setAmount(movementType=='R'?(z.getAmount()-amount-commision) : (z.getAmount() + amount - commision));
                                        logger.debug("el saldo nuevo sería " + z.getAmount());
                                        masterAccountDto.setBlSaldo(true);
                                    }
                                    else{
                                        masterAccountDto.setBlSaldo(false);
                                    }
                                    masterAccountDto.setAccountModel(z);
                                    return Mono.just(masterAccountDto);
                                });

                    });
        });


    }
}
