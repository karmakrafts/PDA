/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.client.sync;

import com.fasterxml.jackson.databind.JsonNode;
import io.karma.pda.api.common.API;
import io.karma.pda.api.common.sync.Synced;
import io.karma.pda.api.common.sync.Synchronizer;
import io.karma.pda.api.common.util.Identifiable;
import io.karma.pda.api.common.util.JSONUtils;
import io.karma.pda.common.PDAMod;
import io.karma.pda.common.network.sb.SPacketSyncValues;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Alexander Hinze<
 * @since 11/04/2024
 */
@OnlyIn(Dist.CLIENT)
public final class ClientSynchronizer implements Synchronizer {
    private static final ConcurrentHashMap<Class<? extends Identifiable>, List<VarHandle>> FIELD_CACHE = new ConcurrentHashMap<>();
    private final UUID id;
    private final ConcurrentHashMap<UUID, Synced<?>> fields = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<Pair<UUID, Synced<?>>> queue = new ConcurrentLinkedDeque<>();

    public ClientSynchronizer(final UUID id) {
        this.id = id;
    }

    private static List<VarHandle> getFields(final Identifiable object) {
        return FIELD_CACHE.computeIfAbsent(object.getClass(), type -> {
            try {
                final var lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());
                final var fields = type.getDeclaredFields();
                final var values = new ArrayList<VarHandle>();
                for (final var field : fields) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue; // We don't care about static fields
                    }
                    final var fieldType = field.getType();
                    if (!Synced.class.isAssignableFrom(fieldType)) {
                        continue; // We are not interested in this field
                    }
                    values.add(lookup.unreflectVarHandle(field));
                }
                return values;
            }
            catch (Throwable error) {
                API.getLogger().error("Could not lookup synchronizable fields in {}", object);
                return Collections.emptyList();
            }
        });
    }

    @Override
    public void flush() {
        PDAMod.LOGGER.debug("Submitting synchronizer flush");
        // Offload the sync serialization to our executor service to utilize full CPU
        PDAMod.EXECUTOR_SERVICE.submit(() -> {
            final var values = new HashMap<UUID, JsonNode>();
            while (!queue.isEmpty()) {
                final var pair = queue.removeFirst();
                values.put(pair.getLeft(), JSONUtils.MAPPER.valueToTree(pair.getRight().get()));
            }
            Minecraft.getInstance().execute(() -> PDAMod.CHANNEL.sendToServer(new SPacketSyncValues(id, values)));
            PDAMod.LOGGER.debug("Flushed synchronizer data to server");
        });
    }

    @SuppressWarnings("all")
    @Override
    public void register(final UUID id, final Synced<?> value) {
        if (fields.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Field %s is already registered", id));
        }
        value.setCallback((previous, current) -> {
            if (current.equals(previous) || queue.contains(value)) {
                return;
            }
            queue.addLast(Pair.of(id, value));
        });
        fields.put(id, value);
        PDAMod.LOGGER.debug("Registered value {}/{} to synchronizer", id, value);
    }

    @Override
    public void register(final Identifiable object) {
        final var ownerId = object.getId();
        final var fields = getFields(object);
        for (final var field : fields) {
            final var value = (Synced<?>) field.get(object);
            if (value == null) {
                return;
            }
            register(ownerId, value);
        }
    }

    @Override
    public void unregister(final UUID id) {
        final var field = fields.remove(id);
        if (field == null) {
            throw new IllegalArgumentException(String.format("Field %s is not registered", id));
        }
        final var toRemove = new HashSet<Pair<UUID, Synced<?>>>();
        for (final var pair : queue) {
            if (!pair.getLeft().equals(id)) {
                continue;
            }
            toRemove.add(pair);
        }
        queue.removeAll(toRemove);
        PDAMod.LOGGER.debug("Unregistered value {} from synchronizer", id);
    }
}
