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
public class GitHubBranchCommitChildAuthor {

    @JsonProperty("name")
    private String authorName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("date")
    private String date;

}
