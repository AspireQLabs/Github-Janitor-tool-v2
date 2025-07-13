package xyz.aqlabs.janitorTool.utils;

// Constants for the tool's operations
public class JanitorConstants {

    // Endpoint to query GitHub API Organization endpoint
    // Param: %s (String) - Organization Name
    public static final String GET_ORG_URL = "https://api.github.com/orgs/%s";

    // Endpoint to query GitHub API Repo Branch endpoint
    // Param 1: %s (String) - Organization Name
    // Param 2: %s (String) - Repository Name
    // Param 3: %s (String) - Branch Name
    public static final String GET_BRANCH_URL = "https://api.github.com/repos/%s/%s/branches/%s";


    // Used to clean up urls
    public static final String BRANCH = "{/branch}";
    public static final String PULLS = "{/number}";
    public static final String CLEAN = "";

    // Constant Values
    public static final String GITHUB = "GitHub";
    public static final String BITBUCKET = "BitBucket";



    private JanitorConstants(){}
}
