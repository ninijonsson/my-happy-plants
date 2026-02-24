package se.mau.myhappyplants.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for setting up caching in the application.
 * This class defines and configures a {@link CacheManager} bean using Caffeine as the caching provider.
 *
 * The cache is configured with the following specifications:
 * - Expiration time after write: 24 hours.
 * - Maximum cache size: 100 entries.
 * - Statistics recording enabled to monitor cache performance.
 */

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100).recordStats());
        return cacheManager;
    }
}
