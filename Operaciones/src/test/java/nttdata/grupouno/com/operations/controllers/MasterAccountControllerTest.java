package nttdata.grupouno.com.operations.controllers;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.DebtClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.models.dto.AccountDetailDto;
import nttdata.grupouno.com.operations.models.dto.RegisterAccountDto;
import nttdata.grupouno.com.operations.services.IAccountClientService;
import nttdata.grupouno.com.operations.services.IDebtClientService;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import nttdata.grupouno.com.operations.services.ITypeAccountService;
import nttdata.grupouno.com.operations.util.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class MasterAccountControllerTest {
    @InjectMocks
    private MasterAccountController masterAccountController;
    @Mock
    private IMasterAccountServices accountServices;
    @Mock
    private ITypeAccountService typeAccountService;
    @Mock
    private IAccountClientService accountClientService;
    @Mock
    private IDebtClientService debtClientService;
    @Autowired
    private TypeModel typeModel;
    @Autowired
    private RegisterAccountDto modelRegister;
    @Autowired 
    private MasterAccountModel masteModel;
    @Autowired
    private AccountClientModel accountModel;
    @Autowired
    private AccountDetailDto detailAccount;
    @BeforeEach
    void init(){
        typeModel = new TypeModel("AHO1", "Ahorro", "A", 1, 0.0, 1, 1, 10.00, null,null,null);
        masteModel = new MasterAccountModel("1", "12", typeModel, "2022.09.23", "A", null, 5.0, "PEN");
        accountModel = new AccountClientModel("11", "123", null, "N", "T", null, null, Util.dateTimeToString(new Date()),null,0);
        modelRegister = new RegisterAccountDto(masteModel, accountModel);

        detailAccount = new AccountDetailDto();
        detailAccount.setId("1");
        detailAccount.setNumberAccount("123");
        detailAccount.setStartDate("2022.01.01");
    }

    @Test
    void createAccountBank(){
        Mono<ResponseEntity<Map<String, Object>>> response;

        /// Valid deuda pendiente
        Mockito.when(debtClientService.findPendingDebt("123")).thenReturn(Flux.just(new DebtClientModel()));

        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertNotNull(data.get("limit"));
            assertEquals("El cliente tiene una deuda pendiente", data.get("limit"));
        });

        /// Valid Type product
        Mockito.when(debtClientService.findPendingDebt("123")).thenReturn(Flux.empty());
        Mockito.when(typeAccountService.findById("AHO1")).thenReturn(Mono.empty());
        
        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());
        });

        /// Valid limit product
        typeModel.setAmountStart(50.0);
        Mockito.when(typeAccountService.findById("AHO1")).thenReturn(Mono.just(typeModel));

        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertNotNull(data.get("limitAmount"));
            assertEquals("El monto mínimo de apertura para este producto es de ".concat(typeModel.getAmountStart().toString()), data.get("limitAmount"));
        });

        ///  Valid required account
        typeModel.setAmountStart(00.0);
        typeModel.setCodeRequired("CRE");
        Mockito.when(typeAccountService.findById("AHO1")).thenReturn(Mono.just(typeModel));
        Mockito.when(accountClientService.countByCodeClientAndTypeAccountLike("123", "CRE")).thenReturn(Mono.just(Long.valueOf("0")));
        
        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertNotNull(data.get("limitType"));
            assertEquals("Para este producto es requisito tener una(s) cuenta(s) del tipo: ".concat(typeModel.getCodeRequired()), data.get("limitType"));
        });

        /// Valid number limit of type person and type account
        typeModel.setCodeRequired(null);
        typeModel.setCountPerson(1);
        Mockito.when(accountClientService.countByCodeClientAndTypeAccountLike("123", null)).thenReturn(Mono.just(Long.valueOf("1")));
        Mockito.when(accountClientService.countByCodeClientAndTypeAccountAndTypeClient("123", "AHO1", "N")).thenReturn(Mono.just(Long.valueOf("1")));

        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertNotNull(data.get("limitMaxN"));
            assertEquals("El máximo de cuentas del tipo <<".concat(typeModel.getDescription()).concat(">> es ").concat(typeModel.getCountPerson().toString()), data.get("limitMaxN"));
        });

        typeModel.setCountBusiness(1);
        accountModel.setTypeClient("J");
        Mockito.when(accountClientService.countByCodeClientAndTypeAccountAndTypeClient("123", "AHO1", "J")).thenReturn(Mono.just(Long.valueOf("1")));
        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertNotNull(data.get("limitMaxJ"));
            assertEquals("El máximo de cuentas del tipo <<".concat(typeModel.getDescription()).concat(">> es ").concat(typeModel.getCountPerson().toString()), data.get("limitMaxJ"));
        });

        /// Valid duplicit account
        accountModel.setTypeClient("N");
        typeModel.setCountPerson(3);

        Mockito.when(accountServices.findByAccount("12")).thenReturn(Mono.just(masteModel));
        Mockito.when(accountClientService.validCreditAccountUntilToday("123","CRE2","J")).thenReturn(Mono.just(0L));
        Mockito.when(accountServices.createAccount(masteModel, accountModel)).thenReturn(Mono.empty());

        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.BAD_REQUEST, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();

            assertEquals(data.get("duplicit"), masteModel);
            assertEquals(data.get("typeAccount"), typeModel);
        });


        /// Valid create account
        Mockito.when(accountServices.findByAccount("12")).thenReturn(Mono.empty());
        Mockito.when(accountClientService.validCreditAccountUntilToday("123","CRE2","J")).thenReturn(Mono.just(0L));
        Mockito.when(accountServices.createAccount(masteModel, accountModel)).thenReturn(Mono.just(masteModel));

        response = masterAccountController.createAccountBank(Mono.just(modelRegister));
        response.subscribe(x -> {
            assertEquals(HttpStatus.CREATED, x.getStatusCode());

            Map<String, Object> data = x.getBody();
            if(data == null ) data = new HashMap<>();
            assertEquals(data.get("account"), masteModel);
            assertEquals(data.get("typeAccount"), typeModel);
        });
    }

    @Test
    void findAllAccount(){
        Mockito.when(accountServices.findAllAccount()).thenReturn(Flux.just(masteModel));
        masterAccountController.findAllAccount().subscribe(
            x -> {
                assertEquals(masteModel.getId(), x.getId());
                assertEquals(masteModel.getNumberAccount(), x.getNumberAccount());
                assertEquals(masteModel.getStatus(), x.getStatus());
                assertEquals(masteModel.getAmount(), x.getAmount());
                assertEquals(masteModel.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findAccountById(){
        Mockito.when(accountServices.findById("1")).thenReturn(Mono.just(masteModel));
        masterAccountController.findAccountById("1").subscribe(
            x -> {
                assertEquals(HttpStatus.OK, x.getStatusCode());
                Mono<MasterAccountModel> data = x.getBody();
                if(data == null) data = Mono.empty();
                data.subscribe(
                    y -> {
                        assertEquals(masteModel.getId(), y.getId());
                        assertEquals(masteModel.getNumberAccount(), y.getNumberAccount());
                        assertEquals(masteModel.getStatus(), y.getStatus());
                        assertEquals(masteModel.getAmount(), y.getAmount());
                        assertEquals(masteModel.getCoinType(), y.getCoinType());
                    }
                );
            }
        );
    }

    @Test
    void update(){
        Mockito.when(accountServices.updateAccount(masteModel, "1")).thenReturn(Mono.just(masteModel));
        masterAccountController.update(masteModel, "1").subscribe(
            x -> {
                assertEquals(HttpStatus.CREATED, x.getStatusCode());
                MasterAccountModel y = x.getBody();
                if(y == null) y = new MasterAccountModel();
                assertEquals(masteModel.getId(), y.getId());
                assertEquals(masteModel.getNumberAccount(), y.getNumberAccount());
                assertEquals(masteModel.getStatus(), y.getStatus());
                assertEquals(masteModel.getAmount(), y.getAmount());
                assertEquals(masteModel.getCoinType(), y.getCoinType());
            }
        );
    }

    @Test
    void delete(){
        Mockito.when(accountServices.findById("1")).thenReturn(Mono.just(masteModel));
        Mockito.when(accountServices.deleteBydId("1")).thenReturn(Mono.empty());
        masterAccountController.delete("1").subscribe(
            x -> {
                assertEquals(HttpStatus.NO_CONTENT, x.getStatusCode());
            }
        );
    }

    @Test
    void findStartDate(){
        Mockito.when(accountServices.findStartDate("2022.01.01")).thenReturn(Flux.just(masteModel));
        masterAccountController.findStartDate("2022.01.01").subscribe(
            x -> {
                assertEquals(masteModel.getId(), x.getId());
                assertEquals(masteModel.getNumberAccount(), x.getNumberAccount());
                assertEquals(masteModel.getStatus(), x.getStatus());
                assertEquals(masteModel.getAmount(), x.getAmount());
                assertEquals(masteModel.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findByClient(){
        Mockito.when(accountServices.findByClient("123")).thenReturn(Flux.just(masteModel));
        masterAccountController.findByClient("123").subscribe(
            x -> {
                assertEquals(HttpStatus.OK, x.getStatusCode());

                Flux<MasterAccountModel> data = x.getBody();
                if(data == null) data = Flux.empty();
                data.subscribe(
                    y -> {
                        assertEquals(masteModel.getId(), y.getId());
                        assertEquals(masteModel.getNumberAccount(), y.getNumberAccount());
                        assertEquals(masteModel.getStatus(), y.getStatus());
                        assertEquals(masteModel.getAmount(), y.getAmount());
                        assertEquals(masteModel.getCoinType(), y.getCoinType());
                    }
                );
            }
        );
    }

    @Test
    void findAccountsBetween(){
        Mockito.when(accountServices.findByStartDateBetween("2022.01.01", "2022.01.02")).thenReturn(Flux.just(masteModel));
        masterAccountController.findAccountsBetween("2022.01.01","2022.01.02").subscribe(
            x -> {
                assertEquals(masteModel.getId(), x.getId());
                assertEquals(masteModel.getNumberAccount(), x.getNumberAccount());
                assertEquals(masteModel.getStatus(), x.getStatus());
                assertEquals(masteModel.getAmount(), x.getAmount());
                assertEquals(masteModel.getCoinType(), x.getCoinType());
            }
        );
    }

    @Test
    void findAccountsBetweenDetail(){
        Mockito.when(accountServices.findByStartDateBetweenDetail("2022.01.01", "2022.01.02")).thenReturn(Flux.just(detailAccount));
        masterAccountController.findAccountsBetweenDetail("2022.01.01","2022.01.02").subscribe(
            x -> {
                assertEquals(masteModel.getId(), x.getId());
                assertEquals("123", x.getNumberAccount());
            }
        );
    }
}
