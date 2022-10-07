package nttdata.grupouno.com.operations.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.operations.models.TypeModel;
import nttdata.grupouno.com.operations.services.ITypeAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class TypeAccountControllerTest {
    @InjectMocks
    private TypeAccountController typeAccountController;
    @Mock
    private ITypeAccountService typeServices;
    @Autowired
    private TypeModel typeModel;

    @BeforeEach
    void init(){
        typeModel = new TypeModel();
        typeModel.setCode("AHO1");
        typeModel.setCodeRequired("CRE");
        typeModel.setDescription("Ahorro");
        typeModel.setStatus("A");
    }

    @Test
    void createType(){
        Mockito.when(typeServices.registerType(typeModel)).thenReturn(Mono.just(typeModel));
        typeAccountController.createType(typeModel).subscribe(
            x -> {
                assertEquals(HttpStatus.CREATED, x.getStatusCode());
                Mono<TypeModel> data = x.getBody();
                if(data == null) data = Mono.empty();
                data.subscribe(
                    y -> {
                        assertEquals(typeModel.getCode(), y.getCode());
                        assertEquals(typeModel.getCodeRequired(), y.getCodeRequired());
                        assertEquals(typeModel.getDescription(), y.getDescription());
                        assertEquals(typeModel.getStatus(), y.getStatus());
                    }
                );
            }
        );
    }

    @Test
    void createTypeAll(){
        Mockito.when(typeServices.registerType(typeModel)).thenReturn(Mono.just(typeModel));
        typeAccountController.createTypeAll(Flux.just(typeModel).toIterable());
        assertNotNull(typeModel);
    }

    @Test
    void findAll(){
        Mockito.when(typeServices.findAll()).thenReturn(Flux.just(typeModel));
        typeAccountController.findAll().subscribe(
            x -> {
                assertEquals(typeModel.getCode(), x.getCode());
                assertEquals(typeModel.getCodeRequired(), x.getCodeRequired());
                assertEquals(typeModel.getDescription(), x.getDescription());
                assertEquals(typeModel.getStatus(), x.getStatus());
            }
        );
    }

    @Test
    void findById(){
        Mockito.when(typeServices.findById("AHO1")).thenReturn(Mono.just(typeModel));
        typeAccountController.findById("AHO1").subscribe(
            x -> {
                assertEquals(HttpStatus.OK, x.getStatusCode());
                Mono<TypeModel> data = x.getBody();
                if(data == null) data = Mono.empty();
                data.subscribe(
                    y -> {
                        assertEquals(typeModel.getCode(), y.getCode());
                        assertEquals(typeModel.getCodeRequired(), y.getCodeRequired());
                        assertEquals(typeModel.getDescription(), y.getDescription());
                        assertEquals(typeModel.getStatus(), y.getStatus());
                    }
                );
            }
        );
    }

    @Test
    void update(){
        Mockito.when(typeServices.updateType(typeModel, "AHO1")).thenReturn(Mono.just(typeModel));
        typeAccountController.update(typeModel, "AHO1").subscribe(
            x -> {
                assertEquals(HttpStatus.CREATED, x.getStatusCode());
                TypeModel y = x.getBody();
                if(y == null) y = new TypeModel();
                assertEquals(typeModel.getCode(), y.getCode());
                assertEquals(typeModel.getCodeRequired(), y.getCodeRequired());
                assertEquals(typeModel.getDescription(), y.getDescription());
                assertEquals(typeModel.getStatus(), y.getStatus());
            }
        );
    }

    @Test
    void delete(){
        Mockito.when(typeServices.findById("AHO1")).thenReturn(Mono.just(typeModel));
        Mockito.when(typeServices.deleteBydId("AHO1")).thenReturn(Mono.empty());
        typeAccountController.delete("AHO1").subscribe(
            x -> {
                assertEquals(HttpStatus.NO_CONTENT, x.getStatusCode());
            }
        );
    }
}
