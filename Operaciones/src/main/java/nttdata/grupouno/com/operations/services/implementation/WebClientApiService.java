package nttdata.grupouno.com.operations.services.implementation;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.services.IWebClientApiService;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientApiService implements IWebClientApiService {
    private WebClient webClient = WebClient.create("http://localhost:8001");
    
    @Override
    public Mono<MasterAccountModel> findClient(String id){
        return webClient.get()
                        .uri("/api/clients/{id}", id)
                        .retrieve()
                        .bodyToMono(MasterAccountModel.class);
    }
}
