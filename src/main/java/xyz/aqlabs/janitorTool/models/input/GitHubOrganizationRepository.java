package xyz.aqlabs.janitorTool.models.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
    This class is to deserialize the response when querying
    https://api.github.com/orgs/ORG-NAME/repos
*/

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubOrganizationRepository {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String repoName;

    @JsonProperty("owner")
    private GitHubOrganizationRepositoryOwner owner;

    @JsonProperty("url")
    private String url;

    @JsonProperty("branches_url")
    private String branchesUrl;

    @JsonProperty("pulls_url")
    private String pullsUrl;



}
