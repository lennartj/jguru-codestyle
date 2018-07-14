/*
 * Copyright (c) jGuru Europe AB.
 * All rights reserved.
 */

package se.jguru.codestyle.projects.enforcer

import java.io.Serializable

class KotlinClassOutsideOfPackageDirStructure(val bar : String = "bar!") : Serializable {

    override fun toString(): String = "This is a $bar"
}