package nttdata.grupouno.com.microwallet.repositories.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@Component
public class ReactiveRedisComponent {
    @Autowired
    private ReactiveRedisOperations<String, Object> redisOperations;

    public Mono<Object> set(String key, String hashKey, Object val) {
        return redisOperations.opsForHash().put(key, hashKey, val).map(b -> val);
    }

    public Flux<Object> get(@NotNull String key){
        return redisOperations.opsForHash().values(key);
    }

    public Mono<Object> get(String key, Object hashKey) {
        return redisOperations.opsForHash().get(key, hashKey);
    }

    public Mono<Long> remove(String key, Object hashKey) {
        return redisOperations.opsForHash().remove(key, hashKey);
    }
}
