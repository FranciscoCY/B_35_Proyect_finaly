package nttdata.grupouno.com.microwallet.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nttdata.grupouno.com.microwallet.models.TypeChangeModel;
import nttdata.grupouno.com.microwallet.services.ITypeChangeService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/wallet/typechange")
public class TypeChangeController {
    @Autowired
    private ITypeChangeService typeChangeService;

    @GetMapping("/all")
    public Flux<TypeChangeModel> getAllTypeChange() {
        return typeChangeService.findAll();
    }

    @PostMapping(value="/")
    public Mono<TypeChangeModel> registerTypeChange(@RequestBody TypeChangeModel entity) {       
        return typeChangeService.register(entity);
    }

    @PutMapping("/{id}")
    public Mono<TypeChangeModel> updateTypeChange(@RequestBody TypeChangeModel entity, @RequestParam String id) {
        return typeChangeService.findById(id).flatMap(
            x -> {
                x.setAmountBuy(entity.getAmountBuy());
                x.setAmountSale(entity.getAmountSale());
                return typeChangeService.register(entity);
            }
        );
    }
    
    @GetMapping("/{coin}")
    public Mono<TypeChangeModel> getByDateCoin(@RequestParam String coin) {
        return typeChangeService.fingByDateAndTypeCoin(new Date(),coin);
    }
}
