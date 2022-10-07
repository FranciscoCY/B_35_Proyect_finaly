package nttdata.grupouno.com.microwallet.services;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import reactor.core.publisher.Mono;

public interface IClientWalletService {
    Mono<ClientWalletModel> register(ClientWalletModel model);
    Mono<ClientWalletModel> findByNumberDocumentAndTypeDocument(String numberDocument, String typeDocument);
    Mono<ClientWalletModel> findByNumberPhone(String number);
    Mono<ClientWalletModel> findById(String id);
}
