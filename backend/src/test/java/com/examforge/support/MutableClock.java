package com.examforge.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

/** Thread-safe controllable clock for deadline and expiry tests. */
public class MutableClock extends Clock {

    public static final Instant DEFAULT_INSTANT = Instant.parse("2026-12-01T08:00:00Z");

    private final AtomicReference<Instant> now;
    private final ZoneId zone;

    public MutableClock() {
        this(DEFAULT_INSTANT, ZoneOffset.UTC);
    }

    public MutableClock(Instant start, ZoneId zone) {
        this.now = new AtomicReference<>(start);
        this.zone = zone;
    }

    public void set(Instant instant) {
        now.set(instant);
    }

    public void advance(Duration duration) {
        now.updateAndGet(current -> current.plus(duration));
    }

    public void reset() {
        set(DEFAULT_INSTANT);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new MutableClock(now.get(), newZone);
    }

    @Override
    public Instant instant() {
        return now.get();
    }
}
