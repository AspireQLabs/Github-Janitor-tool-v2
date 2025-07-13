package xyz.aqlabs.janitorTool.models.state;

// This defines what mode the tool will run in
// CLEANUP - cleans up repos by closing PRs and deleting Branches
// REPORTING - scans the repos to give a report on what is stale

public enum ToolMode {
    CLEANUP,
    REPORTING
}
