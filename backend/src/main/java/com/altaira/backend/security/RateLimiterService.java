package com.altaira.backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Allocates a token bucket per submission or login key to limit repeated requests. */
@Service
public class RateLimiterService {

    /** Buckets. */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    /** Lead capacity. */
    private final int leadCapacity;
    /** Lead refill per minute. */
    private final int leadRefillPerMinute;
    /** Login capacity. */
    private final int loginCapacity;
    /** Login refill per five minutes. */
    private final int loginRefillPerFiveMinutes;

    /**
     * Constructs {@code RateLimiterService} using the supplied lead capacity, lead refill per minute, login capacity and remaining fields.
     *
     * @param leadCapacity lead capacity
     * @param leadRefillPerMinute lead refill per minute
     * @param loginCapacity login capacity
     * @param loginRefillPerFiveMinutes login refill per five minutes
     */
    public RateLimiterService(
            @Value("${altaira.rate-limit.leads.capacity:10}") int leadCapacity,
            @Value("${altaira.rate-limit.leads.refill-per-minute:10}") int leadRefillPerMinute,
            @Value("${altaira.rate-limit.login.capacity:5}") int loginCapacity,
            @Value("${altaira.rate-limit.login.refill-per-five-minutes:5}") int loginRefillPerFiveMinutes
    ) {
        this.leadCapacity = leadCapacity;
        this.leadRefillPerMinute = leadRefillPerMinute;
        this.loginCapacity = loginCapacity;
        this.loginRefillPerFiveMinutes = loginRefillPerFiveMinutes;
    }

    /**
     * Resolves bucket.
     *
     * @param ip ip
     * @return the bucket
     */
    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent("lead:" + ip, this::newLeadBucket);
    }

    /**
     * Resolves login bucket.
     *
     * @param key key used to identify the item
     * @return the bucket
     */
    public Bucket resolveLoginBucket(String key) {
        return buckets.computeIfAbsent("login:" + key, this::newLoginBucket);
    }

    /**
     * Creates the rate-limit bucket applied to public enquiry submissions.
     *
     * @param key key used to identify the item
     * @return the bucket
     */
    private Bucket newLeadBucket(String key) {
        Bandwidth limit = Bandwidth.classic(
                leadCapacity,
                Refill.greedy(leadRefillPerMinute, Duration.ofMinutes(1))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Creates the rate-limit bucket used to throttle sign-in attempts.
     *
     * @param key key used to identify the item
     * @return the bucket
     */
    private Bucket newLoginBucket(String key) {
        Bandwidth limit = Bandwidth.classic(
                loginCapacity,
                Refill.greedy(loginRefillPerFiveMinutes, Duration.ofMinutes(5))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
