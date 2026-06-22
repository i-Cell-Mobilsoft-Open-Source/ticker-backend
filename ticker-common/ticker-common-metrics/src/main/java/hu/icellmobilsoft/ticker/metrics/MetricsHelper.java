/*-
 * #%L
 * Ticker
 * %%
 * Copyright (C) 2024 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.ticker.metrics;

import java.time.Duration;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Metrics helper class
 *
 * @author speter555
 * @since 1.1.0
 */
@Dependent
public class MetricsHelper {

    @Inject
    MeterRegistry meterRegistry;

    /**
     * Add Gauge type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param gaugeValue
     *            value for gauge
     * @param tagKey
     *            tag's key
     * @param tagValue
     *            tag's value
     */
    public void addGaugeMetric(String metadataName, String metadataDescription, long gaugeValue, String tagKey, String tagValue) {
        Gauge.builder(metadataName, () -> gaugeValue)
                .description(metadataDescription)
                .tag(tagKey, tagValue)
                .register(meterRegistry);
    }

    /**
     * Add Gauge type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param gaugeValue
     *            value for gauge
     * @param tags
     *            tags for metric (key-value pairs)
     */
    public void addGaugeMetric(String metadataName, String metadataDescription, int gaugeValue, String... tags) {
        Gauge.builder(metadataName, () -> gaugeValue)
                .description(metadataDescription)
                .tags(tags)
                .register(meterRegistry);
    }

    /**
     * Add Counter type metric (increment by one)
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param tagKey
     *            tag's key
     * @param tagValue
     *            tag's value
     */
    public void addCounterIncOneMetric(String metadataName, String metadataDescription, String tagKey, String tagValue) {
        Counter.builder(metadataName)
                .description(metadataDescription)
                .tag(tagKey, tagValue)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Add Counter type metric (increment by one)
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param tags
     *            tags for metric (key-value pairs)
     */
    public void addCounterIncOneMetric(String metadataName, String metadataDescription, String... tags) {
        Counter.builder(metadataName)
                .description(metadataDescription)
                .tags(tags)
                .register(meterRegistry)
                .increment();
    }

    /**
     * Add Timer type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param duration
     *            duration for timer
     * @param tagKey
     *            tag's key
     * @param tagValue
     *            tag's value
     */
    public void addTimerMetric(String metadataName, String metadataDescription, Duration duration, String tagKey, String tagValue) {
        Timer.builder(metadataName)
                .description(metadataDescription)
                .tag(tagKey, tagValue)
                .register(meterRegistry)
                .record(duration);
    }

    /**
     * Add Timer type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param duration
     *            duration for timer
     * @param tags
     *            tags for metric (key-value pairs)
     */
    public void addTimerMetric(String metadataName, String metadataDescription, Duration duration, String... tags) {
        Timer.builder(metadataName)
                .description(metadataDescription)
                .tags(tags)
                .register(meterRegistry)
                .record(duration);
    }

    /**
     * Add Historgram type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param number
     *            number value to historgram updated number
     * @param tagKey
     *            tag's key
     * @param tagValue
     *            tag's value
     */
    public void addHistorgramMetric(String metadataName, String metadataDescription, long number, String tagKey, String tagValue) {
        DistributionSummary.builder(metadataName)
                .description(metadataDescription)
                .tag(tagKey, tagValue)
                .register(meterRegistry)
                .record(number);
    }

    /**
     * Add Historgram type metric
     *
     * @param metadataName
     *            metadata's name
     * @param metadataDescription
     *            metadata's description
     * @param number
     *            number value to historgram updated number
     * @param tags
     *            tags for metric
     */
    public void addHistorgramMetric(String metadataName, String metadataDescription, long number, String... tags) {
        DistributionSummary.builder(metadataName)
                .description(metadataDescription)
                .tags(tags)
                .register(meterRegistry)
                .record(number);
    }

}
