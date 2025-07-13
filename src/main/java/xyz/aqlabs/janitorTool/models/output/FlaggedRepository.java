package xyz.aqlabs.janitorTool.models.output;

import lombok.Builder;

@Builder
public class FlaggedRepository {

    private String scm;

    private String name;

    private String url;

    private String lastActive;

}
