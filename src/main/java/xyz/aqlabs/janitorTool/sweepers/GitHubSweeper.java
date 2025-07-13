package xyz.aqlabs.janitorTool.sweepers;

/*
    This class is to be used to house the methods to clean
    those nasty repositories in GitHub.
*/

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import xyz.aqlabs.janitorTool.client.ClientWrapper;
import xyz.aqlabs.janitorTool.models.input.GitHubBranch;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationRepository;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationRepositoryBranch;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationResponse;
import xyz.aqlabs.janitorTool.models.output.FlaggedBranch;
import xyz.aqlabs.janitorTool.models.output.FlaggedRepository;
import xyz.aqlabs.janitorTool.models.output.WrapperResponse;
import xyz.aqlabs.janitorTool.models.state.BranchStatus;
import xyz.aqlabs.janitorTool.models.state.ToolMode;
import xyz.aqlabs.janitorTool.utils.JanitorConstants;
import xyz.aqlabs.janitorTool.utils.TimeKeeper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static xyz.aqlabs.janitorTool.utils.JanitorConstants.*;

@Slf4j
@Component
public class GitHubSweeper {

    @Getter
    List<FlaggedBranch> flaggedBranches = Collections.synchronizedList(new ArrayList<>());

    @Getter
    List<FlaggedRepository> flaggedRepositoryList = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    ClientWrapper requester;

    @Value("#{'${sweeper.branches.ignore}'.split(',')}")
    private List<String> branchesToIgnore;

    @Value("${sweeper.stale_branch.days}")
    private int numberOfDaysABranchStale;

    @Value("${sweeper.stale_repository.days}")
    private int numberOfDaysARepositoryStale;

    @Value("${sweeper.mode}")
    private ToolMode mode;

    ObjectMapper mapper = new ObjectMapper();


    // Entry point to the sweep...
    public void sweep(String organization) {
        // Step 1: get the organization data...
        GitHubOrganizationResponse gitHubOrganizationResponse = getOrganization(organization);

        // Step 2: get all the repositories within the organization...
        List<GitHubOrganizationRepository> organizationRepositories =
                getOrganizationRepositories(gitHubOrganizationResponse.getReposUrl());

        // Step 3: set up worker threads
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        for (GitHubOrganizationRepository repository : organizationRepositories) {
            futures.add(executor.submit(() -> {
                try {
                    // Step 4: Get branches info from repository
                    List<GitHubOrganizationRepositoryBranch> repositoryBranches =
                            getRepositoryBranches(repository);

                    List<GitHubBranch> branchObjects = new ArrayList<>();
                    for (GitHubOrganizationRepositoryBranch repositoryBranch : repositoryBranches) {
                        GitHubBranch branch = getGitHubBranch(organization, repository, repositoryBranch.getName());
                        if (branch != null) {
                            branchObjects.add(branch);
                        } else {
                            log.warn("Branch {} in repo {} returned null",
                                    repositoryBranch.getName(), repository.getRepoName());
                        }
                    }

                    // Step 5: Process branches
                    processBranch(branchObjects, repository);
                } catch (Exception e) {
                    log.error("Error sweeping repository {}", repository.getRepoName(), e);
                }
            }));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                log.warn("Executor did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            log.error("Sweeping interrupted", e);
            Thread.currentThread().interrupt();
        }
    }


    // ---- Getters for API DATA ---->
    private GitHubOrganizationResponse getOrganization(String organization){
        log.info("Getting Organization data for {}...", organization);
        GitHubOrganizationResponse organizationResponse = null;
        WrapperResponse response = requester.get(JanitorConstants.GET_ORG_URL.formatted(organization));
        try {
            organizationResponse = mapper.readValue(response.getResponse(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not Deserialize Organization API Response to obj", e);
        }
        log.info("{} Data found....", organization);
        return organizationResponse;
    }

    private List<GitHubOrganizationRepository> getOrganizationRepositories(String url){
        log.info("Getting Organization Repos from url: {}", url);
        List<GitHubOrganizationRepository> repositories = null;
        WrapperResponse response = requester.get(url);
        try{
            repositories = mapper.readValue(response.getResponse(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not Deserialize Organization Repository API Response to obj", e);
        }
        assert repositories != null;
        log.info("Found {} Repositories in organization", repositories.size());
        return repositories;
    }

    private List<GitHubOrganizationRepositoryBranch> getRepositoryBranches(GitHubOrganizationRepository repository){
        log.info("Getting Repository branches data for repository: {}", repository.getRepoName());
        String scrubbedUrl = repository.getBranchesUrl().replace(BRANCH, CLEAN);
        List<GitHubOrganizationRepositoryBranch> branches = null;
        WrapperResponse response = requester.get(scrubbedUrl);
        try{
            branches = mapper.readValue(response.getResponse(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not Deserialize Repository Branches API Response to obj", e);
        }
        log.info("Found {} BRANCHES in REPOSITORY {}", branches.size(), repository.getRepoName());
        return branches;
    }

    private GitHubBranch getGitHubBranch(String organization, GitHubOrganizationRepository repository, String name) {
        GitHubBranch branch = null;
        WrapperResponse response = requester.get(GET_BRANCH_URL.formatted(organization, repository.getRepoName(), name));
        try{
            branch = mapper.readValue(response.getResponse(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not Deserialize Branches API Response to obj", e);
        }
        return branch;
    }


    // ---- Helper Methods ---->
    private void processBranch(List<GitHubBranch> branches, GitHubOrganizationRepository repository){

        // determine if repo is stale
        handleRepository(branches, repository);

        // filter excluded branches
        branches.removeIf(branch -> branchesToIgnore.contains(branch.getName()));

        // process remaining branches
        branches.forEach( branch -> {
            String lastCommitTimeStamp = branch.getCommitParent().getCommitChild().getCommitter().getDate();
            int daysSinceLastCommit = TimeKeeper.daysSince(lastCommitTimeStamp);
            log.info("BRANCH: {} | REPOSITORY: {} is {} days old", branch.getName(), repository.getRepoName(), daysSinceLastCommit);

            if(daysSinceLastCommit >= numberOfDaysABranchStale ){
                handleBranch(branch, repository);
            }


        });
    }

    private void handleBranch(GitHubBranch branch, GitHubOrganizationRepository repository){
        FlaggedBranch flaggedBranch = new FlaggedBranch();
        flaggedBranch.setScm(GITHUB);
        flaggedBranch.setName(branch.getName());
        flaggedBranch.setRepositoryName(repository.getRepoName());
        flaggedBranch.setAuthor(branch.getCommitParent().getCommitChild().getAuthor().getAuthorName());
        flaggedBranch.setLastCommitDate(branch.getCommitParent().getCommitChild().getCommitter().getDate());
        flaggedBranch.setUrl(GET_BRANCH_URL.formatted(repository.getOwner(), repository.getRepoName(), branch.getName()));

        if(mode.equals(ToolMode.REPORTING)){
            flaggedBranch.setStatus(BranchStatus.STALE);
        } else if (mode.equals(ToolMode.CLEANUP)) {
            flaggedBranch.setStatus(deleteBranch(branch, repository));
        }

        // add branch to global list of flagged branches
        log.info("Adding BRANCH: {} to flagged branches list", branch.getName());
        flaggedBranches.add(flaggedBranch);
    }

    private void handleRepository(List<GitHubBranch> branches, GitHubOrganizationRepository repository) {
        boolean allBranchesStale = true;
        Instant latestTimeStamp = TimeKeeper.getLastActiveDate(branches);

        for (GitHubBranch branch : branches) {
            String commitDate = branch.getCommitParent()
                    .getCommitChild()
                    .getCommitter()
                    .getDate();

            int branchDaysOld = TimeKeeper.daysSince(commitDate);

            log.info("Repo: {} | Branch: {} is {} days old",
                    repository.getRepoName(), branch.getName(), branchDaysOld);

            if (branchDaysOld < numberOfDaysARepositoryStale) {
                allBranchesStale = false;
                log.info("Repo: {} marked ACTIVE because branch {} is only {} days old",
                        repository.getRepoName(), branch.getName(), branchDaysOld);
                break;
            }
        }

        if (allBranchesStale) {
            log.info("REPOSITORY {} flagged as STALE - all branches are old.", repository.getRepoName());
            FlaggedRepository flaggedRepo = new FlaggedRepository();
            flaggedRepo.setScm(GITHUB);
            flaggedRepo.setName(repository.getRepoName());
            flaggedRepo.setUrl(repository.getUrl());
            flaggedRepo.setOwner(repository.getOwner().getLogin());
            flaggedRepo.setLastActive(latestTimeStamp.toString());
            flaggedRepositoryList.add(flaggedRepo);
        } else {
            log.info("REPOSITORY {} is ACTIVE - at least one branch is recent.", repository.getRepoName());
        }
    }

    private BranchStatus deleteBranch(GitHubBranch branch, GitHubOrganizationRepository repository){
        WrapperResponse response = requester.delete(DELETE_BRANCH_URL.formatted(repository.getUrl(), branch.getName()));
        if(response.getResponseCode() == 204){
            log.info("Deleted BRANCH {} | REPOSITORY {}", branch.getName(), repository.getRepoName());
            return BranchStatus.DELETED;
        } else {
            return BranchStatus.ERROR;
        }
    }

}
