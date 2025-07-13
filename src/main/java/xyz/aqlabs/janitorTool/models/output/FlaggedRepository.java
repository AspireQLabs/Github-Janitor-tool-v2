package xyz.aqlabs.janitorTool.models.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlaggedRepository {

    private String scm;

    private String name;

    private String url;

    private String owner;

    private String lastActive;

}
