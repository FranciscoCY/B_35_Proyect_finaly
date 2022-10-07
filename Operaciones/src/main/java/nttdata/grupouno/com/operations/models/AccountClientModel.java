package nttdata.grupouno.com.operations.models;

import javax.validation.constraints.NotEmpty;

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
@Document(collection = "accountClient")
public class AccountClientModel {
    @Id
    private String id;
    @Indexed
    @NotEmpty
    private String codeClient;
    @Indexed
    private String numberAccount;
    @NotEmpty
    private String typeClient; // Persona : N - Empresa: J
    private String status; // T : Titular - F : Firmante
    private String typeAccount; // AHO% - CRE% - VIP%
    @Indexed
    private String idCartClient;
    @NotEmpty
    private String openingDate;
    private String principalAccount; // S: SI - N: NO
    private Integer pyme;
}

