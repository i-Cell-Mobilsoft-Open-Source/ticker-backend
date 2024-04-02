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
package hu.icellmobilsoft.ticker.quartz.service.quartz.health;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.collections4.CollectionUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.ticker.quartz.service.quartz.util.QuartzJobStatus;

/**
 * Class to get Job trigger details
 * 
 * @author arnold.bucher
 * @author czenczl
 * @author mate.biro
 * @since 0.2.0
 */
@Dependent
public class TickerHealthCheckStatus {

    /**
     * get the scheduler job summary
     *
     * @return job status list
     */
    public List<QuartzJobStatus> getSchedulerJobSummary() {
        List<QuartzJobStatus> jobStatuses = new ArrayList<>();

        try {
            Scheduler scheduler = CDI.current().select(Scheduler.class).get();
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();

            if (CollectionUtils.isEmpty(triggerGroupNames)) {
                return Collections.emptyList();
            }

            for (String triggerGroupName : triggerGroupNames) {
                Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroupName));
                jobStatuses.addAll(
                        triggerKeys.stream() //
                                .map(triggerKey -> this.getQuartzJobStatus(triggerKey, scheduler)) //
                                .toList());
            }

        } catch (SchedulerException e) {
            Logger.getLogger(TickerHealthCheckStatus.class).error("Cannot get scheduler job summary: ", e);
        }

        return jobStatuses;
    }

    private QuartzJobStatus getQuartzJobStatus(TriggerKey triggerKey, org.quartz.Scheduler scheduler) {
        try {
            QuartzJobStatus quartzJobStatus = new QuartzJobStatus();
            Trigger trigger = scheduler.getTrigger(triggerKey);

            quartzJobStatus.setJobKey(trigger.getJobKey().getName());
            quartzJobStatus.setTriggerKey(trigger.getKey().getName());
            quartzJobStatus.setPreviousFireTime(trigger.getPreviousFireTime());
            quartzJobStatus.setNextFireTime(trigger.getNextFireTime());

            return quartzJobStatus;

        } catch (SchedulerException e) {
            Logger.getLogger(TickerHealthCheckStatus.class).error("Error occurred while establishing connection: " + e.getLocalizedMessage(), e);
            return null;
        }
    }
}
