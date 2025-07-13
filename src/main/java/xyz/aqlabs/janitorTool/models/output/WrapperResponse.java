package xyz.aqlabs.janitorTool.models.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WrapperResponse {
    private String response;
    private int responseCode;
}
