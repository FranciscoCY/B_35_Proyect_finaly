package nttdata.grupouno.com.Clients.models.dto;

import lombok.Getter;
import lombok.Setter;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.NaturalPerson;

public class NaturalClients extends Clients {

    @Getter
    @Setter
    private NaturalPerson person;
    public NaturalClients(){
        super();
        person = new NaturalPerson();
    }
}
