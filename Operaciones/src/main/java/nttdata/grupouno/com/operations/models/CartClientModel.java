package nttdata.grupouno.com.operations.models;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@EntityScan
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cartClient")
public class CartClientModel {
    @Id
    private String id;
    private String cartNumber; // Encripted AES
    @Indexed
    private String hashCartNumber;
    @NotBlank
    private String codeClient;
    @NotBlank
    private String typeCart; // AHO: Debito - CRE: Credito
    private String codeStatus; // A: Activo - C: Cancelado
    private String startDate;
    private String endDate;
}
