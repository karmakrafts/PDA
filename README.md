# PDA
A mod which adds functional pocket computers to the game.

## For players
If you want the play the mod, you can download it on CurseForge and Modrinth soon&trade;.

## For developers
### Developing apps

If you want to use the mod to develop an app for the PDA, you can simply add the required  
dependencies through the following code in your `build.gradle` file:

```groovy
repositories {
    maven { url = 'https://maven.covers1624.net' }
}

dependencies {
    compileOnly fg.deobf(
            group: 'io.karma.pda', 
            name: "pda-${minecraft_version}", 
            version: "${minecraft_version}-1.0+", 
            classifier: 'api', 
            changing: true)
    runtimeOnly fg.deobf(
            group: 'io.karma.pda',
            name: "pda-${minecraft_version}",
            version: "${minecraft_version}-1.0+",
            changing: true)
}
```

**An example for a simple Hello World app in Java:**

```java
import io.karma.pda.api.common.app.*;
import io.karma.pda.api.common.app.component.*;

public final class ExampleApp extends AbstractApp {
    public ExampleApp(final AppType<?> type) {
        super(type);
    }
    
    public void init(final AppContext context) {
        final var label = DefaultComponents.LABEL.create(props -> props
            .width(FlexValue.percent(100F))
            .height(FlexValue.auto())
        );
        label.text.set("Hello World!");
        container.addChild(label);
    }
}
```

**An example for the same Hello World app in Kotlin using the compose API:**

```kotlin
import io.karma.pda.api.common.app.*
import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.app.compose.*

class ExampleApp(type: AppType<*>) : ComposableApp(type) {
    override fun init(context: AppContext) {
        compose {
            label({
                width(100.percent)
                height(auto)
            }) { 
                text("Hello World") 
            }
        }
    }
}
```