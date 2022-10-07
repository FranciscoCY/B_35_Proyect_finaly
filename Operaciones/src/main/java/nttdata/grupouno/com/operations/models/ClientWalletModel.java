package nttdata.grupouno.com.operations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientWalletModel {
    private String id;
    private String numberDocument;
    private String typeDocument;
    private String numberPhone;
    private String imeiPhone;
    private String email;
    private Double amount;
    private String targetAssociated;
}
