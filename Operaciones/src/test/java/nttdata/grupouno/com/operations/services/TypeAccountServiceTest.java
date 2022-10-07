package nttdata.grupouno.com.operations.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.repositories.implementation.TypeAccountRepository;
import nttdata.grupouno.com.operations.services.implementation.TypeAccountService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class TypeAccountServiceTest {
    @Mock
    private TypeAccountRepository typeAccountRepository;
    @InjectMocks
    private TypeAccountService typeAccountService;
    @Autowired
    private TypeModel modelType;
    @Autowired
    private Mono<TypeModel> typeModel;
    @Autowired
    private Flux<TypeModel> typeModels;

    @BeforeEach
    void init(){
        modelType = new TypeModel("AHO", "Ahorro", "A", 1 , 10.0, 1, 0, 0.0, null,null,null);
        typeModel = Mono.just(modelType);
        typeModels = typeModel.flux();
    }

    @Test
    void registerType(){
        Mockito.when(typeAccountRepository.save(modelType)).thenReturn(typeModel);
        Mono<TypeModel> response = typeAccountService.registerType(modelType);
        assertEquals(response, typeModel);
        response.subscribe(x -> {
            assertEquals(x.getCode(), modelType.getCode());
            assertEquals(x.getDescription(), modelType.getDescription());
            assertEquals(x.getCountPerson(), modelType.getCountPerson());
            assertEquals(x.getCountBusiness(), modelType.getCountBusiness());
        });
    }

    @Test
    void findAll(){
        Mockito.when(typeAccountRepository.findAll()).thenReturn(typeModels);
        Flux<TypeModel> response = typeAccountService.findAll();
        assertEquals(response, typeModels);
    }

    @Test
    void findById(){
        Mockito.when(typeAccountRepository.findById("1")).thenReturn(typeModel);
        Mono<TypeModel> response = typeAccountService.findById("1");
        assertEquals(response, typeModel);
    }

    @Test
    void updateType(){
        Mockito.when(typeAccountRepository.findById("1")).thenReturn(typeModel);
        Mockito.when(typeAccountRepository.save(modelType)).thenReturn(typeModel);
        Mono<TypeModel> response = typeAccountService.updateType(modelType, "1");
        response.subscribe(x -> {
            assertEquals(x.getCode(), modelType.getCode());
            assertEquals(x.getDescription(), modelType.getDescription());
        });
    }

    @Test
    void deleteById(){
        Mockito.when(typeAccountRepository.deleteById("1")).thenReturn(Mono.empty());
        Mono<Void> response = typeAccountService.deleteBydId("1");
        assertEquals(response, Mono.empty());
    }
}
