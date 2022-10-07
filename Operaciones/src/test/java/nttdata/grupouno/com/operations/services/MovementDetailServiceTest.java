package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.repositories.implementation.MovementDetailRepository;
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

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class MovementDetailServiceTest {
    @InjectMocks
    private MovementDetailService detailService;
    @Mock
    private MovementDetailRepository movementRepository;
    @Mock
    private MasterAccountServices masterAccountServices;
    @Mock
    private AccountClientService accountClientService;
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
        accountClientModel=new AccountClientModel("C0001","85671457","654321987","N","T","AHO2","123456","2022.09.01","45698731",0);
        accountModelCorriente=new MasterAccountModel("CRE0001", "654321200", typeModelCorriente, "2022.09.23", "A", null, 500.0, "PEN");
    }

    @Test
    void createMovement() {
    }

    @Test
    void findById() {
        Mockito.when(movementRepository.findById(1)).thenReturn(Mono.just(modelD));
        detailService.findById(1).subscribe(a -> {
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
    void findAllMovements() {
        Mockito.when(movementRepository.findAll()).thenReturn(Flux.just(modelD));
        detailService.findAllMovements().subscribe(a -> {
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
    void findByAccount() {
    }

    @Test
    void countValue() {
    }

    @Test
    void findByClient() {
    }

    @Test
    void checkBalance() {
    }

    @Test
    void depositAmount() {
    }

    @Test
    void withdrawAmount() {
    }

    @Test
    void countByAccountMonthYear() {
    }

    @Test
    void chargeMaintenace() {
    }
}
