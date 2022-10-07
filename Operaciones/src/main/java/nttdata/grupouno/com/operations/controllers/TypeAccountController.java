package nttdata.grupouno.com.operations.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.services.ITypeAccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/operation/typeAccount")
public class TypeAccountController {
    @Autowired
    private ITypeAccountService typeServices;

    /**
     * @param typeModel
     * @return
     */
    @PostMapping("/")
    public Mono<ResponseEntity<Mono<TypeModel>>> createType(@RequestBody final TypeModel typeModel) {
        Mono<TypeModel> typeMono = typeServices.registerType(typeModel);
        return Mono
                .just(new ResponseEntity<>(typeMono, typeMono != null ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST));
    }

    /**
     * @param typeModels
     * @return
     */
    @PostMapping("/all")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTypeAll(@RequestBody final Iterable<TypeModel> typeModels) {
        typeModels.forEach(c -> typeServices.registerType(c).subscribe());
    }

    /**
     * @return
     */
    @GetMapping("/all")
    @ResponseBody
    public Flux<TypeModel> findAll() {
        return typeServices.findAll();
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Mono<TypeModel>>> findById(@PathVariable("id") final String id) {
        Mono<TypeModel> typeMono = typeServices.findById(id);
        return Mono.just(new ResponseEntity<>(typeMono, typeMono != null ? HttpStatus.OK : HttpStatus.NOT_FOUND));
    }

    /**
     * @param typeModel
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TypeModel>> update(@RequestBody final TypeModel typeModel,
            @PathVariable final String id) {
        return typeServices.updateType(typeModel, id)
                .map(c -> ResponseEntity.created(URI.create("/api/account/".concat(c.getCode())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(c))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") final String id) {
        return typeServices.findById(id).flatMap(c -> typeServices.deleteBydId(c.getCode())
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}