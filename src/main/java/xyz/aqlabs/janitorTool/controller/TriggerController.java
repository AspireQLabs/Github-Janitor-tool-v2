package xyz.aqlabs.janitorTool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.aqlabs.janitorTool.scheduler.Scheduler;

@RestController
public class TriggerController {

    @Autowired
    Scheduler scheduler;

    @PostMapping("/trigger")
    public ResponseEntity<String> runSweeper(){
        scheduler.runSchedulerManually();
        return ResponseEntity.ok("Started Sweeping");
    }


}
