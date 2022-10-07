package nttdata.grupouno.com.operations.services.implementation;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.operations.models.CartClientModel;
import nttdata.grupouno.com.operations.repositories.implementation.AccountClientRepositorio;
import nttdata.grupouno.com.operations.repositories.implementation.CartClientRepositorio;
import nttdata.grupouno.com.operations.services.ICartClientService;
import nttdata.grupouno.com.operations.services.IWebClientApiService;
import nttdata.grupouno.com.operations.util.Util;
import reactor.core.publisher.Mono;

@Service
public class CartClientService implements ICartClientService {
    @Autowired
    private CartClientRepositorio cartClientRepositorio;
    @Autowired
    private AccountClientRepositorio accountClientRepositorio;
    @Autowired
    private IWebClientApiService webClient;

    @Override
    public Mono<CartClientModel> findById(String id) {
        return cartClientRepositorio.findById(id).map(x -> {
            x.setCartNumber(Util.desencriptAES(x.getCartNumber(), x.getCodeClient()));
            return x;
        });
    }

    @Override
    public Mono<CartClientModel> findByHashCartNumber(String hashCartNumber) {
        return cartClientRepositorio.findByHashCartNumber(hashCartNumber).map(x -> {
            x.setCartNumber(Util.desencriptAES(x.getCartNumber(), x.getCodeClient()));
            return x;
        });
    }

    @Override
    public Mono<CartClientModel> findByCartNumber(String cardNumber) {
        return cartClientRepositorio.findByHashCartNumber(Util.generateHash(cardNumber)).map(x -> {
            x.setCartNumber(cardNumber);
            return x;
        });
    }

    @Override
    public Mono<CartClientModel> registerCardNumber(CartClientModel cartClientModel) {
        return webClient.findClient(cartClientModel.getCodeClient()).flatMap(
            z -> {
                if(!z.getId().equals(cartClientModel.getCodeClient())) return Mono.empty();

                String cardNumber = Util.generateCartNumber();
                cartClientModel.setId(UUID.randomUUID().toString());
                cartClientModel.setCartNumber(Util.encriptAES(cardNumber, z.getId()));
                cartClientModel.setHashCartNumber(Util.generateHash(cardNumber));
                cartClientModel.setCodeStatus("A");
                cartClientModel.setStartDate(Util.dateToString(new Date()));

                return cartClientRepositorio.findByCodeClientAndTypeCartAndCodeStatus(
                    cartClientModel.getCodeClient(), cartClientModel.getTypeCart(), "A"
                )
                .flatMap(a -> {
                    a.setCodeStatus("C");
                    a.setEndDate(Util.dateToString(new Date()));
                    return cartClientRepositorio.save(a).flatMap(y -> cartClientRepositorio.save(cartClientModel));
                })
                .switchIfEmpty(cartClientRepositorio.save(cartClientModel))
                .single()
                .map(x -> {
                    x.setCartNumber(Util.desencriptAES(x.getCartNumber(), x.getCodeClient()));
                    return x;
                })
                .flatMap(b -> accountClientRepositorio.countByCodeClientAndTypeAccountLike(cartClientModel.getCodeClient(), b.getTypeCart())
                    .flatMap(c -> {
                        if(c.longValue() == 0) return Mono.just(b);
                        return accountClientRepositorio
                            .findByCodeClient(cartClientModel.getCodeClient())
                            .filter(d -> d.getTypeAccount().contains(b.getTypeCart()))
                            .flatMap(d -> {
                                d.setIdCartClient(b.getId());
                                return accountClientRepositorio.save(d);
                            })
                            .map(e -> b)
                            .next();
                    })
                );
            }
        ).switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Long> countByCodeClientAndTypeCartAndCodeStatus(String codeClient, String typeCart, String codeStatus) {
        return cartClientRepositorio.countByCodeClientAndTypeCartAndCodeStatus(codeClient, typeCart, codeStatus);
    }

}
