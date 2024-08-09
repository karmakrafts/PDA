/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.mod.state

import io.karma.pda.mod.PDAMod
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import java.util.function.Function as JFunction

/**
 * @author Alexander Hinze
 * @since 26/04/2024
 */
@io.karma.pda.api.state.Reflector(io.karma.pda.composition.Composable::class)
class ComposableStateReflector : io.karma.pda.api.state.StateReflector {
    private fun getState(prop: KProperty<*>, instance: Any? = null): io.karma.pda.api.state.MutableState<*>? {
        val field = prop.javaField ?: return null
        if (!field.isAnnotationPresent(io.karma.pda.api.state.Synchronize::class.java)) {
            return null
        }
        val type = prop.returnType
        if (!type.isSubtypeOf(io.karma.pda.api.state.MutableState::class.starProjectedType)) {
            return null
        }
        PDAMod.LOGGER.debug("Reflecting synced property '{}' in {}", prop.name, field.declaringClass.name)
        val getter = prop.getter
        val isNonPublic = getter.visibility != KVisibility.PUBLIC
        if (isNonPublic) getter.isAccessible = true
        val state = getter.call(instance) as io.karma.pda.api.state.MutableState<*>
        state.name = prop.name
        state.isPersistent = field.isAnnotationPresent(io.karma.pda.api.state.Persistent::class.java)
        if (isNonPublic) getter.isAccessible = false
        return state
    }

    override fun getStates(
        type: Class<*>,
        instance: Any,
        reflectorGetter: JFunction<Class<*>, io.karma.pda.api.state.StateReflector>
    ): List<io.karma.pda.api.state.MutableState<*>> {
        val kType = type.kotlin
        val superType = type.superclass
        val props = kType.declaredMemberProperties.mapNotNull { getState(it, instance) }.toList()
        if (superType == null || superType == Object::class.java) {
            return props
        }
        // @formatter:off
        return props.plus(reflectorGetter.apply(superType)
            .getStates(superType, instance, reflectorGetter))
        // @formatter:on
    }
}