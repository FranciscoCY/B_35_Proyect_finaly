package nttdata.grupouno.com.Clients.services.implementation;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.Clients.models.NaturalPerson;
import nttdata.grupouno.com.Clients.repositories.NaturalPersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class NaturalPersonServiceImplTest {

    @Mock
    private NaturalPersonRepository naturalPersonRepository;
    @InjectMocks
    private NaturalPersonServiceImpl naturalPersonService;
    @Autowired
    private NaturalPerson naturalPerson;
    @Autowired
    private Mono<NaturalPerson> naturalPersonMono;
    @Autowired
    private Flux<NaturalPerson> naturalPersonFlux;
    @Autowired
    private NaturalPerson naturalPerson2;
    @Autowired
    private Mono<NaturalPerson> naturalPersonMono2;
    //@Autowired
    //private Flux<NaturalPerson> naturalPersonFlux2;

    @BeforeEach
    void init() {
        naturalPerson = new NaturalPerson("ae452d5c-1cc1-4ecb-8108-22294844a7a5",85671457L,1L,"Camilo","Vega Torres","masculino","camil@mail.com");
        naturalPersonMono = Mono.just(naturalPerson);
        naturalPersonFlux = naturalPersonMono.flux();

        naturalPerson2 = new NaturalPerson("ae452d5c-1cc1-4ecb-8108-22294844a7a5",85671457L,1L,"Mateo","Solis Fausto","masculino","camil@mail.com");
        naturalPersonMono2 = Mono.just(naturalPerson2);
        //naturalPersonFlux2 = naturalPersonMono2.flux();
    }

    @Test
    void createNaturalPerson() {
        Mockito.when(naturalPersonRepository.save(naturalPerson)).thenReturn(naturalPersonMono);
        Mono<NaturalPerson> response = naturalPersonService.createNaturalPerson(naturalPerson);
        assertEquals(response,naturalPersonMono);
    }

    @Test
    void listAllNaturalPerson() {
        Mockito.when(naturalPersonRepository.findAll()).thenReturn(naturalPersonFlux);
        Flux<NaturalPerson> response = naturalPersonService.listAllNaturalPerson();
        assertEquals(response,naturalPersonFlux);
    }

    @Test
    void findAllById() {
        Mockito.when(naturalPersonRepository.findById("ae452d5c-1cc1-4ecb-8108-22294844a7a5")).thenReturn(naturalPersonMono);
        Mono<NaturalPerson> response = naturalPersonService.findAllById("ae452d5c-1cc1-4ecb-8108-22294844a7a5");
        assertEquals(response,naturalPersonMono);
    }

    @Test
    void updateNaturalPerson() {
        Mockito.when(naturalPersonRepository.save(naturalPerson2)).thenReturn(naturalPersonMono2);
        Mono<NaturalPerson> response = naturalPersonService.updateNaturalPerson(naturalPerson2);
        assertEquals(response,naturalPersonMono2);
        response.subscribe(x -> {
            assertEquals(x.getId(),naturalPerson2.getId());
            assertEquals(x.getDocumentNumber(),naturalPerson2.getDocumentNumber());
            assertEquals(x.getDocumentType(),naturalPerson2.getDocumentType());
            assertEquals(x.getNames(),naturalPerson2.getNames());
            assertEquals(x.getLastNames(),naturalPerson2.getLastNames());
            assertEquals(x.getGender(),naturalPerson2.getGender());
            assertEquals(x.getMail(),naturalPerson2.getMail());
        });
    }

    @Test
    void deleteNaturalPerson() {
        Mockito.when(naturalPersonRepository.deleteById("ae452d5c-1cc1-4ecb-8108-22294844a7a5")).thenReturn(Mono.empty());
        Mono<Void> response = naturalPersonService.deleteNaturalPerson("ae452d5c-1cc1-4ecb-8108-22294844a7a5");
        assertEquals(response,Mono.empty());
    }

    @Test
    void findByDocumentNumber() {
        Mockito.when(naturalPersonRepository.findByDocumentNumber(85671457L)).thenReturn(naturalPersonMono);
        Mono<NaturalPerson> response = naturalPersonService.findByDocumentNumber(85671457L);
        assertEquals(response,naturalPersonMono);
    }

    @Test
    void findByNames() {
        Mockito.when(naturalPersonRepository.findByNames("Camilo")).thenReturn(naturalPersonFlux);
        Flux<NaturalPerson> response = naturalPersonService.findByNames("Camilo");
        assertEquals(response,naturalPersonFlux);
    }

    @Test
    void findByLastNames() {
        Mockito.when(naturalPersonRepository.findByNames("Vega Torres")).thenReturn(naturalPersonFlux);
        Flux<NaturalPerson> response = naturalPersonService.findByNames("Vega Torres");
        assertEquals(response,naturalPersonFlux);
    }
}