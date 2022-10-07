package nttdata.grupouno.com.microwallet.services.implementation;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import nttdata.grupouno.com.microwallet.repositories.IClientWalletRepositories;
import nttdata.grupouno.com.microwallet.services.IClientWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ClientWalletService implements IClientWalletService {
    @Autowired
    private IClientWalletRepositories clientWalletRepositories;

    @Autowired
    @Qualifier("KafkaProducerTemplate")
    private KafkaTemplate<String, ClientWalletModel> kafkaTemplate;

    private static final String TOPIC = "Kafka_target";

    @Override
    public Mono<ClientWalletModel> register(ClientWalletModel model) {
        if(model.getNumberDocument() == null || model.getNumberDocument().isBlank())
            return Mono.empty();
        ClientWalletModel data = new ClientWalletModel();
        data.setTargetAssociated(model.getTargetAssociated());
        data.setId(UUID.randomUUID().toString());

        model.setId(data.getId());
        model.setTargetAssociated(null);

        return clientWalletRepositories.save(model)
            .doOnSuccess(
                x ->  
                {
                    if(data.getTargetAssociated() != null && !data.getTargetAssociated().isBlank())
                    {
                        kafkaTemplate.send(TOPIC, data);
                    }
                }
            );
    }

    @Override
    public Mono<ClientWalletModel> findByNumberDocumentAndTypeDocument(String numberDocument, String typeDocument) {
        return clientWalletRepositories.findByNumberDocumentAndTypeDocument(numberDocument, typeDocument);
    }

    @Override
    public Mono<ClientWalletModel> findByNumberPhone(String number) {
        return clientWalletRepositories.findByNumberPhone(number);
    }

    @Override
    public Mono<ClientWalletModel> findById(String id) {
        return clientWalletRepositories.findById(id);
    }
}
