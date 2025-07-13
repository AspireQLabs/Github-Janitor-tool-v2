package xyz.aqlabs.janitorTool.models.output;

import lombok.Builder;

@Builder
public class FlaggedBranch {

    private String scm;

    private String name;

    private String url;

    private String lastCommitDate;

    private String author;

}
