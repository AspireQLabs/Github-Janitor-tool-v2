package xyz.aqlabs.janitorTool.models.input;

/*
    This class is to be used within the GitHubBranch class
    It is part of the response that GitHubBranch gets serialized to
*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubBranchCommitParent {

    @JsonProperty("sha")
    private String sha;

    @JsonProperty("commit")
    private GitHubBranchCommitChild commitChild;

}
