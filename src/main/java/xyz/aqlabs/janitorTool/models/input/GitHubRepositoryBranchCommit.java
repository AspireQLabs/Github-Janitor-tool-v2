package xyz.aqlabs.janitorTool.models.input;

/*
    This class is to be used within the GitHubRepositoryBranch class
    It is part of the response that GitHubRepositoryBranch gets serialized to
*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubRepositoryBranchCommit {

    @JsonProperty("sha")
    private String branchSha;

    @JsonProperty("url")
    private String branchShaUrl;
}
