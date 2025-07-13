package xyz.aqlabs.janitorTool.models.input;
/*
    This class is to be used within the GitHubBranchCommitChild class
    It is part of the response that GitHubBranchCommitParent gets serialized to
*/

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubBranchCommitChild {

    @JsonProperty("author")
    private GitHubBranchCommitChildAuthor author;

    @JsonProperty("committer")
    private GitHubBranchCommitChildCommiter committer;

    @JsonProperty("message")
    private String message;





}
