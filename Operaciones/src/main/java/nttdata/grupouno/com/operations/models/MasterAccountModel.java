package nttdata.grupouno.com.operations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@EntityScan
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "masterAccount")
public class MasterAccountModel {
    @Id
    private String id;
    @NotEmpty
    private String numberAccount;
    @NotNull
    private TypeModel type; // Ahorro - Cuenta Corriente - Plazo Fijo / Personal - Empresarial
    @NotEmpty
    private String startDate;
    @NotEmpty
    private String status; // A:Activo - I:Inactivo - C:Cancelado
    private String endDate;
    @NotNull
    @DecimalMin(value = "0.00", message = "El monto no puede ser negativo")
    private Double amount;
    @NotEmpty
    private String coinType; // PEN - USD
}
