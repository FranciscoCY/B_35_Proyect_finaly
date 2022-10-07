package nttdata.grupouno.com.Clients.services.implementation;

import nttdata.grupouno.com.Clients.models.MasterAccount;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "account-topic", groupId = "group_json", containerFactory = "accountKafkaListenerFactory")
    public MasterAccount consumeJsonAccount(MasterAccount account) {
        System.out.println("Se ha creado la siguiente cuenta: " + account);
        return account;
    }
}
