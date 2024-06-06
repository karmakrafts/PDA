/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

package io.karma.pda.composition

/**
 * Describes an app class to be composable by the state handler API.
 * This means the inheritance hierarchy of the annotated class will
 * be scanned front-to-back using reflection to find any state properties
 * annotated with [Synchronized].
 *
 * @author Alexander Hinze
 * @since 26/04/2024
 */
@ComposeDsl
@DslMarker
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR
)
annotation class Composable
