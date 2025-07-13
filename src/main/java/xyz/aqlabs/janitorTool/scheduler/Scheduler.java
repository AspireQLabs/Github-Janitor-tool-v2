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

                    log.info("Found {} branches", gitHubSweeper.getFlaggedBranches().size());
                    gitHubSweeper.getFlaggedBranches().forEach(
                            flaggedBranch ->  {
                                log.info("----------------------------------");
                                log.info("Branch Name: {}", flaggedBranch.getName());
                                log.info("Repository Name: {}",flaggedBranch.getRepositoryName());
                                log.info("Author Name: {}", flaggedBranch.getAuthor());
                                log.info("Last Commit: {}", flaggedBranch.getLastCommitDate());
                                log.info("Branch STATUS: {}", flaggedBranch.getStatus());
                            });

                    log.info("Found {} flagged Repositories", gitHubSweeper.getFlaggedRepositoryList().size());
                    gitHubSweeper.getFlaggedRepositoryList().forEach(flaggedRepository -> {
                                log.info("-----------------------------------");
                                log.info("Scm: {}", flaggedRepository.getScm());
                                log.info("Repo Name: {}", flaggedRepository.getName());
                                log.info("URL: {}", flaggedRepository.getUrl());
                                log.info("Last Activity Date: {}", flaggedRepository.getLastActive());
                            });
                }
        );

    }




}
