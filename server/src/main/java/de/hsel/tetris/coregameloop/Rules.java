package de.hsel.tetris.coregameloop;

import java.util.Map;

import static java.util.Map.entry;

public class Rules {
    // 16,666,660 ns per frame
    private final Map<Integer, Long> tickTime = Map.ofEntries(
            entry(0, 799999680L),    // equivalent of 48 frames
            entry(1, 716666380L),    // equivalent of 43 frames
            entry(2, 633333080L),    // equivalent of 38 frames
            entry(3, 549999780L),    // equivalent of 33 frames
            entry(4, 466666480L),    // equivalent of 28 frames
            entry(5, 383333180L),    // equivalent of 23 frames
            entry(6, 299999880L),    // equivalent of 18 frames
            entry(7, 216666580L),    // equivalent of 13 frames
            entry(8, 133333280L),    // equivalent of 8 frames
            entry(9, 99999960L),     // equivalent of 6 frames
            entry(10, 83333300L),    // equivalent of 5 frames
            entry(11, 83333300L),    // equivalent of 5 frames
            entry(12, 83333300L),    // equivalent of 5 frames
            entry(13, 66666640L),    // equivalent of 4 frames
            entry(14, 66666640L),    // equivalent of 4 frames
            entry(15, 66666640L),    // equivalent of 4 frames
            entry(16, 49999980L),    // equivalent of 3 frames
            entry(17, 49999980L),    // equivalent of 3 frames
            entry(18, 49999980L),    // equivalent of 3 frames
            entry(19, 33333320L),    // equivalent of 2 frames
            entry(20, 33333320L),    // equivalent of 2 frames
            entry(21, 33333320L),    // equivalent of 2 frames
            entry(22, 33333320L),    // equivalent of 2 frames
            entry(23, 33333320L),    // equivalent of 2 frames
            entry(24, 33333320L),    // equivalent of 2 frames
            entry(25, 33333320L),    // equivalent of 2 frames
            entry(26, 33333320L),    // equivalent of 2 frames
            entry(27, 33333320L),    // equivalent of 2 frames
            entry(28, 33333320L),    // equivalent of 2 frames
            entry(29, 16666660L)     // equivalent of 1 frame
    );

    public Integer getNewLevel(final Integer lines) {
        return Math.min(lines / 10, 29);
    }

    public Integer getScoreForLinesByLevel(final Integer level, final Integer removedLines) {
        return switch (removedLines) {
            case 4 -> 1200 * (level + 1);
            case 3 -> 300 * (level + 1);
            case 2 -> 100 * (level + 1);
            case 1 -> 40 * (level + 1);
            default -> 0;
        };
    }

    public long getEntryDelayTime() {
        return 166666600L; // equivalent of 10 frames
    }

    public Long getTickTimeForLevel(Integer level) {
        var cappedLevel = Math.min(Math.max(0, level), 29);
        return tickTime.getOrDefault(cappedLevel, tickTime.get(0));
    }
}
