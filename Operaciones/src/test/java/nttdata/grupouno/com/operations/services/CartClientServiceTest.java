package nttdata.grupouno.com.operations.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.CartClientModel;
import nttdata.grupouno.com.operations.models.AccountClientModel;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.repositories.implementation.AccountClientRepositorio;
import nttdata.grupouno.com.operations.repositories.implementation.CartClientRepositorio;
import nttdata.grupouno.com.operations.services.implementation.CartClientService;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class CartClientServiceTest {

    @InjectMocks
    private CartClientService cartClientService;
    @Mock
    private CartClientRepositorio cartClientRepositorio;
    @Mock
    private AccountClientRepositorio accountClientRepositorio;
    @Mock
    private IWebClientApiService webClient;
    @Autowired
    private CartClientModel cartClientModel;

    @BeforeEach
    void init() {
        cartClientModel = new CartClientModel();
        cartClientModel.setId("123456");
        cartClientModel.setCartNumber("os9FuU4Cs3M9+Ou7amrTOwe3ZfP/u7jEGbW1MQ4nokM=");
        cartClientModel.setHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c");
        cartClientModel.setCodeClient("895b3346-5091-4465-b6ed-7b9f3b8411f8");
        cartClientModel.setTypeCart("AHO");
        cartClientModel.setCodeStatus("A");
        cartClientModel.setStartDate("2022.10.04");
    }

    @Test
    void findById(){
        Mockito.when(cartClientRepositorio.findById("123456")).thenReturn(Mono.just(cartClientModel));
        cartClientService.findById("123456").subscribe(
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
    void findByHashCartNumber(){
        Mockito.when(cartClientRepositorio.findByHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c")).thenReturn(Mono.just(cartClientModel));
        cartClientService.findByHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c").subscribe(
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
        Mockito.when(cartClientRepositorio.findByHashCartNumber("ef655c9091b68efc0c0da60e2a92a599a945c13eb78aa1b50a4d255401a9fd2c")).thenReturn(Mono.just(cartClientModel));
        cartClientService.findByCartNumber("4214000000000001").subscribe(
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
    void registerCardNumber(){
        MasterAccountModel data = new MasterAccountModel();
        data.setId("895b3346-5091-4465-b6ed-7b9f3b8411f8");

        AccountClientModel dataClient = new AccountClientModel();
        dataClient.setTypeAccount("AHO");
        dataClient.setId("123");
        dataClient.setNumberAccount("123");
        dataClient.setCodeClient("895b3346-5091-4465-b6ed-7b9f3b8411f8");

        Mockito.when(webClient.findClient("895b3346-5091-4465-b6ed-7b9f3b8411f8")).thenReturn(Mono.just(data));
        Mockito.when(cartClientRepositorio.findByCodeClientAndTypeCartAndCodeStatus(
            "895b3346-5091-4465-b6ed-7b9f3b8411f8",
            "AHO",
            "A"
        )).thenReturn(Flux.just(cartClientModel));
        Mockito.when(cartClientRepositorio.save(cartClientModel)).thenReturn(Mono.just(cartClientModel));
        Mockito.when(accountClientRepositorio.countByCodeClientAndTypeAccountLike(
            "895b3346-5091-4465-b6ed-7b9f3b8411f8",
            "AHO"
        )).thenReturn(Mono.just(Long.valueOf(1)));
        Mockito.when(accountClientRepositorio.findByCodeClient("895b3346-5091-4465-b6ed-7b9f3b8411f8")).thenReturn(Flux.just(dataClient));
        Mockito.when(accountClientRepositorio.save(dataClient)).thenReturn(Mono.just(dataClient));

        cartClientService.registerCardNumber(cartClientModel).subscribe(
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
    void countByCodeClientAndTypeCartAndCodeStatus(){
        Mockito.when(cartClientRepositorio.countByCodeClientAndTypeCartAndCodeStatus(
            "895b3346-5091-4465-b6ed-7b9f3b8411f8",
            "AHO",
            "A"
        )).thenReturn(Mono.just(Long.valueOf(1)));
        cartClientService.countByCodeClientAndTypeCartAndCodeStatus(
            "895b3346-5091-4465-b6ed-7b9f3b8411f8",
            "AHO",
            "A"
        ).subscribe(
            x -> {
                assertEquals(1, x);
            }
        );
    }
}
