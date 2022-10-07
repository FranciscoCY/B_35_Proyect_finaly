package nttdata.grupouno.com.operations.controllers;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.services.IAccountClientService;
import nttdata.grupouno.com.operations.services.IMasterAccountServices;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import nttdata.grupouno.com.operations.services.implementation.AccountClientService;
import nttdata.grupouno.com.operations.services.implementation.MasterAccountServices;
import nttdata.grupouno.com.operations.services.implementation.MovementDetailService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class MovementDetailControllerTest {

    @InjectMocks
    private MovementDetailController detailController;
    @Mock
    private MovementDetailService movementService;
    @Mock
    private AccountClientService clientService;
    @Mock
    private MasterAccountServices masterAccountServices;
    @Autowired
    private MovementDetailModel modelD;
    @Autowired
    private MovementDetailModel modelR;
    @Autowired
    private TypeModel typeModel;
    @Autowired
    private TypeModel typeModelCorriente;
    @Autowired
    private MasterAccountModel accountModel;
    @Autowired
    private MasterAccountModel accountModelCorriente;
    @Autowired
    private AccountClientModel accountClientModel;

    @BeforeEach
    void init() {
        modelR=new MovementDetailModel(1,"654321987","2022.10.04",20.0,'R',1.0,"PEN","10","2022");
        modelD=new MovementDetailModel(2,"654321987","2022.10.04",20.0,'D',1.0,"PEN","10","2022");
        typeModel = new TypeModel("AHO1", "Ahorro", "A", 1, 0.0, 1, 1, 10.00, null,null,null);
        typeModelCorriente = new TypeModel("AHO2", "Corriente", "A", 1, 0.0, 1, 1, 10.00, null,10.0,"C");
        accountModel=new MasterAccountModel("AHO0001", "654321987", typeModel, "2022.09.23", "A", null, 500.0, "PEN");
        accountClientModel=new AccountClientModel("C0001","85671457","654321200","N","T","AHO2","123456","2022.09.01","45698731",0);
        accountModelCorriente=new MasterAccountModel("CRE0001", "654321200", typeModelCorriente, "2022.09.23", "A", null, 500.0, "PEN");
    }

    @Test
    void findAllAccount() {
        Mockito.when(movementService.findAllMovements()).thenReturn(Mono.just(modelD).flux());
        detailController.findAllAccount().subscribe(a -> {
            assertEquals(modelD.getId(),a.getId());
            assertEquals(modelD.getNumberAccount(),a.getNumberAccount());
            assertEquals(modelD.getDate(),a.getDate());
            assertEquals(modelD.getAmount(),a.getAmount());
            assertEquals(modelD.getMovementType(),a.getMovementType());
            assertEquals(modelD.getCommission(),a.getCommission());
            assertEquals(modelD.getCurrency(),a.getCurrency());
            assertEquals(modelD.getMonth(),a.getMonth());
            assertEquals(modelD.getYear(),a.getYear());
        });
    }

    @Test
    void findMovementById() {
        Mockito.when(movementService.findById(1)).thenReturn(Mono.just(modelD));
        ResponseEntity<Mono<MovementDetailModel>> response = detailController.findMovementById(1);
        response.getBody().subscribe(a -> {
            assertEquals(modelD.getId(),a.getId());
            assertEquals(modelD.getNumberAccount(),a.getNumberAccount());
            assertEquals(modelD.getDate(),a.getDate());
            assertEquals(modelD.getAmount(),a.getAmount());
            assertEquals(modelD.getMovementType(),a.getMovementType());
            assertEquals(modelD.getCommission(),a.getCommission());
            assertEquals(modelD.getCurrency(),a.getCurrency());
            assertEquals(modelD.getMonth(),a.getMonth());
            assertEquals(modelD.getYear(),a.getYear());
        });
    }

    @Test
    void findMovementByClient() {
        Mockito.when(movementService.findByClient("85694124")).thenReturn(Mono.just(modelD).flux());
        ResponseEntity<Flux<MovementDetailModel>> response = detailController.findMovementByClient("85694124");
        response.getBody().subscribe(a -> {
            assertEquals(modelD.getId(),a.getId());
            assertEquals(modelD.getNumberAccount(),a.getNumberAccount());
            assertEquals(modelD.getDate(),a.getDate());
            assertEquals(modelD.getAmount(),a.getAmount());
            assertEquals(modelD.getMovementType(),a.getMovementType());
            assertEquals(modelD.getCommission(),a.getCommission());
            assertEquals(modelD.getCurrency(),a.getCurrency());
            assertEquals(modelD.getMonth(),a.getMonth());
            assertEquals(modelD.getYear(),a.getYear());
        });
    }

    @Test
    void findMovementByAccount() {
        Mockito.when(movementService.findByAccount("654321987")).thenReturn(Mono.just(modelD).flux());
        ResponseEntity<Flux<MovementDetailModel>> response = detailController.findMovementByAccount("654321987");
        response.getBody().subscribe(a -> {
            assertEquals(modelD.getId(),a.getId());
            assertEquals(modelD.getNumberAccount(),a.getNumberAccount());
            assertEquals(modelD.getDate(),a.getDate());
            assertEquals(modelD.getAmount(),a.getAmount());
            assertEquals(modelD.getMovementType(),a.getMovementType());
            assertEquals(modelD.getCommission(),a.getCommission());
            assertEquals(modelD.getCurrency(),a.getCurrency());
            assertEquals(modelD.getMonth(),a.getMonth());
            assertEquals(modelD.getYear(),a.getYear());
        });
    }

    @Test
    void checkBalance() {
        Mockito.when(movementService.checkBalance("654321987")).thenReturn(Mono.just(accountModel));
        ResponseEntity<Mono<MasterAccountModel>> response = detailController.checkBalance("654321987");
        response.getBody().subscribe(a -> {
            assertEquals(accountModel.getId(),a.getId());
            assertEquals(accountModel.getNumberAccount(),a.getNumberAccount());
            assertEquals(accountModel.getCoinType(),a.getCoinType());
            assertEquals(accountModel.getStatus(),a.getStatus());
            assertEquals(accountModel.getEndDate(),a.getEndDate());
            assertEquals(accountModel.getStartDate(),a.getStartDate());
        });
    }

    @Test
    void depositAmount() {
        Mockito.when(movementService.depositAmount("654321987",25.0)).thenReturn(Mono.just(accountModel));
        ResponseEntity<Mono<MasterAccountModel>> response = detailController.depositAmount("654321987",25.0);
        response.getBody().subscribe(a -> {
            assertEquals(accountModel.getId(),a.getId());
            assertEquals(accountModel.getNumberAccount(),a.getNumberAccount());
            assertEquals(accountModel.getCoinType(),a.getCoinType());
            assertEquals(accountModel.getStatus(),a.getStatus());
            assertEquals(accountModel.getEndDate(),a.getEndDate());
            assertEquals(accountModel.getStartDate(),a.getStartDate());
        });
    }

    @Test
    void withdrawAmount() {
        Mockito.when(movementService.withdrawAmount("654321987",25.0)).thenReturn(Mono.just(accountModel));
        ResponseEntity<Mono<MasterAccountModel>> response = detailController.withdrawAmount("654321987",25.0);
        response.getBody().subscribe(a -> {
            assertEquals(accountModel.getId(),a.getId());
            assertEquals(accountModel.getNumberAccount(),a.getNumberAccount());
            assertEquals(accountModel.getCoinType(),a.getCoinType());
            assertEquals(accountModel.getStatus(),a.getStatus());
            assertEquals(accountModel.getEndDate(),a.getEndDate());
            assertEquals(accountModel.getStartDate(),a.getStartDate());
        });
    }

    @Test
    void transfer() {
        Mockito.when(masterAccountServices.findAllAccount()).thenReturn(Flux.just(accountModel));
        Map<String, String> body = new HashMap<>();
        body.put("rootAccount","654321987");
        body.put("destinationAccount","654321100");
        body.put("amount","50.0");
        detailController.transfer(body).subscribe(a -> {
            assertEquals(HttpStatus.CREATED,a.getStatusCode());
        });
    }

    @Test
    void chargeMaintenace() {
        Mockito.when(masterAccountServices.findByAccount("654321200")).thenReturn(Mono.just(accountModelCorriente));
        Mockito.when(clientService.findByNumBerAccount("654321200")).thenReturn(Flux.just(accountClientModel));
        Mockito.when(movementService.chargeMaintenace("654321200")).thenReturn(Mono.just(accountModelCorriente));
        detailController.chargeMaintenace("654321200")
                .subscribe(a -> {
                    assertEquals("Se cargo el mantenimiento",a.getBody().get("msg"));
                });
    }
}