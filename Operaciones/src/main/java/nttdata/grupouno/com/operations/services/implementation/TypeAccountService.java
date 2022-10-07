package nttdata.grupouno.com.operations.services.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.repositories.implementation.TypeAccountRepository;
import nttdata.grupouno.com.operations.services.ITypeAccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TypeAccountService implements ITypeAccountService {

    @Autowired
    private TypeAccountRepository typeAccountRepository;

    @Override
    public Mono<TypeModel> registerType(TypeModel typeModel) {
        return typeAccountRepository.save(typeModel);
    }

    @Override
    public Flux<TypeModel> findAll() {
        return typeAccountRepository.findAll();
    }

    @Override
    public Mono<TypeModel> findById(String id) {
        return typeAccountRepository.findById(id);
    }

    @Override
    public Mono<TypeModel> updateType(TypeModel typeModel, String id) {
        return typeAccountRepository.findById(id).flatMap(c -> {
            typeModel.setCode(c.getCode());
            return typeAccountRepository.save(typeModel);
        });
    }

    @Override
    public Mono<Void> deleteBydId(String id) {
        return typeAccountRepository.deleteById(id);
    }

}
