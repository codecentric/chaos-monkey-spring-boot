package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author Maxime Bouchenoire
 * @author Lukas Morawietz
 */
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
    Map<ChaosMonkeyRuntimeAssault, String> cronExpressions = new LinkedHashMap<>();
    for (ChaosMonkeyRuntimeAssault assault : assaults) {
      cronExpressions.put(assault, assault.getCronExpression(config));
    }
    if (!currentTasks.isEmpty()) {
      for (Iterator<Map.Entry<ChaosMonkeyRuntimeAssault, String>> iterator =
              cronExpressions.entrySet().iterator();
          iterator.hasNext(); ) {
        Map.Entry<ChaosMonkeyRuntimeAssault, String> entry = iterator.next();
        ScheduledTask task = currentTasks.get(entry.getKey());
        if (task != null) {
          if (Objects.equals(((CronTask) task.getTask()).getExpression(), entry.getValue())) {
            // no need to reschedule
            iterator.remove();
          } else {
            // cancel and reschedule below
            Logger.info(
                "Cancelling previous task for " + entry.getKey().getClass().getSimpleName());
            task.cancel();
          }
        }
      }
    }

    cronExpressions.forEach(
        (assault, expression) -> {
          if (!"OFF".equals(expression) && expression != null)
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
