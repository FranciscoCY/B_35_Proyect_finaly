package nttdata.grupouno.com.operations.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EntityScan
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "debtClient")
public class DebtClientModel {
    @Id
    private String id;
    private String numberAccount;
    private Double amount;
    private String state;
    private String expirationDate;
    private String issueDate;
    private String paymentDate;
    private String codeCredit;
    private String paidBy;
}
