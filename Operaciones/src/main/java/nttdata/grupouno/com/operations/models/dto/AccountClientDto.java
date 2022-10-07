package nttdata.grupouno.com.operations.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Data
@EntityScan
@AllArgsConstructor
@NoArgsConstructor
public class AccountClientDto {

    private String codeClient;
    private String numberAccount;
    private String idCartClient;
}
