package nttdata.grupouno.com.microwallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "WalletMovement")
public class WalletMovementModel {

    @Id
    private String id;
    @NotEmpty
    private String codWallet;
    private String date;
    @NotNull
    @DecimalMin(value = "0.00", message = "Monto no negativo")
    private Double amount;
    @NotEmpty
    private Character movementType; // R:recibir E:enviar C:consulta
    @NotEmpty
    private String currency; // PEN - USD
    private String month;
    private String year;
}
