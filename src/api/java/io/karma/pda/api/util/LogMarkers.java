/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author Alexander Hinze
 * @since 18/04/2024
 */
public final class LogMarkers {
    public static final Marker API = MarkerManager.getMarker("PDAAPI");
    public static final Marker PROTOCOL = MarkerManager.getMarker("PDAPRO");
    public static final Marker RENDERER = MarkerManager.getMarker("PDAREN");
    public static final Marker FLEX = MarkerManager.getMarker("PDAFLX");

    // @formatter:off
    private LogMarkers() {}
    // @formatter:on
}
