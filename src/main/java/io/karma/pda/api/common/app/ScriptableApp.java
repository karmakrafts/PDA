/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.api.common.app;

import io.karma.pda.api.common.API;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author Alexander Hinze
 * @since 12/04/2024
 */
public final class ScriptableApp extends AbstractApp {
    private static final ScriptEngineManager ENGINE_MANAGER = new ScriptEngineManager();
    private static final ScriptEngine ENGINE = ENGINE_MANAGER.getEngineByName("nashorn");

    private final Logger logger;
    private final ResourceLocation scriptLocation;

    public ScriptableApp(final AppType<?> type) {
        super(type);
        final var name = type.getName();
        logger = LogManager.getLogger(name.toString());
        scriptLocation = new ResourceLocation(name.getNamespace(), String.format("apps/%s.js", name.getPath()));
        API.getLogger().debug("Created scriptable app instance from {}", scriptLocation);
    }

    @Override
    public void init(final AppContext context) {
        API.getLogger().debug("Initializing scriptable app instance {}", scriptLocation);
        reload(context);
    }

    @Override
    public void reload(final AppContext context) {
        API.getLogger().debug("Reloading scriptable app instance {}", scriptLocation);
        try (final var reader = API.getResourceManager().getResource(scriptLocation).orElseThrow().openAsReader()) {
            final var bindings = ENGINE.createBindings();
            bindings.put("API", new ScriptAPI(context));
            ENGINE.eval(reader, bindings);
        }
        catch (Throwable error) {
            API.getLogger().error("Could not load scriptable app instance {}: {}", scriptLocation, error.getMessage());
        }
    }

    private final class ScriptAPI {
        public final AppContext context;
        public final Logger logger = ScriptableApp.this.logger; // Steal reference

        private ScriptAPI(final AppContext context) {
            this.context = context;
        }
    }
}
