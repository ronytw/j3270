package com.github.filipesimoes.j3270.protocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class StatusTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "U F U C(192.168.111.1) I 3 24 80 8 17 0x0 0.321",
            "U F U C(192.168.111.1) I 3 24 80 8 21 0x0 0.001",
            "L U U C(192.168.111.1) I 3 24 80 0 0 0x0 0.126",
            "U F U C(192.168.111.1) I 3 24 80 13 21 0x0 0.003",
            "U F P C(192.168.111.1) I 3 24 80 16 48 0x0 0.001",
    })
    void canParseValidStatusLine(String line) {
        assertThat(Status.tryParse(line)).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "U F U C(192.168.111.1) I 3 24 80 8 21 0x0 0.001 abc",  // extra field
            "U F U C(192.168.111.1) I 3 24 80 8 17 0x0",            // missing time
            "Z F U C(192.168.111.1) I 3 24 80 8 17 0x0 0.123",      // wrong keyboard
            "U J U C(192.168.111.1) I 3 24 80 8 17 0x0 0.123",      // wrong formatting
            "U F G C(192.168.111.1) I 3 24 80 13 21 0x0 0.003",     // wrong protection field
            "U F P X(192.168.111.1) I 3 24 80 16 48 0x0 0.001",     // wrong connection status
            "U F P C() I 3 24 80 16 48 0x0 0.001",                  // missing connection host
            "U F P C(192.168.111.1) Y 3 24 80 16 48 0x0 0.001",     // wrong emulator mode
            "U F P C(192.168.111.1) I 9 24 80 16 48 0x0 0.001",     // unsupported model number
    })
    void cannotParseInvalidStatusLine(String line) {
        assertThat(Status.tryParse(line)).isEmpty();
    }

    @Test
    void canParseStatus() {
        Optional<Status> optionalStatus = Status.tryParse("U F U C(192.168.111.1) I 3 24 80 8 17 0x0 0.321");

        assertThat(optionalStatus).isNotEmpty();
        Status status = optionalStatus.get();
        assertThat(status.getKeyboardLock()).isEqualTo(Status.KeyboardLock.UNLOCKED);
        assertThat(status.getFormatting()).isEqualTo(Status.Formatting.FORMATTED);
        assertThat(status.getFieldProtection()).isEqualTo(Status.FieldProtection.UNPROTECTED);
        assertThat(status.getConnectionStatus()).isEqualTo(Status.ConnectionStatus.CONNECTED);
        assertThat(status.getEmulatorMode()).isEqualTo(Status.EmulatorMode.MODE_3270);
        assertThat(status.getModelNumber()).isEqualTo((short) 3);
        assertThat(status.getScreenRows()).isEqualTo((short) 24);
        assertThat(status.getScreenCols()).isEqualTo((short) 80);
        assertThat(status.getCursorRow()).isEqualTo((short) 8);
        assertThat(status.getCursorCol()).isEqualTo((short) 17);
    }
}
