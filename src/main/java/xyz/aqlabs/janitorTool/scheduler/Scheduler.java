package xyz.aqlabs.janitorTool.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.aqlabs.janitorTool.sweepers.GitHubSweeper;

import java.util.List;

@Slf4j
@Component
public class Scheduler {

    @Autowired
    private GitHubSweeper gitHubSweeper;

    @Value("#{'${scan.organizations}'.split(',')}")
    private List<String> organizations;

    // Triggers Scheduler every Monday @ 5am
    @Scheduled()
    public void runScheduler(){
        log.info("Sweeper was dispatched via scheduler...");
        dispatchSweeper(organizations);
    }

    // Triggers Scheduler manually using POST
    public void runSchedulerManually(){
        log.info("Sweeper was dispatched via WebRequest...");
        dispatchSweeper(organizations);
    }

    private void dispatchSweeper(List<String> organizations){

        // We need to loop through each organization
        organizations.forEach(
                organization -> {
                    log.info("Found {} organizations to scan...", organizations.size());
                    gitHubSweeper.sweep(organization);
                }
        );

    }




}
