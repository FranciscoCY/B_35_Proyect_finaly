package nttdata.grupouno.com.microwallet.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RedisHash
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeDocumentModel {
    @NotNull
    private String id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
}
