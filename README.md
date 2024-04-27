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
            version: "${minecraft_version}-1.+", 
            classifier: 'api', 
            changing: true)
    runtimeOnly fg.deobf(
            group: 'io.karma.pda',
            name: "pda-${minecraft_version}",
            version: "${minecraft_version}-1.+",
            changing: true)
}
```

**An example for a simple Hello World app in Java:**

```java
import io.karma.pda.api.common.state.*;
import io.karma.pda.api.common.app.*;
import io.karma.pda.api.common.app.component.*;

public final class ExampleApp extends AbstractApp {
    @Persistent  // Means that the value will be saved/loaded from/to NBT
    @Synchronize // Means that the value will be updated for other clients in realtime
    private final MutableState<String> mySetting = MutableState.of(""); 
    
    public ExampleApp(final AppType<?> type) {
        super(type); // Passing no theme here will use the device theme
    }
    
    public void compose() {
        addDefaultView(container -> {           // The default must always be present
            final var label = DefaultComponents.LABEL.create(props -> props
                .width(FlexValue.percent(100F)) // 100% of the width of the parent
                .height(FlexValue.auto())       // Automatically decide on the height
            );
            label.text.set(mySettings.get());   // Use setter since this is a syncable property
            container.addChild(label);          // Add the label to the container of the default view
        });
    }
}
```

**An example for the same Hello World app in Kotlin using the compose API:**

```kotlin
import io.karma.pda.api.common.state.*
import io.karma.pda.api.common.app.*
import io.karma.pda.api.common.app.component.*
import io.karma.pda.api.common.app.compose.*

@Composable
class ExampleApp(type: AppType<*>) : ComposableApp(type) {
    @Persistent  // Means that the value will be saved/loaded from/to NBT
    @Synchronize // Means that the value will be updated for other clients in realtime
    private val mySetting: MutableState<MySetting> = mutableStateOf("")
    
    override fun compose() {
        defaultView {
            label({
                width(100.percent)
                height(auto)
            }) { 
                text(mySetting()) 
            }
        }
    }
}
```