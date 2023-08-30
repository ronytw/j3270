package com.github.filipesimoes.j3270.protocol;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class Status {
    private interface Coded {
        char getCode();
    }

    @Getter
    @AllArgsConstructor
    public enum KeyboardLock implements Coded {
        UNLOCKED('U'),
        LOCKED('L'),
        LOCKED_FOR_ERROR('E');

        private final char code;
    }

    @Getter
    @AllArgsConstructor
    public enum Formatting implements Coded {
        FORMATTED('F'),
        UNFORMATTED('U');

        private final char code;
    }

    @Getter
    @AllArgsConstructor
    public enum FieldProtection implements Coded {
        UNPROTECTED('U'),
        PROTECTED('P');

        private final char code;
    }

    @Getter
    @AllArgsConstructor
    public enum ConnectionStatus implements Coded {
        NOT_CONNECTED('N'),
        CONNECTED('C');

        private final char code;
    }

    @Getter
    @AllArgsConstructor
    public enum EmulatorMode implements Coded {
        NOT_CONNECTED('N'),
        NVT_CHARACTER('C'),
        NVT_LINE('L'),
        PENDING('P'),
        MODE_3270('I');

        private final char code;
    }

    /*
     * Prompt line definition: https://x3270.miraheze.org/wiki/S3270_protocol#Prompt_line
     * */
    private final static Pattern STATUS_PATTERN = Pattern.compile(
            "([ULE]) " +                    // keyboard locked?
                    "([FU]) " +             // formatting
                    "([UP]) " +             // protection of current field
                    "(N|C\\([^)]+\\)) " +   // host connection
                    "([NCLPI]) " +          // emulator mode
                    "([2345]) " +           // model number
                    "(\\d+) " +             // rows on display
                    "(\\d+) " +             // cols on display
                    "(\\d+) " +             // cursor row (0 based)
                    "(\\d+) " +             // cursor col (0 based)
                    "0x[0-9A-Fa-f]+ " +     // X window ID
                    "(\\d+\\.\\d+)"         // time for last command (secs)
    );

    private final KeyboardLock keyboardLock;
    private final Formatting formatting;
    private final FieldProtection fieldProtection;
    private final ConnectionStatus connectionStatus;
    private final EmulatorMode emulatorMode;
    private final short modelNumber;
    private final short screenRows;
    private final short screenCols;
    private final short cursorRow;
    private final short cursorCol;

    private static <E extends Coded> E valueForCode(char code, E[] values) {
        for (E value : values) {
            if (code == value.getCode()) {
                return value;
            }
        }

        throw new IllegalArgumentException("Invalid code " + code);
    }

    public static Optional<Status> tryParse(String line) {
        Matcher matcher = STATUS_PATTERN.matcher(line);
        boolean matched = matcher.matches();
        if (!matched) {
            return empty();
        }

        int groupIdx = 1;
        return of(Status.builder()
                .keyboardLock(valueForCode(matcher.group(groupIdx++).charAt(0), KeyboardLock.values()))
                .formatting(valueForCode(matcher.group(groupIdx++).charAt(0), Formatting.values()))
                .fieldProtection(valueForCode(matcher.group(groupIdx++).charAt(0), FieldProtection.values()))
                .connectionStatus(valueForCode(matcher.group(groupIdx++).charAt(0), ConnectionStatus.values()))
                .emulatorMode(valueForCode(matcher.group(groupIdx++).charAt(0), EmulatorMode.values()))
                .modelNumber(Short.parseShort(matcher.group(groupIdx++)))
                .screenRows(Short.parseShort(matcher.group(groupIdx++)))
                .screenCols(Short.parseShort(matcher.group(groupIdx++)))
                .cursorRow(Short.parseShort(matcher.group(groupIdx++)))
                .cursorCol(Short.parseShort(matcher.group(groupIdx)))
                .build());
    }
}
