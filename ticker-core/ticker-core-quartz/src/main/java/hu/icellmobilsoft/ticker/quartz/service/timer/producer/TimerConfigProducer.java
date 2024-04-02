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
package hu.icellmobilsoft.ticker.quartz.service.timer.producer;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.ticker.quartz.service.timer.annotation.Timer;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.TimerConfigImpl;

/**
 * Producer for creating {@code ITimerConfig}
 *
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
@ApplicationScoped
public class TimerConfigProducer {

    /**
     * Creates {@code ITimerConfig} for the injected configKey
     * 
     * @param injectionPoint
     *            injection point
     * @return ITimerConfig instance
     */
    @Produces
    @Dependent
    @Timer(configKey = "")
    public ITimerConfig getTimerConfig(InjectionPoint injectionPoint) {
        Optional<Timer> annotation = AnnotationUtil.getAnnotation(injectionPoint, Timer.class);
        String configKey = annotation.map(Timer::configKey).orElse(null);
        Instance<TimerConfigImpl> instance = CDI.current().select(TimerConfigImpl.class);
        TimerConfigImpl timerConfig = instance.get();
        try {
            timerConfig.setConfigKey(configKey);
            return timerConfig;
        } finally {
            instance.destroy(timerConfig);
        }
    }
}
