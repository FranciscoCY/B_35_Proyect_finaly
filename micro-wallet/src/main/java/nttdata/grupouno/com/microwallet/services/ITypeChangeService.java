package nttdata.grupouno.com.microwallet.services;

import java.util.Date;

import nttdata.grupouno.com.microwallet.models.TypeChangeModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITypeChangeService {
    Flux<TypeChangeModel> findAll();
    Mono<TypeChangeModel> findById(String id);
    Mono<TypeChangeModel> register(TypeChangeModel model);
    Mono<TypeChangeModel> fingByDateAndTypeCoin(Date date, String typeCoin);
}
