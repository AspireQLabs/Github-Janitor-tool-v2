package xyz.aqlabs.janitorTool.utils;


import lombok.extern.slf4j.Slf4j;
import xyz.aqlabs.janitorTool.models.input.GitHubBranch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

// This class give helper methods for any time or date operations
@Slf4j
public class TimeKeeper {

    public static int daysSince(String isoTimestamp) {
        Instant commitInstant = Instant.parse(isoTimestamp);
        LocalDate commitDate = commitInstant.atZone(ZoneId.of("UTC")).toLocalDate();
        LocalDate today = LocalDate.now(ZoneId.of("UTC"));
        return (int) ChronoUnit.DAYS.between(commitDate, today);
    }

    public static Instant getLastActiveDate(List<GitHubBranch> branches) {
        Instant latest = null;

        for (GitHubBranch branch : branches) {
            String commitDateStr = branch.getCommitParent()
                    .getCommitChild()
                    .getCommitter()
                    .getDate();

            try {
                Instant commitInstant = ZonedDateTime
                        .parse(commitDateStr, DateTimeFormatter.ISO_DATE_TIME)
                        .toInstant();

                if (latest == null || commitInstant.isAfter(latest)) {
                    latest = commitInstant;
                }
            } catch (Exception e) {
                log.error("Could not parse date for branch {}: {}", branch.getName(), commitDateStr, e);
            }
        }

        return latest;
    }



}
