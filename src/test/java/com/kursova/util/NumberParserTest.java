package com.kursova.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberParserTest {

    @Test
    void parseIntOrNull_validInteger_returnsInteger() {
        assertEquals(42, NumberParser.parseIntOrNull("42"));
    }

    @Test
    void parseIntOrNull_withSpaces_returnsInteger() {
        assertEquals(17, NumberParser.parseIntOrNull("  17  "));
    }

    @Test
    void parseIntOrNull_invalidInput_returnsNull() {
        assertNull(NumberParser.parseIntOrNull("abc"));
    }

    @Test
    void parseIntOrNull_emptyString_returnsNull() {
        assertNull(NumberParser.parseIntOrNull(""));
    }

    @Test
    void parseIntOrNull_null_returnsNull() {
        assertNull(NumberParser.parseIntOrNull(null));
    }

    @Test
    void parseDoubleOrNull_validDouble_returnsDouble() {
        assertEquals(3.14, NumberParser.parseDoubleOrNull("3.14"));
    }

    @Test
    void parseDoubleOrNull_withSpaces_returnsDouble() {
        assertEquals(2.718, NumberParser.parseDoubleOrNull(" 2.718 "));
    }

    @Test
    void parseDoubleOrNull_invalidInput_returnsNull() {
        assertNull(NumberParser.parseDoubleOrNull("pi"));
    }

    @Test
    void parseDoubleOrNull_emptyString_returnsNull() {
        assertNull(NumberParser.parseDoubleOrNull(""));
    }

    @Test
    void parseDoubleOrNull_null_returnsNull() {
        assertNull(NumberParser.parseDoubleOrNull(null));
    }
}
