package de.codecentric.spring.boot.chaos.monkey.component;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.MemoryAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/** @author Maxime Bouchenoire */
@ExtendWith(MockitoExtension.class)
class ChaosMonkeySchedulerTest {

  @Mock private ScheduledTaskRegistrar registrar;

  @Mock private AssaultProperties config;

  @Mock private KillAppAssault killAppAssault;

  @Mock private MemoryAssault memoryAssault;

  @Test
  void shouldTolerateMissingRegistry() {
    when(config.getRuntimeAssaultCronExpression()).thenReturn("*/5 * * * * ?");
    new ChaosMonkeyScheduler(null, config, killAppAssault, memoryAssault);
    // no exception despite null injection
  }

  @Test
  void shouldRespectTheOffSetting() {
    when(config.getRuntimeAssaultCronExpression()).thenReturn("OFF");
    when(config.getKillApplicationCronExpression()).thenReturn("OFF");

    new ChaosMonkeyScheduler(registrar, config, killAppAssault, memoryAssault);
    verify(killAppAssault, never()).attack();
    verify(memoryAssault, never()).attack();
    verify(registrar, never()).scheduleCronTask(any());
  }

  @Test
  void shouldScheduleATask() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask scheduledTask = mock(ScheduledTask.class);
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(scheduledTask);

    new ChaosMonkeyScheduler(registrar, config, killAppAssault, memoryAssault);

    verify(registrar, times(2)).scheduleCronTask(argThat(hasScheduleLike(schedule)));
  }

  @Test
  void shouldScheduleANewTaskAfterAnUpdate() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask oldTask = mock(ScheduledTask.class);
    ScheduledTask newTask = mock(ScheduledTask.class);
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(oldTask, newTask);

    ChaosMonkeyScheduler cms = new ChaosMonkeyScheduler(
        registrar, config, killAppAssault, memoryAssault);
    cms.reloadConfig();

    verify(registrar, times(4)).scheduleCronTask(argThat(hasScheduleLike(schedule)));
    verify(oldTask).cancel();
  }

  @Test
  void shouldTriggerRuntimeScopeRunAttack() {
    String schedule = "*/1 * * * * ?";
    when(killAppAssault.isActive()).thenReturn(true);
    when(memoryAssault.isActive()).thenReturn(true);
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any()))
        .thenAnswer(
            iom -> {
              iom.getArgument(0, CronTask.class).getRunnable().run();
              return null;
            });

    new ChaosMonkeyScheduler(registrar, config, killAppAssault, memoryAssault);
    verify(killAppAssault).attack();
    verify(memoryAssault).attack();
  }

  @Test
  void shouldPrioritizeSpecificOverGlobalAttackCron() {
    String globalSchedule = "*/1 * * * * ?";
    String specificSchedule = "*/2 * * * * ?";
    when(config.getRuntimeAssaultCronExpression()).thenReturn(globalSchedule);
    when(config.getKillApplicationCronExpression()).thenReturn(specificSchedule);
    when(config.getMemoryCronExpression()).thenReturn("OFF"); // this will fallback on global cron

    new ChaosMonkeyScheduler(registrar, config, killAppAssault, memoryAssault);
    verify(registrar, times(1))
        .scheduleCronTask(argThat(cronTask -> cronTask.getExpression().equals(globalSchedule)));
    verify(registrar, times(1))
        .scheduleCronTask(argThat(cronTask -> cronTask.getExpression().equals(specificSchedule)));
  }

  private ArgumentMatcher<CronTask> hasScheduleLike(String schedule) {
    return cronTask -> cronTask.getExpression().equals(schedule);
  }
}
