package xyz.aqlabs.janitorTool.sweepers;

/*
    This class is to be used to house the methods to clean
    those nasty repositories in GitHub.
*/

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.aqlabs.janitorTool.client.ClientWrapper;
import xyz.aqlabs.janitorTool.models.output.FlaggedBranch;
import xyz.aqlabs.janitorTool.models.output.FlaggedRepository;
import xyz.aqlabs.janitorTool.models.state.ToolMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


    // Entry point to the sweep...
    public void startSweep(String organization){



    }







}
