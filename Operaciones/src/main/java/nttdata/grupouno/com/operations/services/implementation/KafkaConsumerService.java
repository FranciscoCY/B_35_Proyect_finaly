package nttdata.grupouno.com.operations.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.operations.models.ClientWalletModel;
import nttdata.grupouno.com.operations.repositories.implementation.CartClientRepositorio;
import nttdata.grupouno.com.operations.util.Util;

@Service
public class KafkaConsumerService {

    @Autowired
    @Qualifier("kafkaTarjetTemplate")
    private KafkaTemplate<String, ClientWalletModel> kafkaTemplate;

    @Autowired
    private CartClientRepositorio cartClientRepositorio;

    @KafkaListener(topics = "Kafka_target", groupId = "group_json", containerFactory = "userKafkaListenerFactory")
    public void consumeJsonTarjet(ClientWalletModel user) {
        cartClientRepositorio.findByHashCartNumber(Util.generateHash(user.getTargetAssociated()))
            .map(x -> {
                user.setTargetAssociated(x.getHashCartNumber());
                user.setAmount(25.0);
                return x;
            })
            .doOnSuccess(x -> kafkaTemplate.send("kafka_target_response", user))
            .subscribe();
    }
}
