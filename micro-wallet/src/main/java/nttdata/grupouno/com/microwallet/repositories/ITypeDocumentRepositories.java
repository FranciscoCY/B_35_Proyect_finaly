package nttdata.grupouno.com.microwallet.repositories;

import nttdata.grupouno.com.microwallet.models.TypeDocumentModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ITypeDocumentRepositories {
    Mono<TypeDocumentModel> save(TypeDocumentModel model);
    Mono<TypeDocumentModel> get(String key);
    Flux<TypeDocumentModel> getAll();
    Mono<Long> delete(String id);
}
