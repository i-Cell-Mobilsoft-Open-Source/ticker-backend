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
package hu.icellmobilsoft.ticker.quartz.service.quartz.util;

import java.util.Date;

/**
 * Helper class to hold job trigger details
 *
 * @author arnold.bucher
 * @author mate.biro
 * @since 0.2.0
 */
public class QuartzJobStatus {

    private String jobKey;
    private String triggerKey;
    private Date previousFireTime;
    private Date nextFireTime;

    /**
     * Getter of jobKey
     * 
     * @return jobKey
     */
    public String getJobKey() {
        return jobKey;
    }

    /**
     * Setter of jobKey
     * 
     * @param jobKey
     *            jobKey
     */
    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    /**
     * Getter of triggerKey
     * 
     * @return triggerKey
     */
    public String getTriggerKey() {
        return triggerKey;
    }

    /**
     * Setter of triggerKey
     * 
     * @param triggerKey
     *            triggerKey
     */
    public void setTriggerKey(String triggerKey) {
        this.triggerKey = triggerKey;
    }

    /**
     * Getter of previousFireTime
     * 
     * @return previousFireTime
     */
    public Date getPreviousFireTime() {
        return previousFireTime;
    }

    /**
     * Setter of previousFireTime
     * 
     * @param previousFireTime
     *            previousFireTime
     */
    public void setPreviousFireTime(Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    /**
     * Getter of nextFireTime
     * 
     * @return nextFireTime
     */
    public Date getNextFireTime() {
        return nextFireTime;
    }

    /**
     * Setter of nextFireTime
     * 
     * @param nextFireTime
     *            nextFireTime
     */
    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "QuartzJobStatus{" + "jobKey='" + jobKey + '\'' + ", triggerKey='" + triggerKey + '\'' + ", previousFireTime=" + previousFireTime
                + ", nextFireTime=" + nextFireTime + '}';
    }
}
