package us.pax.basil.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Value("${aws.threads.min:1}")
    private Integer minimum;
    @Value("${aws.threads.max:10}")
    private Integer maximum;
    @Value("${aws.threads.queueLimit:100}")
    private Integer queueCapacity;

    @Bean(name = "awsExecutor")
    public Executor awsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(minimum);
        executor.setMaxPoolSize(maximum);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("AWSAsync-");
        executor.initialize();
        return executor;
    }
}
