package nttdata.grupouno.com.Clients.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@EntityScan
@Document(collection = "clients")
@Data  @NoArgsConstructor @AllArgsConstructor
public class Clients {

    @Id
    private String id;

    @Positive(message = "El Tipo Persona debe ser diferente a cero")
    @NotNull(message = "El Tipo Persona no puede ser vacio")
    private Long idTypePerson;

    @NotNull(message = "El Id Persona no puede ser vacio")
    private String idPerson;

}
