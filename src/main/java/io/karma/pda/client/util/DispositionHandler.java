package io.karma.pda.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Hinze
 * @since 09/02/2024
 */
@OnlyIn(Dist.CLIENT)
public final class DispositionHandler {
    private static final Set<Disposable> OBJECTS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // @formatter:off
    private DispositionHandler() {}
    // @formatter:on

    public static void disposeAll() {
        OBJECTS.forEach(Disposable::dispose);
        OBJECTS.clear();
    }

    public static void addObject(final Disposable obj) {
        if (OBJECTS.contains(obj)) {
            return;
        }
        OBJECTS.add(obj);
    }
}
