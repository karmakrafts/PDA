/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import io.karma.pda.api.API;
import io.karma.pda.api.app.component.AbstractComponent;
import io.karma.pda.api.app.component.ComponentType;
import io.karma.pda.foundation.component.DefaultContainer;
import io.karma.pda.mod.session.DefaultSessionHandler;
import mock.MockForgeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexander Hinze
 * @since 16/04/2024
 */
public final class TestHarness {
    public static Logger logger;
    public static ExecutorService executor;
    private static boolean isInitialized;

    public static final class TestComponent extends AbstractComponent {
        public TestComponent(final ComponentType<?> type, final UUID id) {
            super(type, id);
        }
    }

    public static final class TestContainer extends DefaultContainer {
        public TestContainer(final ComponentType<?> type, final UUID uuid) {
            super(type, uuid);
        }
    }

    public static final MockForgeRegistry<ComponentType<?>> COMPONENT_REGISTRY = new MockForgeRegistry<>(new ResourceLocation(
        "test",
        "components"));

    public static final ComponentType<TestComponent> COMPONENT = new ComponentType<>(new ResourceLocation("test",
        "component"), TestComponent.class, TestComponent::new);
    public static final ComponentType<TestContainer> CONTAINER = new ComponentType<>(new ResourceLocation("test",
        "container"), TestContainer.class, TestContainer::new);

    public static void init() {
        if (isInitialized) {
            return;
        }

        logger = LogManager.getLogger();
        executor = Executors.newWorkStealingPool();

        if (!API.isInitialized()) {
            // Register components
            COMPONENT_REGISTRY.register(COMPONENT.getName(), COMPONENT);
            COMPONENT_REGISTRY.register(CONTAINER.getName(), CONTAINER);

            // Mock the API lazily
            API.setLogger(logger);
            API.setObjectMapper(new ObjectMapper());
            API.setExecutorService(executor);
            API.setSessionHandler(DefaultSessionHandler.INSTANCE);
            API.setComponentTypeRegistry(() -> COMPONENT_REGISTRY);
            API.init();
        }

        isInitialized = true;
    }

    public static void dispose() {
        if (!isInitialized) {
            return;
        }
        try {
            executor.shutdown();
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow().forEach(Runnable::run);
            }
        }
        catch (Throwable error) {
            logger.error("Could not shutdown test executor service: {}", error.getMessage());
        }
        isInitialized = false;
    }
}
