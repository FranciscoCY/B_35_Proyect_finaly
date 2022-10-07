package nttdata.grupouno.com.microwallet.repositories.implementation;

import nttdata.grupouno.com.microwallet.config.ObjectMapperUtils;
import nttdata.grupouno.com.microwallet.models.TypeDocumentModel;
import nttdata.grupouno.com.microwallet.repositories.ITypeDocumentRepositories;
import nttdata.grupouno.com.microwallet.repositories.redis.ReactiveRedisComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static nttdata.grupouno.com.microwallet.config.ObjectMapperUtils.BOOK_KEY;

@Repository
public class TypeDocumentRepositories implements ITypeDocumentRepositories {

    @Autowired
    private ReactiveRedisComponent reactiveRedisComponent;

    @Override
    public Mono<TypeDocumentModel> save(TypeDocumentModel model) {
        return reactiveRedisComponent.set(BOOK_KEY, model.getId(), model).map(b -> model);
    }

    @Override
    public Mono<TypeDocumentModel> get(String key) {
        return reactiveRedisComponent.get(BOOK_KEY, key).flatMap(d -> Mono.just(ObjectMapperUtils.objectMapper(d, TypeDocumentModel.class)));
    }

    @Override
    public Flux<TypeDocumentModel> getAll() {
        return reactiveRedisComponent.get(BOOK_KEY).map(b -> ObjectMapperUtils.objectMapper(b, TypeDocumentModel.class))
                .collectList().flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Long> delete(String id) {
        return reactiveRedisComponent.remove(BOOK_KEY,id);
    }
}
