package xyz.aqlabs.janitorTool.models.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
    This class is to deserialize the response when querying
    https://api.github.com/orgs/ORG-NAME
*/

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class GitHubOrganizationResponse {

    @JsonProperty("login")
    private String login;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("repos_url")
    private String reposUrl;

    @JsonProperty("description")
    private String description;

    @JsonProperty("name")
    private String name;
}
