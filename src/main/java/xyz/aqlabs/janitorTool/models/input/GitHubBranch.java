package xyz.aqlabs.janitorTool.models.input;

/*
    This class is to deserialize the response when querying
    https://api.github.com/repos/ORG-NAME/REPO-NAME/branches/BRANCH-NAME
*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubBranch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("commit")
    private GitHubBranchCommitParent commitParent;

}
