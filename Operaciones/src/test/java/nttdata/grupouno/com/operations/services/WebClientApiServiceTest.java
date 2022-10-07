package nttdata.grupouno.com.operations.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.services.implementation.WebClientApiService;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class WebClientApiServiceTest {
    @Mock
    private WebClientApiService webClientApiService;
    @Autowired
    private Mono<MasterAccountModel> dataClient;

    @BeforeEach
    void init(){
        MasterAccountModel data = new MasterAccountModel();
        data.setId("123456");

        dataClient = Mono.just(data);
    }

    @Test
	void findById() {
        Mockito.when(webClientApiService.findClient("123456")).thenReturn(dataClient);
        webClientApiService.findClient("123456").subscribe(x -> {
            assertEquals("123456", x.getId());
        });
	}
}
