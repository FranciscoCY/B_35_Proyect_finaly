package nttdata.grupouno.com.operations.services;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import nttdata.grupouno.com.operations.repositories.implementation.AccountClientRepositorio;
import nttdata.grupouno.com.operations.services.implementation.AccountClientService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class AccountClientServiceTest {
    @Mock
    private AccountClientRepositorio accountClientRepositorio;
    @Mock
    private IWebClientApiService apiClient;
    @InjectMocks
    private AccountClientService accountClientService;
    @Autowired
    private Mono<AccountClientModel> accountClientModel;
    @Autowired
    private Flux<AccountClientModel> accountClienteModels;
    @Autowired
    private Mono<Long> countClienType;

    @BeforeEach
    void init() {
        accountClientModel = Mono.just(new AccountClientModel("1", "1", "12", "N", "T", "AHO1", null, Util.dateTimeToString(new Date()),null,0));
        accountClienteModels = accountClientModel.flux();
        countClienType = Mono.just(Long.valueOf("1"));
    }

    @Test
	void findById() {
        assertNotNull(accountClientModel);

        Mockito.when(accountClientRepositorio.findById("1")).thenReturn(accountClientModel);
        Mono<AccountClientModel> response = accountClientService.findById("1");

        assertEquals(accountClientModel, response);
        response.subscribe(x -> {
            assertEquals("1", x.getCodeClient());
            assertEquals("12", x.getNumberAccount());
        });
	}

    @Test
    void countByCodeClientAndTypeAccount() {
        Mockito.when(accountClientRepositorio.countByCodeClientAndTypeAccount("1", "AHO")).thenReturn(countClienType);
        Mono<Long> countResponse = accountClientService.countByCodeClientAndTypeAccount("1", "AHO");
        
        assertEquals(countResponse, countClienType);
        countResponse.subscribe(x -> assertEquals(1, x));
    }

    @Test
    void findAll(){
        Mockito.when(accountClientRepositorio.findAll()).thenReturn(accountClienteModels);
        Flux<AccountClientModel> response = accountClientService.findAll();
        assertEquals(response, accountClienteModels);
    }

    @Test
    void findByClientTypeAccount(){
        Mockito.when(accountClientRepositorio.findByNumberAccountAndTypeAccount("1", "AHO")).thenReturn(accountClienteModels);
        Flux<AccountClientModel> response = accountClientService.findByClientTypeAccount("1", "AHO");
        assertEquals(response, accountClienteModels);
    }
}
