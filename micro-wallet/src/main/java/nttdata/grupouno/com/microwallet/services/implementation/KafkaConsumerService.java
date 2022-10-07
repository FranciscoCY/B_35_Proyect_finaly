package nttdata.grupouno.com.microwallet.services.implementation;

import nttdata.grupouno.com.microwallet.models.ClientWalletModel;
import nttdata.grupouno.com.microwallet.repositories.IClientWalletRepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    private IClientWalletRepositories clientWalletRepositories;

    @KafkaListener(topics = "kafka_target_response", groupId = "group_json", containerFactory = "userKafkaListenerFactory")
    public void consume(ClientWalletModel user){
        clientWalletRepositories.findById(user.getId())
            .flatMap(x -> {
                x.setTargetAssociated(user.getTargetAssociated());
                return clientWalletRepositories.save(x);
            })
            .subscribe();
    }
}
