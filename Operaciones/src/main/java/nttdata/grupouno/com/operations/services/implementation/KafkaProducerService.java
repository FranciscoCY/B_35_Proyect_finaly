package nttdata.grupouno.com.operations.services.implementation;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, MasterAccountModel> kafkaTemplate;

    public KafkaProducerService(@Qualifier("kafkaAccountTemplate") KafkaTemplate<String, MasterAccountModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(MasterAccountModel account) {
        LOGGER.info("Producing message {}", account);
        this.kafkaTemplate.send("account-topic", account);
    }
}
