package nttdata.grupouno.com.microwallet.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "typeChange")
public class TypeChangeModel {
    @Id
    private String id;
    @NotBlank
    private String date;
    @NotEmpty
    private Double amountBuy;
    @NotEmpty
    private Double amountSale;
    @NotEmpty
    private String typeCoin;
}
