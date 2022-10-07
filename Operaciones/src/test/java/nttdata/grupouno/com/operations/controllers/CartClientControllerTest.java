package nttdata.grupouno.com.operations.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import nttdata.grupouno.com.operations.services.ICartClientService;
import nttdata.grupouno.com.operations.services.IMovementDetailService;
import nttdata.grupouno.com.operations.models.CartClientModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class CartClientControllerTest {
    @InjectMocks
    private CartClientController cartClientController;
    @Mock
    private ICartClientService cartClientService;
    @Mock
    private IMovementDetailService movementDetailService;
    @Autowired
    private CartClientModel cartClientModel;
    @Autowired
    private MovementDetailModel movementDetailModel;

    @BeforeEach
    void init(){
        cartClientModel = new CartClientModel();
        cartClientModel.setId("123456");
        cartClientModel.setCartNumber("os9FuU4Cs3M9+Ou7amrTOwe3ZfP/u7jEGbW1MQ4nokM=");
        cartClientModel.setHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c");
        cartClientModel.setCodeClient("895b3346-5091-4465-b6ed-7b9f3b8411f8");
        cartClientModel.setTypeCart("AHO");
        cartClientModel.setCodeStatus("A");
        cartClientModel.setStartDate("2022.10.04");

        movementDetailModel = new MovementDetailModel();
        movementDetailModel.setId(1);
        movementDetailModel.setAmount(20.0);
        movementDetailModel.setNumberAccount("123");
    }

    @Test
    void createCard(){
        Mockito.when(cartClientService.registerCardNumber(cartClientModel)).thenReturn(Mono.just(cartClientModel));

        cartClientController.createCard(Mono.just(cartClientModel)).subscribe(
            x -> {
                assertEquals(HttpStatus.CREATED, x.getStatusCode());
                Map<String, Object> response = x.getBody();
                if(response == null) response = new HashMap<>();
                assertNotNull(response.get("tarjet"));
                assertEquals(cartClientModel, response.get("tarjet"));
            }
        );
    }

    @Test
    void findById(){
        Mockito.when(cartClientService.findById("123456")).thenReturn(Mono.just(cartClientModel));
        cartClientController.findById("123456").subscribe(
            x -> {
                assertEquals(cartClientModel.getId(), x.getId());
                assertEquals(cartClientModel.getCartNumber(), x.getCartNumber());
                assertEquals(cartClientModel.getHashCartNumber(), x.getHashCartNumber());
                assertEquals(cartClientModel.getCodeClient(), x.getCodeClient());
                assertEquals(cartClientModel.getTypeCart(), x.getTypeCart());
                assertEquals(cartClientModel.getCodeStatus(), x.getCodeStatus());
                assertEquals(cartClientModel.getStartDate(), x.getStartDate());
            }
        );
    }

    @Test
    void findByHashNumber(){
        Mockito.when(cartClientService.findByHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c")).thenReturn(Mono.just(cartClientModel));
        cartClientController.findByHashNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c").subscribe(
            x -> {
                assertEquals(cartClientModel.getId(), x.getId());
                assertEquals(cartClientModel.getCartNumber(), x.getCartNumber());
                assertEquals(cartClientModel.getHashCartNumber(), x.getHashCartNumber());
                assertEquals(cartClientModel.getCodeClient(), x.getCodeClient());
                assertEquals(cartClientModel.getTypeCart(), x.getTypeCart());
                assertEquals(cartClientModel.getCodeStatus(), x.getCodeStatus());
                assertEquals(cartClientModel.getStartDate(), x.getStartDate());
            }
        );
    }

    @Test
    void findByCartNumber(){
        Mockito.when(cartClientService.findByCartNumber("4214000000000001")).thenReturn(Mono.just(cartClientModel));
        cartClientController.findByCartNumber("4214000000000001").subscribe(
            x -> {
                assertEquals(cartClientModel.getId(), x.getId());
                assertEquals(cartClientModel.getCartNumber(), x.getCartNumber());
                assertEquals(cartClientModel.getHashCartNumber(), x.getHashCartNumber());
                assertEquals(cartClientModel.getCodeClient(), x.getCodeClient());
                assertEquals(cartClientModel.getTypeCart(), x.getTypeCart());
                assertEquals(cartClientModel.getCodeStatus(), x.getCodeStatus());
                assertEquals(cartClientModel.getStartDate(), x.getStartDate());
            }
        );
    }

    @Test
    void listMovementsByTarjet(){
        Mockito.when(cartClientService.findByCartNumber("4214000000000001")).thenReturn(Mono.just(cartClientModel));
        Mockito.when(movementDetailService.findByClient("895b3346-5091-4465-b6ed-7b9f3b8411f8")).thenReturn(Flux.just(movementDetailModel));
        cartClientController.listMovementsByTarjet("4214000000000001").subscribe(
            x -> {
                assertEquals(movementDetailModel.getId(), x.getId());
                assertEquals(movementDetailModel.getAmount(), x.getAmount());
                assertEquals(movementDetailModel.getNumberAccount(), x.getNumberAccount());
            }
        );
    }
}
