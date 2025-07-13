package xyz.aqlabs.janitorTool.models.input;

/*
    This class is to deserialize the response when querying
    https://api.github.com/orgs/ORG-NAME/repos/REPO-NAME/branches
*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubOrganizationRepositoryBranch {

    @JsonProperty("name")
    private String name;

    @JsonProperty("commit")
    private GitHubRepositoryBranchCommit commit;

    @JsonProperty("protected")
    private Boolean isProtected;


}
