package nttdata.grupouno.com.operations.services;

import nttdata.grupouno.com.operations.models.CartClientModel;
import reactor.core.publisher.Mono;

public interface ICartClientService {
    Mono<CartClientModel> findById(String id);
    Mono<CartClientModel> findByHashCartNumber(String hashCartNumber);
    Mono<CartClientModel> findByCartNumber(String cardNumber);
    Mono<CartClientModel> registerCardNumber(CartClientModel cartClientModel);
    Mono<Long> countByCodeClientAndTypeCartAndCodeStatus(String codeClient, String typeCart, String codeStatus);
}
