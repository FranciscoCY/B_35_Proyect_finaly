package nttdata.grupouno.com.microwallet.services.implementation;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.microwallet.models.TypeDocumentModel;
import nttdata.grupouno.com.microwallet.repositories.ITypeDocumentRepositories;
import nttdata.grupouno.com.microwallet.services.ITypeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TypeDocumentService implements ITypeDocumentService {

    @Autowired
    private ITypeDocumentRepositories typeDocumentRepositories;

    @Override
    public Mono<TypeDocumentModel> create(TypeDocumentModel model) {
        return typeDocumentRepositories.save(model);
    }

    @Override
    public Flux<TypeDocumentModel> getAll() {
        return typeDocumentRepositories.getAll();
    }

    @Override
    public Mono<TypeDocumentModel> getOne(String id) {
        return typeDocumentRepositories.get(id);
    }

    @Override
    public Mono<Long> deleteById(String id) {
        return typeDocumentRepositories.delete(id);
    }
}
