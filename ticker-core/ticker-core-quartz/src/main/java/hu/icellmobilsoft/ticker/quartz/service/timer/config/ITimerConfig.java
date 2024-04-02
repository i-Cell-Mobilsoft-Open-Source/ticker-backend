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
package hu.icellmobilsoft.ticker.quartz.service.timer.config;

import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Timer configuration values
 * 
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
public interface ITimerConfig {

    /**
     * Constant <code>TIMER_PREFIX="ticker.timer"</code>
     */
    String TIMER_PREFIX = "ticker.timer";
    /**
     * Constant <code>KEY_DELIMITER="."</code>
     */
    String KEY_DELIMITER = ".";

    /**
     * Constant <code>CONFIG_JOB="job"</code>
     */
    String CONFIG_JOB = "job";
    /**
     * Constant <code>FAILOVER_ACTIVEJOBS="ticker.timer.activeJobs"</code>
     */
    String TIMER_ACTIVEJOBS = TIMER_PREFIX + ".activeJobs";
    /**
     * Constant <code>CONFIG_CODE="code"</code>
     */
    String CONFIG_CODE = "code";
    /**
     * Constant <code>CONFIG_CRON="cron"</code>
     */
    String CONFIG_CRON = "cron";
    /**
     * Constant <code>CONFIG_ACTIONCLASS="actionClass"</code>
     */
    String CONFIG_ACTIONCLASS = "actionClass";

    /**
     * Active job list <code>ticker.timer.activeJobs</code>
     * 
     * @return active job names
     */
    List<String> activeJobs();

    /**
     * Job code name <code>ticker.timer.job.{0}.code</code>
     * 
     * @return code name
     * @throws BaseException
     *             exception
     */
    String code() throws BaseException;

    /**
     * Cron periodicity <code>ticker.timer.job.{0}.cron</code>
     * 
     * @return cron formatted value
     * @throws BaseException
     *             exception
     */
    String cron() throws BaseException;

    /**
     * Called action class <code>ticker.timer.job.{0}.actionClass</code>
     * 
     * @return Action class full name
     * @throws BaseException
     *             exception
     */
    String actionClass() throws BaseException;
}
