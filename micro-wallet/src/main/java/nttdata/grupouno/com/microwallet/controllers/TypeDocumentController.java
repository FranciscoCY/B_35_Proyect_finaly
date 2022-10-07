package nttdata.grupouno.com.microwallet.controllers;

import nttdata.grupouno.com.microwallet.models.TypeDocumentModel;
import nttdata.grupouno.com.microwallet.services.implementation.TypeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/wallet")
public class TypeDocumentController {

    @Autowired
    private TypeDocumentService typeDocumentService;

    @PostMapping("/")
    public Mono<TypeDocumentModel> addTypeDocument(@RequestBody @Valid TypeDocumentModel request){
        return typeDocumentService.create(request);
    }

    @GetMapping("/all")
    public Flux<TypeDocumentModel> getAllDocument(){
        return typeDocumentService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<TypeDocumentModel> getTypeDocument(@PathVariable String id){
        return typeDocumentService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public  Mono<Long> deleteTypeDocument(@PathVariable String id){
        return typeDocumentService.deleteById(id);
    }
}
