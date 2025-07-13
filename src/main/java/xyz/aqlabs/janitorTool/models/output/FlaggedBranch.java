package xyz.aqlabs.janitorTool.models.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import xyz.aqlabs.janitorTool.models.state.BranchStatus;

@Setter
@Getter
public class FlaggedBranch {

    private String scm;

    private String name;

    private String repositoryName;

    private String url;

    private String lastCommitDate;

    private String author;

    private BranchStatus status;

}
