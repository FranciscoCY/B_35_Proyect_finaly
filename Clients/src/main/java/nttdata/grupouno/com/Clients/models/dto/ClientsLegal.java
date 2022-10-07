package nttdata.grupouno.com.Clients.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nttdata.grupouno.com.Clients.models.Clients;
import nttdata.grupouno.com.Clients.models.LegalPerson;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class ClientsLegal extends Clients {
    @Getter
    @Setter
    List<LegalPerson> legalPersonList;
}
