package nttdata.grupouno.com.operations.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import nttdata.grupouno.com.operations.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.CartClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.models.dto.AccountDetailDto;
import nttdata.grupouno.com.operations.convert.AccountConvert;
import nttdata.grupouno.com.operations.repositories.implementation.AccountClientRepositorio;
import nttdata.grupouno.com.operations.repositories.implementation.CartClientRepositorio;
import nttdata.grupouno.com.operations.repositories.implementation.MasterAccountRepository;
import nttdata.grupouno.com.operations.repositories.implementation.TypeAccountRepository;
import nttdata.grupouno.com.operations.repositories.implementation.MovementDetailRepository;
import nttdata.grupouno.com.operations.services.implementation.MasterAccountServices;
import nttdata.grupouno.com.operations.services.implementation.WebClientApiService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class MasterAccountServicesTest {
    @InjectMocks
    private MasterAccountServices masterAccountServices;
    @Mock
    private MasterAccountRepository accountRepository;
    @Mock
    private TypeAccountRepository typeAccountRepository;
    @Mock
    private AccountClientRepositorio accountClientRepositorio;
    @Mock
    private MovementDetailRepository movementDetailRepository;
    @Mock
    private WebClientApiService webClientApiService;
    @Mock
    private CartClientRepositorio cartClientRepositorio;
    @Mock
    private AccountConvert accountConvert;
    @Autowired
    private MasterAccountModel modelMaster;
    @Autowired
    private MasterAccountModel modelMasterService;
    @Autowired
    private AccountClientModel modelAccount;
    @Autowired
    private CartClientModel cartClient;
    @Autowired
    private Mono<MasterAccountModel> masterAccountModel;
    @Autowired
    private Mono<TypeModel> typeModel;
    @Autowired
    private Mono<AccountClientModel> modelClient;
    @Autowired
    private Flux<AccountClientModel> modelClients;
    @Autowired
    private Flux<CartClientModel> cartClientModel;
    @Autowired
    private Flux<MasterAccountModel> masterAccountModels;
    @Autowired
    private MovementDetailModel modelDetailMovement;

    @BeforeEach
    void init(){
        typeModel = Mono.just(new TypeModel("AHO1", "Ahorro", "A", 10, 0.0, 1, 1, 20.0, null,null,null));
        modelMasterService = new MasterAccountModel();
        modelMasterService.setId("123");
        modelMaster = new MasterAccountModel("123", "12", new TypeModel("AHO1", null, null, null, null, null, null, null, null,null,null), "2021.01.02", "A", "", 20.0, "PEN");
        modelAccount = new AccountClientModel("1", "123", "12", "N", null, null, null, Util.dateTimeToString(new Date()),null,0);
        cartClient = new CartClientModel("1","4152000000000000", "123", "123", "AHO", "A", "2022.10.01", null);

        masterAccountModel = Mono.just(modelMaster);
        masterAccountModels = Flux.just(modelMaster);
        modelClient = Mono.just(modelAccount);
        modelClients = Flux.just(modelAccount);
        cartClientModel = Flux.just(cartClient);

        MovementDetailModel movement = new MovementDetailModel();
        movement.setId(1);
        movement.setAmount(20.50);
        movement.setCommission(0.50);
        modelDetailMovement = movement;
    }

    @Test
    void createAccount(){
        Mockito.when(webClientApiService.findClient("123")).thenReturn(Mono.empty());

        Mono<MasterAccountModel> response =  masterAccountServices.createAccount(modelMaster, modelAccount);
        assertNotNull(response);

        Mono<Long> cantTarjet = Mono.just(Long.valueOf(1));
        
        Mockito.when(webClientApiService.findClient("123")).thenReturn(Mono.just(modelMasterService));
        Mockito.when(accountRepository.save(modelMaster)).thenReturn(masterAccountModel);
        Mockito.when(typeAccountRepository.findById("AHO1")).thenReturn(typeModel);
        Mockito.when(accountClientRepositorio.save(modelAccount)).thenReturn(modelClient);
        Mockito.when(accountRepository.deleteById(anyString())).thenReturn(Mono.empty());
        Mockito.when(cartClientRepositorio.countByCodeClientAndTypeCartAndCodeStatus("123","AHO","A")).thenReturn(cantTarjet);
        Mockito.when(cartClientRepositorio.findByCodeClientAndTypeCartAndCodeStatus("123","AHO","A")).thenReturn(cartClientModel);

        response =  masterAccountServices.createAccount(modelMaster, modelAccount);
        response.subscribe(
            x -> {
                assertEquals(x.getNumberAccount(), modelMaster.getNumberAccount());
                assertEquals("AHO1", x.getType().getCode());
                assertEquals("Ahorro", x.getType().getDescription());
                assertEquals("A", x.getStatus());
                assertEquals("2021.01.02", x.getStartDate());
                assertEquals("PEN", x.getCoinType());
                assertEquals(20.00, x.getAmount());
            }
        );
    }

    @Test
    void findById(){
        Mockito.when(typeAccountRepository.findById("AHO1")).thenReturn(typeModel);
        Mockito.when(accountRepository.findById("123")).thenReturn(masterAccountModel);

        masterAccountServices.findById("123").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findAllAccount(){
        Mockito.when(accountRepository.findAll()).thenReturn(masterAccountModels);
        Mockito.when(typeAccountRepository.findById("AHO1")).thenReturn(typeModel);

        masterAccountServices.findAllAccount().subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void updateAccount(){
        Mockito.when(accountRepository.save(modelMaster)).thenReturn(masterAccountModel);
        Mockito.when(accountRepository.findById("123")).thenReturn(masterAccountModel);

        masterAccountServices.updateAccount(modelMaster, "123").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );   
    }

    @Test
    void deleteBydId(){
        Mockito.when(accountRepository.deleteById("123")).thenReturn(Mono.empty());
        Mono<Void> respuesta = masterAccountServices.deleteBydId("123");
        assertNotNull(respuesta);
    }

    @Test
    void findStartDate(){
        Mockito.when(accountRepository.findByStartDate("2021.01.02")).thenReturn(masterAccountModels);
        masterAccountServices.findStartDate("2021.01.02").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findByAccount(){
        Mockito.when(typeAccountRepository.findById("AHO1")).thenReturn(typeModel);
        Mockito.when(accountRepository.findByNumberAccount("12")).thenReturn(masterAccountModel);

        masterAccountServices.findByAccount("12").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findByClient(){
        Mockito.when(typeAccountRepository.findById("AHO1")).thenReturn(typeModel);
        Mockito.when(accountRepository.findByNumberAccount("12")).thenReturn(masterAccountModel);
        Mockito.when(accountClientRepositorio.findByCodeClient("123")).thenReturn(modelClients);

        masterAccountServices.findByClient("123").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findByStartDateBetween(){
        Mockito.when(accountRepository.findAll()).thenReturn(masterAccountModels);

        masterAccountServices.findByStartDateBetween("2021.01.02", "2021.01.02").subscribe(
            x -> {
                assertEquals("AHO1", x.getType().getCode());
                assertEquals(modelMaster.getId(), x.getId());
                assertEquals(modelMaster.getNumberAccount(), x.getNumberAccount());
                assertEquals(modelMaster.getType().getCode(), x.getType().getCode());
                assertEquals(modelMaster.getStatus(), x.getStatus());
                assertEquals(modelMaster.getAmount(), x.getAmount());
                assertEquals(modelMaster.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findByStartDateBetweenDetail(){
        AccountDetailDto response = new AccountDetailDto();
        response.setId("1");
        response.setNumberAccount("12");
        response.setAmount(20.0);

        Mockito.when(movementDetailRepository.findByNumberAccount("12")).thenReturn(Flux.just(modelDetailMovement));
        Mockito.when(accountConvert.accountToDetail(modelMaster)).thenReturn(response);
        Mockito.when(accountRepository.findAll()).thenReturn(masterAccountModels);

        masterAccountServices.findByStartDateBetweenDetail("2021.01.02", "2021.01.02").subscribe(
            x -> {
                assertEquals(response.getId(), x.getId());
                assertEquals(response.getNumberAccount(), x.getNumberAccount());
            }
        );
    }
}
