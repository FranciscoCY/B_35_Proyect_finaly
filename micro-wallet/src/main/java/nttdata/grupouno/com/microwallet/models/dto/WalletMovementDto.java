package nttdata.grupouno.com.microwallet.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletMovementDto {
    @NotEmpty
    private String numberPhone;
    @DecimalMin(value = "0.00", message = "Monto no negativo")
    private Double amount;
    @NotEmpty
    private Character movementType; // R:recibir E:enviar C:consulta
    @NotEmpty
    private String currency;

}
