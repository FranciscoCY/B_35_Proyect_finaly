package nttdata.grupouno.com.operations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "MovementDetail")

public class MovementDetailModel {
    @Id
    private Integer id;
    @NotEmpty
    private String numberAccount;
    private String date;
    @NotNull
    @DecimalMin(value = "0.00", message = "Monto no negativo")
    private Double amount;
    @NotEmpty
    private Character movementType; // R:retiro D:deposito C:consulta
    @NotNull
    @DecimalMin(value = "0.00", message = "Monto comision no negativo")
    private Double commission;
    @NotEmpty
    private String currency; // PEN - USD
    private String month;
    private String year;
}
