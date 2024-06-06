/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class ItemStackSerializer extends StdSerializer<ItemStack> {
    public ItemStackSerializer() {
        super(ItemStack.class);
    }

    @Override
    public void serialize(final ItemStack stack, final JsonGenerator generator,
                          final SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        if (stack.isEmpty()) {
            generator.writeEndObject();
            return;
        }
        generator.writeObjectField("item", stack.getItem());
        generator.writeNumberField("count", stack.getCount());
        generator.writeEndObject();
    }
}
