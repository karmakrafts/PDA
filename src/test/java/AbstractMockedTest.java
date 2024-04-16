/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import io.karma.pda.api.common.API;
import io.karma.pda.api.common.app.component.AbstractComponent;
import io.karma.pda.api.common.app.component.ComponentType;
import io.karma.pda.api.common.app.component.DefaultContainer;
import io.karma.pda.common.session.DefaultSessionHandler;
import mock.MockForgeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public abstract class AbstractMockedTest extends AbstractTest {
    protected static final class TestComponent extends AbstractComponent {
        public TestComponent(final ComponentType<?> type, final UUID id) {
            super(type, id);
        }
    }

    protected static final class TestContainer extends DefaultContainer {
        public TestContainer(final ComponentType<?> type, final UUID uuid) {
            super(type, uuid);
        }
    }

    protected static final MockForgeRegistry<ComponentType<?>> COMPONENT_REGISTRY = new MockForgeRegistry<>(new ResourceLocation(
        "test",
        "components"));

    protected static final ComponentType<TestComponent> COMPONENT = new ComponentType<>(new ResourceLocation("test",
        "component"), TestComponent::new);
    protected static final ComponentType<TestContainer> CONTAINER = new ComponentType<>(new ResourceLocation("test",
        "container"), TestContainer::new);

    @BeforeAll
    static void beforeAll() {
        // Register components
        COMPONENT_REGISTRY.register(COMPONENT.getName(), COMPONENT);
        COMPONENT_REGISTRY.register(CONTAINER.getName(), CONTAINER);
        // Mock the API
        API.setLogger(LOGGER);
        API.setExecutorService(executorService);
        API.setSessionHandler(DefaultSessionHandler.INSTANCE);
        API.setComponentTypeRegistry(COMPONENT_REGISTRY);
        API.init();
    }
}
