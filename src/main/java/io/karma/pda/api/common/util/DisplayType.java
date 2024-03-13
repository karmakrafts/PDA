package io.karma.pda.api.common.util;

/**
 * @author Alexander Hinze
 * @since 13/03/2024
 */
public enum DisplayType {
    BW, SRGB, OLED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}