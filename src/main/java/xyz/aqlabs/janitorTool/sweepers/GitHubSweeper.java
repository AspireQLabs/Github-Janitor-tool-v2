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
import xyz.aqlabs.janitorTool.client.ClientWrapper;
import xyz.aqlabs.janitorTool.models.input.GitHubBranch;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationRepository;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationRepositoryBranch;
import xyz.aqlabs.janitorTool.models.input.GitHubOrganizationResponse;
import xyz.aqlabs.janitorTool.models.output.FlaggedBranch;
import xyz.aqlabs.janitorTool.models.output.FlaggedRepository;
import xyz.aqlabs.janitorTool.models.output.WrapperResponse;
import xyz.aqlabs.janitorTool.models.state.ToolMode;
import xyz.aqlabs.janitorTool.utils.JanitorConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static xyz.aqlabs.janitorTool.utils.JanitorConstants.BRANCH;
import static xyz.aqlabs.janitorTool.utils.JanitorConstants.CLEAN;

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

        ExecutorService executor = Executors.newFixedThreadPool(5);

       // Step 3: Process a repository...
        organizationRepositories.forEach(repository -> {
            executor.submit(() -> {
                // step 4: Get Branch info from Repository
                List<GitHubOrganizationRepositoryBranch> repositoryBranches = getRepositoryBranches(repository.getBranchesUrl());
                log.info("Hi from thread {} I proccesed {}", Thread.currentThread().getName(), repository.getRepoName());
            });
        });





    }


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

    // Step 3: get all branches from the repository...
    private List<GitHubOrganizationRepositoryBranch> getRepositoryBranches(String url){
        String scrubbedUrl = url.replace(BRANCH, CLEAN);
        List<GitHubOrganizationRepositoryBranch> branches = null;
        WrapperResponse response = requester.get(scrubbedUrl);
        try{
            branches = mapper.readValue(response.getResponse(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Could not Deserialize Repository Branches API Response to obj", e);
        }
        return branches;
    }




}
