package nttdata.grupouno.com.operations.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@Data
@EntityScan
@AllArgsConstructor
@NoArgsConstructor
public class MasterAccountDto {

    private MasterAccountModel accountModel;
    private Boolean blSaldo; // indica si tiene saldo
}
