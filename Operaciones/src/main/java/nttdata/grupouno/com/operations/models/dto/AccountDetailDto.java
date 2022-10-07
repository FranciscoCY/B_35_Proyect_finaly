package nttdata.grupouno.com.operations.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.MovementDetailModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.List;

@EntityScan
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailDto extends MasterAccountModel {
    @Getter
    @Setter
    private List<MovementDetailModel> movements;
}
