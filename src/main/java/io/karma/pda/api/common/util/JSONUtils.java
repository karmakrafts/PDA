/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.util;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
public final class JSONUtils {
    public static final ObjectMapper MAPPER;
    public static final ObjectReader READER;
    public static final ObjectWriter WRITER;

    static { // @formatter:off
        MAPPER = new ObjectMapper();
        READER = MAPPER.reader();
        WRITER = MAPPER.writer(new DefaultPrettyPrinter()
            .withSeparators(Separators.createDefaultInstance().withRootSeparator("\n")));
    } // @formatter:on

    // @formatter:off
    private JSONUtils() {}
    // @formatter:on
}