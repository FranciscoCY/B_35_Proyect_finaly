package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.TypeModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITypeAccountService {
    Mono<TypeModel> registerType(TypeModel typeModel);
    Flux<TypeModel> findAll();
    Mono<TypeModel> findById(String id);
    Mono<TypeModel> updateType(TypeModel typeModel, String id);
    Mono<Void> deleteBydId(String id);
}
