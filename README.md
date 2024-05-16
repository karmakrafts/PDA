# PDA

A mod which adds functional pocket computers to the game.

## For players

If you want the play the mod, you can download it on CurseForge and Modrinth soon&trade;.

### About custom natives

This mod uses various native libraries through custom built LWJGL modules to achieve what it does. This includes but is
not
limited to:

* [Yoga](https://github.com/facebook/yoga) as the flexbox layout engine
* [FreeType](https://github.com/freetype/freetype) for loading TrueType font files
* [msdfgen](https://github.com/Chlumsky/msdfgen) for generating MTSDF glyph sprites to build font atlases

The mod also makes use of [libffi](https://github.com/libffi/libffi) to mitigate missing LWJGL call intrinsics.  
Currently, the mod is built and tested on the following systems:

| Platform | Architecture | Status |
|----------|--------------|--------|
| Windows  | i386         | ❌      |
| Windows  | x64          | ✅      |
| Windows  | arm64        | 🛠️    |
| Linux    | i386         | ❌      |
| Linux    | x64          | ✅      |
| Linux    | arm32sf      | ❌      |
| Linux    | arm32hf      | ❌      |
| Linux    | arm64        | 🛠️    |
| Linux    | riscv64      | 🛠️    |
| macOS    | x64          | ✅      |
| macOS    | arm64        | ✅    |

❌ Currently unsupported | 🛠️ Being worked on | ✅ Supported

## FAQ

**> Why?**  
Originally meant for in-game documentation, it turned into my personal  
spiritual successor to mods like [ComputerCraft](https://tweaked.cc/) and [OpenComputers](https://www.curseforge.com/minecraft/mc-mods/opencomputers).  
It's as if in-game computers arrived in the modern age of graphical interfaces.

**> Can i use the mod in a modpack?**  
Yes. Credit is optional but always appreciated.

**> Can i propose a new feature/app for the mod?**  
Yes, but i reserve the right to reject ideas.

**> Can you add support for platform X?**  
Maybe. Create an issue and i'll see what i can do. I'm mostly limited by what  
platforms are supported by Java and LWJGL.

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
            final var label = DefaultComponents.LABEL.create(props -> props.width(FlexValue.percent(100F)) // 100% of the width of the parent
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