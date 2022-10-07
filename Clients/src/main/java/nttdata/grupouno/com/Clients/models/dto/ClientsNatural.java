package nttdata.grupouno.com.Clients.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.NaturalPerson;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ClientsNatural extends Clients {
    @Getter
    @Setter
    List<NaturalPerson> naturalPersonList;

}
