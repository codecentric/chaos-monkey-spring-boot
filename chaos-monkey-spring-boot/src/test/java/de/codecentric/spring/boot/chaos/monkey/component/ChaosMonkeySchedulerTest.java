/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.CpuAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.MemoryAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChaosMonkeySchedulerTest {

    private static final Runnable EMPTY_RUNNABLE = () -> {
    };

    @Mock
    private ScheduledTaskRegistrar registrar;

    @Mock
    private AssaultProperties config;

    @Mock
    private KillAppAssault killAppAssault;

    @Mock
    private MemoryAssault memoryAssault;

    @Mock
    private CpuAssault cpuAssault;

    @Test
    void shouldRespectTheOffSetting() {
        when(memoryAssault.getCronExpression(any())).thenReturn("OFF");
        when(killAppAssault.getCronExpression(any())).thenReturn("OFF");
        when(cpuAssault.getCronExpression(any())).thenReturn("OFF");

        new ChaosMonkeyScheduler(registrar, config, Arrays.asList(memoryAssault, killAppAssault, cpuAssault));
        verify(killAppAssault, never()).attack();
        verify(memoryAssault, never()).attack();
        verify(cpuAssault, never()).attack();
        verify(registrar, never()).scheduleCronTask(any());
    }

    @Test
    void shouldScheduleATask(@Mock ScheduledTask scheduledTask) {
        when(memoryAssault.getCronExpression(any())).thenReturn("*/1 * * * * ?");
        when(registrar.scheduleCronTask(any())).thenReturn(scheduledTask);

        new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));

        verify(registrar).scheduleCronTask(argThat(hasScheduleLike("*/1 * * * * ?")));
    }

    @Test
    void shouldNotScheduleNewTasksAfterUnrelatedUpdate(@Mock(name = "memory") ScheduledTask oldTask) {
        when(oldTask.getTask()).thenReturn(new CronTask(EMPTY_RUNNABLE, "*/1 * * * * ?"));
        when(memoryAssault.getCronExpression(any())).thenReturn("*/1 * * * * ?");
        when(registrar.scheduleCronTask(any())).thenReturn(oldTask);

        ChaosMonkeyScheduler cms = new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));
        verify(registrar).scheduleCronTask(argThat(hasScheduleLike("*/1 * * * * ?")));

        reset(registrar);

        cms.reloadConfig();

        verify(registrar, never()).scheduleCronTask(argThat(hasScheduleLike("*/1 * * * * ?")));
    }

    @Test
    void shouldTriggerRuntimeScopeRunAttack() {
        when(memoryAssault.isActive()).thenReturn(true);
        when(memoryAssault.getCronExpression(any())).thenReturn("*/1 * * * * ?");
        when(registrar.scheduleCronTask(any())).thenAnswer(iom -> {
            iom.getArgument(0, CronTask.class).getRunnable().run();
            return null;
        });

        new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));
        verify(memoryAssault).attack();
    }

    @Test
    void shouldRescheduleOnlyChangedTasks(@Mock ScheduledTask newTask,
                                          @Mock(name = "memory") ScheduledTask memoryTask,
                                          @Mock(name = "killApp") ScheduledTask oldTask) {
        when(memoryTask.getTask()).thenReturn(new CronTask(EMPTY_RUNNABLE, "*/1 * * * * ?"));
        when(oldTask.getTask()).thenReturn(new CronTask(EMPTY_RUNNABLE, "*/2 * * * * ?"));
        when(memoryAssault.getCronExpression(any())).thenReturn("*/1 * * * * ?");
        when(killAppAssault.getCronExpression(any())).thenReturn("*/2 * * * * ?");
        when(registrar.scheduleCronTask(argThat(hasScheduleLike("*/1 * * * * ?")))).thenReturn(memoryTask);
        when(registrar.scheduleCronTask(argThat(hasScheduleLike("*/2 * * * * ?")))).thenReturn(oldTask, newTask);

        ChaosMonkeyScheduler cms = new ChaosMonkeyScheduler(registrar, config, Arrays.asList(memoryAssault, killAppAssault));
        verify(registrar).scheduleCronTask(argThat(hasScheduleLike("*/1 * * * * ?")));
        verify(registrar).scheduleCronTask(argThat(hasScheduleLike("*/2 * * * * ?")));

        reset(registrar);
        when(killAppAssault.getCronExpression(any())).thenReturn("*/3 * * * * ?");

        cms.reloadConfig();
        verify(registrar).scheduleCronTask(argThat(hasScheduleLike("*/3 * * * * ?")));
        verify(memoryTask, never()).cancel();
        verify(oldTask).cancel();
    }

    private ArgumentMatcher<CronTask> hasScheduleLike(String schedule) {
        return cronTask -> cronTask != null && cronTask.getExpression().equals(schedule);
    }
}
