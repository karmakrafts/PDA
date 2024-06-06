/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.state

import io.karma.pda.api.state.*
import io.karma.pda.PDAMod
import io.karma.pda.composition.Composable
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
@Reflector(Composable::class)
class ComposableStateReflector : StateReflector {
    private fun getState(prop: KProperty<*>, instance: Any? = null): MutableState<*>? {
        val field = prop.javaField ?: return null
        if (!field.isAnnotationPresent(Synchronize::class.java)) {
            return null
        }
        val type = prop.returnType
        if (!type.isSubtypeOf(MutableState::class.starProjectedType)) {
            return null
        }
        PDAMod.LOGGER.debug("Reflecting synced property '{}' in {}", prop.name, field.declaringClass.name)
        val getter = prop.getter
        val isNonPublic = getter.visibility != KVisibility.PUBLIC
        if (isNonPublic) getter.isAccessible = true
        val state = getter.call(instance) as MutableState<*>
        state.name = prop.name
        state.isPersistent = field.isAnnotationPresent(Persistent::class.java)
        if (isNonPublic) getter.isAccessible = false
        return state
    }

    override fun getStates(
        type: Class<*>,
        instance: Any,
        reflectorGetter: JFunction<Class<*>, StateReflector>
    ): List<MutableState<*>> {
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