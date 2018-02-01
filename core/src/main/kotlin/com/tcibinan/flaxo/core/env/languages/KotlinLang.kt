package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.JUnitTestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.gradle.kotlinGradlePlugin
import com.tcibinan.flaxo.core.env.tools.gradle.kotlinJreDependency
import com.tcibinan.flaxo.core.env.tools.gradle.kotlinTestDependency

object KotlinLang : AbstractLanguage("kotlin",
        suitableTestLanguages = setOf(),
        suitableTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
) {

    override fun main(buildTool: BuildTool) {
        buildTool
                .addPlugin(kotlinGradlePlugin())
                .addDependency(kotlinJreDependency())
    }

    override fun test(buildTool: BuildTool) {
        buildTool
                .addPlugin(junitPlatformPlugin())
                .addDependency(kotlinTestDependency())
    }
}