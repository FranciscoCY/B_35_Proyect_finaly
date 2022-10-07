package nttdata.grupouno.com.operations.controllers;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.DebtClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.services.IDebtClientService;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class DebtClientControllerTest {
    @InjectMocks
    private DebtClientController debtClientController;
    @Mock
    private IDebtClientService debtClientService;
    @Mock
    private IMasterAccountServices accountServices;
    @Mock
    private IMovementDetailService movementDetailService;
    @Autowired
    private DebtClientModel model;
    @Autowired
    private DebtClientModel modelCancel;
    @Autowired
    private Mono<DebtClientModel> modelMono;
    @Autowired
    private Flux<DebtClientModel> modelFlux;
    @Autowired
    private TypeModel typeModel;
    @Autowired
    private MasterAccountModel accountModel;
    @Autowired
    private MasterAccountModel account;
    @Autowired
    private Mono<MasterAccountModel> accountModelMono;
    @Autowired
    private Flux<MasterAccountModel> accountModelFlux;

    @BeforeEach
    void init() {
        model=new DebtClientModel("A001","408535551732729",300.0,"P","2022.10.15","2022.10.01","2022.10.03","CRE1","96671457");
        modelCancel=new DebtClientModel("A002","408535551732729",300.0,"C","2022.10.15","2022.10.01","2022.10.03","CRE1","96671457");
        modelMono=Mono.just(model);
        modelFlux=modelMono.flux();

        typeModel = new TypeModel("AHO1", "Ahorro", "A", 1, 0.0, 1, 1, 10.00, null,null,null);
        accountModel=new MasterAccountModel("AHO0001", "859632987654", typeModel, "2022.09.23", "A", null, 500.0, "PEN");
        account=new MasterAccountModel("AHO0002", "859632987200", typeModel, "2022.09.23", "A", null, 0.0, "PEN");
        accountModelMono=Mono.just(accountModel);
        accountModelFlux=accountModelMono.flux();
    }

    @Test
    void createDebt() {
        Mockito.when(debtClientService.createdDebt(model)).thenReturn(modelMono);
        debtClientController.createDebt(modelMono).subscribe(a -> {
            assertEquals(HttpStatus.CREATED, a.getStatusCode());
            assertEquals(model, Objects.requireNonNull(a.getBody()).get("debt"));
        });
    }

    @Test
    void findAllDebt() {
        Mockito.when(debtClientService.findAll()).thenReturn(modelFlux);
        debtClientController.findAllDebt().subscribe(a -> {
            assertEquals(model.getId(), a.getId());
            assertEquals(model.getNumberAccount(), a.getNumberAccount());
            assertEquals(model.getAmount(), a.getAmount());
            assertEquals(model.getState(), a.getState());
            assertEquals(model.getExpirationDate(), a.getExpirationDate());
            assertEquals(model.getIssueDate(), a.getIssueDate());
            assertEquals(model.getPaymentDate(), a.getPaymentDate());
            assertEquals(model.getCodeCredit(), a.getCodeCredit());
            assertEquals(model.getPaidBy(), a.getPaidBy());
        });
    }

    @Test
    void findById() {
        Mockito.when(debtClientService.findById("A001")).thenReturn(modelMono);
        debtClientController.findById("A001")
                .subscribe(a -> {
                    assertEquals(model.getId(), Objects.requireNonNull(a.getBody()).getId());
                    assertEquals(model.getNumberAccount(), a.getBody().getNumberAccount());
                    assertEquals(model.getAmount(), a.getBody().getAmount());
                    assertEquals(model.getState(), a.getBody().getState());
                    assertEquals(model.getExpirationDate(), a.getBody().getExpirationDate());
                    assertEquals(model.getIssueDate(), a.getBody().getIssueDate());
                    assertEquals(model.getPaymentDate(), a.getBody().getPaymentDate());
                    assertEquals(model.getCodeCredit(), a.getBody().getCodeCredit());
                    assertEquals(model.getPaidBy(), a.getBody().getPaidBy());
                });
    }

    @Test
    void findPendingDebt() {
        Mockito.when(debtClientService.findPendingDebt("70856415")).thenReturn(modelFlux);
        debtClientController.findPendingDebt("70856415")
                .subscribe(a -> Objects.requireNonNull(a.getBody())
                        .subscribe(b -> {
            assertEquals(model.getId(), b.getId());
            assertEquals(model.getNumberAccount(), b.getNumberAccount());
            assertEquals(model.getAmount(), b.getAmount());
            assertEquals(model.getState(), b.getState());
            assertEquals(model.getExpirationDate(), b.getExpirationDate());
            assertEquals(model.getIssueDate(), b.getIssueDate());
            assertEquals(model.getPaymentDate(), b.getPaymentDate());
            assertEquals(model.getCodeCredit(), b.getCodeCredit());
            assertEquals(model.getPaidBy(), b.getPaidBy());
        }));
    }

    @Test
    void payDebt() {
        Mockito.when(debtClientService.findById("A002")).thenReturn(Mono.just(modelCancel));
        debtClientController.payDebt("A002","96671457")
                .subscribe(a -> assertEquals("La deuda se encuentra cancelada", Objects.requireNonNull(a.getBody()).get("msg")));

        Mockito.when(debtClientService.findById("A001")).thenReturn(modelMono);
        Mockito.when(accountServices.findByClient("96671457")).thenReturn(Flux.just(account));
        debtClientController.payDebt("A001","96671457")
                .subscribe(a -> assertEquals("El Cliente no cuenta con saldo suficiente", Objects.requireNonNull(a.getBody()).get("msg")));

        Mockito.when(accountServices.findByClient("96671457")).thenReturn(accountModelFlux);
        Mockito.when(movementDetailService.withdrawAmount("AHO0001",300.0)).thenReturn(accountModelMono);
        Mockito.when(debtClientService.updatedDebt("A001",model)).thenReturn(modelMono);
        debtClientController.payDebt("A001","96671457")
                .subscribe(a -> assertEquals("Deuda cancelada con exitó", Objects.requireNonNull(a.getBody()).get("msg")));
    }

    @Test
    void deleteDebt() {
        Mockito.when(debtClientService.findById("A001")).thenReturn(modelMono);
        Mockito.when(debtClientService.deleteDebtById("A001")).thenReturn(Mono.empty());
        debtClientController.deleteDebt("A001")
                .subscribe(a -> assertEquals(HttpStatus.NO_CONTENT, a.getStatusCode()));
    }
}