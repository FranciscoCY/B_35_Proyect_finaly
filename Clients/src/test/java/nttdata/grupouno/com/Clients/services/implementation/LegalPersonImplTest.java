package nttdata.grupouno.com.Clients.services.implementation;

import lombok.RequiredArgsConstructor;
import nttdata.grupouno.com.Clients.models.LegalPerson;
import nttdata.grupouno.com.Clients.repositories.LegalPersonRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class LegalPersonImplTest {

    @Mock
    private LegalPersonRepository legalPersonRepository;

    @InjectMocks
    private LegalPersonImpl legalPersonService;

    private LegalPerson legalPerson;

    private Mono<LegalPerson> legalPersonMono;

    private Flux<LegalPerson> legalPersonFlux;

    @BeforeEach
    void init() {
        legalPerson = new LegalPerson("91138f42-1c69-49c2-b49c-40dfe411d70c",20158585671457L,"IMPORTADORA IMP","imp@mail.com");
        legalPersonMono = Mono.just(legalPerson);
        legalPersonFlux = legalPersonMono.flux();
    }

    @Test
    void listAllLegalPerson() {
        Mockito.when(legalPersonRepository.findAll()).thenReturn(legalPersonFlux);
        Flux<LegalPerson> response = legalPersonService.listAllLegalPerson();
        assertEquals(response,legalPersonFlux);
    }

    @Test
    void findAllById() {
        Mockito.when(legalPersonRepository.findById("91138f42-1c69-49c2-b49c-40dfe411d70c")).thenReturn(legalPersonMono);
        Mono<LegalPerson> response = legalPersonService.findAllById("91138f42-1c69-49c2-b49c-40dfe411d70c");
        assertEquals(response,legalPersonMono);
    }

    @Test
    void createLegalPerson() {
        Mockito.when(legalPersonRepository.save(legalPerson)).thenReturn(legalPersonMono);
        Mono<LegalPerson> response = legalPersonService.createLegalPerson(legalPerson);
        assertEquals(response,legalPersonMono);
        response.subscribe(x -> {
           assertEquals(x.getId(),legalPerson.getId());
           assertEquals(x.getRuc(),legalPerson.getRuc());
           assertEquals(x.getBusinessName(),legalPerson.getBusinessName());
           assertEquals(x.getMail(),legalPerson.getMail());
        });
    }

    @Test
    void findByRuc() {
        Mockito.when(legalPersonRepository.findByRuc(20158585671457L)).thenReturn(legalPersonMono);
        Mono<LegalPerson> response = legalPersonService.findByRuc(20158585671457L);
        assertEquals(response,legalPersonMono);
    }

    @Test
    void findByBusinessName() {
        Mockito.when(legalPersonRepository.findById("IMPORTADORA IMP")).thenReturn(legalPersonMono);
        Mono<LegalPerson> response = legalPersonService.findAllById("IMPORTADORA IMP");
        assertEquals(response,legalPersonMono);
    }
}