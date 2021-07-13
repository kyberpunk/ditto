/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.concierge.service.common;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.internal.utils.config.ConfigWithFallback;
import org.eclipse.ditto.internal.utils.config.ScopedConfig;

import com.typesafe.config.Config;

@Immutable
final class DefaultPersistenceIdsConfig implements PersistenceIdsConfig {

    private static final String CONFIG_PATH = "persistence-ids";

    private final int burst;
    private final Duration streamRequestTimeout;
    private final Duration streamIdleTimeout;
    private final Duration minBackoff;
    private final Duration maxBackoff;
    private final int maxRestarts;
    private final Duration recovery;

    private DefaultPersistenceIdsConfig(final ScopedConfig config) {
        burst = config.getPositiveIntOrThrow(ConfigValue.BURST);
        streamRequestTimeout = config.getNonNegativeAndNonZeroDurationOrThrow(ConfigValue.STREAM_REQUEST_TIMEOUT);
        streamIdleTimeout = config.getNonNegativeAndNonZeroDurationOrThrow(ConfigValue.STREAM_IDLE_TIMEOUT);
        minBackoff = config.getNonNegativeDurationOrThrow(ConfigValue.MIN_BACKOFF);
        maxBackoff = config.getNonNegativeAndNonZeroDurationOrThrow(ConfigValue.MAX_BACKOFF);
        maxRestarts = config.getNonNegativeIntOrThrow(ConfigValue.MAX_RESTARTS);
        recovery = config.getNonNegativeAndNonZeroDurationOrThrow(ConfigValue.RECOVERY);
    }

    static PersistenceIdsConfig of(final Config config) {
        return new DefaultPersistenceIdsConfig(
                ConfigWithFallback.newInstance(config, CONFIG_PATH, ConfigValue.values()));
    }

    @Override
    public int getBurst() {
        return burst;
    }

    @Override
    public Duration getStreamRequestTimeout() {
        return streamRequestTimeout;
    }

    @Override
    public Duration getStreamIdleTimeout() {
        return streamIdleTimeout;
    }

    @Override
    public Duration getMinBackoff() {
        return minBackoff;
    }

    @Override
    public Duration getMaxBackoff() {
        return maxBackoff;
    }

    @Override
    public int getMaxRestarts() {
        return maxRestarts;
    }

    @Override
    public Duration getRecovery() {
        return recovery;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof DefaultPersistenceIdsConfig) {
            final DefaultPersistenceIdsConfig that = (DefaultPersistenceIdsConfig) o;
            return Objects.equals(
                    Arrays.asList(this.burst, this.streamRequestTimeout, this.streamIdleTimeout,
                            this.minBackoff, this.maxBackoff, this.maxRestarts, this.recovery),
                    Arrays.asList(that.burst, that.streamRequestTimeout, that.streamIdleTimeout,
                            that.minBackoff, that.maxBackoff, that.maxRestarts, that.recovery));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(burst, streamRequestTimeout, streamIdleTimeout, minBackoff, maxBackoff, maxRestarts,
                recovery);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[ burst=" + burst +
                ", streamRequestTimeout" + streamRequestTimeout +
                ", streamIdleTimeout" + streamIdleTimeout +
                ", minBackoff" + minBackoff +
                ", maxBackoff" + maxBackoff +
                ", maxRestarts" + maxRestarts +
                ", recovery" + recovery +
                "]";
    }

}
