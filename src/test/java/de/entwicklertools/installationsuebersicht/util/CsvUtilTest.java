package de.entwicklertools.installationsuebersicht.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvUtilTest {

    @Test
    void joinEscapesSpecialCharacters() {
        List<String> values = List.of("simple", "needs;semicolon", "quote\"here", "multi\nline");
        String joined = CsvUtil.join(values);

        assertEquals("simple;\"needs;semicolon\";\"quote\"\"here\";\"multi\nline\"", joined);
    }

    @Test
    void parseLineHandlesQuotedSemicolonsAndQuotes() {
        String line = "simple;\"needs;semicolon\";\"quote\"\"here\";\"multi\nline\"";
        List<String> parsed = CsvUtil.parseLine(line);

        assertEquals(List.of("simple", "needs;semicolon", "quote\"here", "multi\nline"), parsed);
    }

    @Test
    void valueAtReturnsEmptyWhenOutOfBounds() {
        List<String> values = List.of("one");
        assertEquals("", CsvUtil.valueAt(values, 2));
    }
}
