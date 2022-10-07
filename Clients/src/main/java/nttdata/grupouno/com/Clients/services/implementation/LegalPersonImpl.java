package nttdata.grupouno.com.Clients.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.Clients.models.LegalPerson;
import nttdata.grupouno.com.Clients.repositories.LegalPersonRepository;
import nttdata.grupouno.com.Clients.services.LegalPersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class LegalPersonImpl implements LegalPersonService {

    @Autowired
    private LegalPersonRepository legalPersonRepository;

    @Override
    public Flux<LegalPerson> listAllLegalPerson() {
        return legalPersonRepository.findAll();
    }

    @Override
    public Mono<LegalPerson> findAllById(String id) {
        return legalPersonRepository.findById(id);
    }

    @Override
    public Mono<LegalPerson> createLegalPerson(LegalPerson legalPerson) {
        if(legalPerson == null){
            return null;
        }else{
            legalPerson.setId(UUID.randomUUID().toString());
            return legalPersonRepository.save(legalPerson);
        }
    }

    @Override
    public Mono<LegalPerson> updateLegalPerson(LegalPerson legalPerson, String id) {
       return findAllById(id).flatMap(l ->{
           l.setBusinessName(legalPerson.getBusinessName());
           return legalPersonRepository.save(l);
       });
    }

    @Override
    public Mono<Void> deleteLegalPerson(String id) {
        return findAllById(id).flatMap(l -> legalPersonRepository.deleteById(l.getId()));
    }

    @Override
    public Mono<LegalPerson> findByRuc(Long ruc) {
        return legalPersonRepository.findByRuc(ruc);
    }

    @Override
    public Flux<LegalPerson> findByBusinessName(String businessName) {
        return legalPersonRepository.findByBusinessName(businessName);
    }
}
