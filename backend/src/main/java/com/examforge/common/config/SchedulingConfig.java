package com.examforge.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables {@code @Scheduled} jobs (attempt deadline sweeper, exam reminders). Disabled in the
 * {@code test} profile so integration tests trigger jobs explicitly.
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@ConditionalOnProperty(name = "examforge.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfig {}
