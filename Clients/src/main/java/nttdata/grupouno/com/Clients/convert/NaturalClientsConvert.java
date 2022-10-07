package nttdata.grupouno.com.Clients.convert;

import org.springframework.stereotype.Component;

import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.dto.NaturalClients;

@Component
public class NaturalClientsConvert {

    public NaturalClients convertNaturalClient(Clients clients){
        NaturalClients naturalClients = new NaturalClients();
        naturalClients.setId(clients.getId());
        naturalClients.setIdTypePerson(clients.getIdTypePerson());
        naturalClients.setIdPerson(clients.getIdPerson());

        return naturalClients;
    }

    public Clients convertClient(NaturalClients clients){
        Clients naturalClients = new Clients();
        naturalClients.setId(clients.getId());
        naturalClients.setIdTypePerson(clients.getIdTypePerson());
        naturalClients.setIdPerson(clients.getIdPerson());

        return naturalClients;
    }
}
