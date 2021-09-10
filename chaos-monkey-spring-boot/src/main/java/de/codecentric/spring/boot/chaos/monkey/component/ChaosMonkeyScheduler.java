package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

public class ChaosMonkeyScheduler {

  private static final Logger Logger = LoggerFactory.getLogger(ChaosMonkeyScheduler.class);

  private final ScheduledTaskRegistrar scheduler;

  private final AssaultProperties config;
  private final List<ChaosMonkeyRuntimeAssault> assaults;

  private final Map<ChaosMonkeyRuntimeAssault, ScheduledTask> currentTasks = new HashMap<>();

  public ChaosMonkeyScheduler(
      ScheduledTaskRegistrar scheduler,
      AssaultProperties config,
      List<ChaosMonkeyRuntimeAssault> assaults) {
    this.scheduler = scheduler;
    this.config = config;
    this.assaults = assaults;

    reloadConfig();
  }

  public void reloadConfig() {
    Map<ChaosMonkeyRuntimeAssault, String> cronExpressions = getCronExpressions();
    if (!currentTasks.isEmpty()) {
      removeUnchangedExpressions(cronExpressions);
      cancelOldTasks(cronExpressions);
    }

    scheduleNewTasks(cronExpressions);
  }

  private Map<ChaosMonkeyRuntimeAssault, String> getCronExpressions() {
    return assaults.stream()
        .collect(
            Collectors.toMap(Function.identity(), assault -> assault.getCronExpression(config)));
  }

  private void removeUnchangedExpressions(Map<ChaosMonkeyRuntimeAssault, String> cronExpressions) {
    cronExpressions
        .entrySet()
        .removeIf(
            entry -> {
              ScheduledTask task = currentTasks.get(entry.getKey());
              return task != null
                  && task.getTask() instanceof CronTask
                  && Objects.equals(((CronTask) task.getTask()).getExpression(), entry.getValue());
            });
  }

  private void cancelOldTasks(Map<ChaosMonkeyRuntimeAssault, String> cronExpressions) {
    cronExpressions.forEach(
        (assault, expression) -> {
          ScheduledTask task = currentTasks.remove(assault);
          if (task != null) task.cancel();
        });
  }

  private void scheduleNewTasks(Map<ChaosMonkeyRuntimeAssault, String> cronExpressions) {
    cronExpressions.forEach(
        (assault, expression) -> {
          if (expression != null && !"OFF".equals(expression))
            scheduleRuntimeAssault(scheduler, assault, expression);
        });
  }

  private void scheduleRuntimeAssault(
      ScheduledTaskRegistrar scheduler, ChaosMonkeyRuntimeAssault assault, String cron) {

    final CronTask cronTask =
        new CronTask(
            () -> {
              if (assault.isActive()) assault.attack();
            },
            cron);

    final ScheduledTask scheduledTask = scheduler.scheduleCronTask(cronTask);
    currentTasks.put(assault, scheduledTask);
  }
}
