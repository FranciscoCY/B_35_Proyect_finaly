package nttdata.grupouno.com.operations.repositories.implementation;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import nttdata.grupouno.com.operations.models.CartClientModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartClientRepositorio extends ReactiveMongoRepository<CartClientModel, String> {
    Mono<CartClientModel> findByHashCartNumber(String hashCartNumber);
    Flux<CartClientModel> findByCodeClientAndTypeCartAndCodeStatus(String codeClient, String typeCart, String codeStatus);
    Mono<Long> countByCodeClientAndTypeCartAndCodeStatus(String codeClient, String typeCart, String codeStatus);
}
