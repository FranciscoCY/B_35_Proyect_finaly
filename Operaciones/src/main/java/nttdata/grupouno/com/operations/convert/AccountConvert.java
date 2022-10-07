package nttdata.grupouno.com.operations.convert;

import nttdata.grupouno.com.operations.models.MasterAccountModel;
import nttdata.grupouno.com.operations.models.dto.AccountDetailDto;
import org.springframework.stereotype.Component;

@Component
public class AccountConvert {
    public AccountDetailDto accountToDetail(MasterAccountModel model){
        AccountDetailDto detailDto = new AccountDetailDto();
        detailDto.setId(model.getId());
        detailDto.setNumberAccount(model.getNumberAccount());
        detailDto.setType(model.getType());
        detailDto.setStartDate(model.getStartDate());
        detailDto.setStatus(model.getStatus());
        detailDto.setEndDate(model.getEndDate());
        detailDto.setAmount(model.getAmount());
        detailDto.setCoinType(model.getCoinType());

        return detailDto;
    }
}
