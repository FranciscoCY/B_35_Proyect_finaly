package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import reactor.core.publisher.Mono;

public interface IWebClientApiService {
    Mono<MasterAccountModel> findClient(String id);
}
