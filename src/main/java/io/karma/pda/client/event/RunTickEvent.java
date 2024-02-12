package io.karma.pda.client.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * @author Alexander Hinze
 * @since 12/02/2024
 */
@OnlyIn(Dist.CLIENT)
public class RunTickEvent extends Event {
    private final boolean renderLevel;

    protected RunTickEvent(final boolean renderLevel) {
        this.renderLevel = renderLevel;
    }

    public boolean shouldRenderLevel() {
        return renderLevel;
    }

    @Cancelable
    public static final class Pre extends RunTickEvent {
        public Pre(final boolean renderLevel) {
            super(renderLevel);
        }
    }

    public static final class Post extends RunTickEvent {
        public Post(final boolean renderLevel) {
            super(renderLevel);
        }
    }
}
