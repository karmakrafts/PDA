/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.common.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import io.karma.pda.api.common.API;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

/**
 * @author Alexander Hinze
 * @since 29/04/2024
 */
public final class ItemStackDeserializer extends StdDeserializer<ItemStack> {
    public ItemStackDeserializer() {
        super(ItemStack.class);
    }

    @Override
    public ItemStack deserialize(final JsonParser parser,
                                 final DeserializationContext deserializationContext) throws IOException {
        final var node = parser.getCodec().readTree(parser);
        if (node.size() == 0) {
            return ItemStack.EMPTY;
        }
        final var item = API.getObjectMapper().treeToValue(node.get("item"), Item.class);
        final var count = ((IntNode) node.get("count")).intValue();
        return new ItemStack(item, count);
    }
}
