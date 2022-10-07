package nttdata.grupouno.com.microwallet.services;

import nttdata.grupouno.com.microwallet.models.TypeDocumentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITypeDocumentService {
    Mono<TypeDocumentModel> create(TypeDocumentModel model);

    Flux<TypeDocumentModel> getAll();

    Mono<TypeDocumentModel> getOne(String id);

    Mono<Long> deleteById(String id);
}
