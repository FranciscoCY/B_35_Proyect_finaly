package nttdata.grupouno.com.microwallet.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bootCoinDetail")
public class BootCoinDetailModel {
    @Id
    private String id;
    @NotBlank
    private String codeWalletRequest;
    @NotBlank
    private String codeWalletResponse;
    @NotNull
    private Double amountConvert;
    @NotEmpty
    private String typeOperation; //  C: Compra - V: Venta
    private Double amountTypeChange; //Monto del tipo de cambio
    private String dateRequest;
    @NotBlank
    private String modePay; // Y: Yanqui - T: Transferencia
    private String dateAccept;
    @NotBlank
    private String status; // S: Solicitado - A: Aceptado - C: Cancelado
    private String numberTransaction;
}
