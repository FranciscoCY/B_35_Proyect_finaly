package nttdata.grupouno.com.microwallet.services.implementation;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nttdata.grupouno.com.microwallet.models.TypeChangeModel;
import nttdata.grupouno.com.microwallet.repositories.ITypeChangeRepositories;
import nttdata.grupouno.com.microwallet.services.ITypeChangeService;
import nttdata.grupouno.com.microwallet.util.Util;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TypeChangeService implements ITypeChangeService {
    @Autowired
    private ITypeChangeRepositories typeChangeRespositories;

    @Override
    public Flux<TypeChangeModel> findAll() {
        return typeChangeRespositories.findAll();
    }

    @Override
    public Mono<TypeChangeModel> findById(String id) {
        return typeChangeRespositories.findById(id);
    }

    @Override
    public Mono<TypeChangeModel> register(TypeChangeModel model) {
        return typeChangeRespositories.save(model);
    }

    @Override
    public Mono<TypeChangeModel> fingByDateAndTypeCoin(Date date, String typeCoin) {
        return typeChangeRespositories.findByDateAndTypeCoin(Util.dateToString(date), typeCoin);
    }
}
