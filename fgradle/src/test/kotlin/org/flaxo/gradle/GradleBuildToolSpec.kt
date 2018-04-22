package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.core.build.BuildTool
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.language.JavaLang
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import io.kotlintest.matchers.substring
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object GradleBuildToolSpec : SubjectSpek<BuildTool>({

    val firstPlugin = GradlePlugin("java")
    val secondPlugin = GradlePlugin("application")
    val firstDependency = GradleDependency("a", "b", "c")
    val secondDependency = GradleDependency("1", "2", "3")
    val testingDependency = GradleDependency("t", "y", "u", type = GradleDependencyType.TEST_COMPILE)
    val compilingDependency = GradleDependency("t", "y", "u", type = GradleDependencyType.COMPILE)
    val travis: EnvironmentSupplier = mock {
        on { withLanguage(any()) }.thenReturn(it)
        on { withTestingLanguage(any()) }.thenReturn(it)
        on { withTestingFramework(any()) }.thenReturn(it)
        on { getEnvironment() }.thenReturn(SimpleEnvironment(emptySet()))
    }

    subject {
        GradleBuildTool(travis)
                .with(JavaLang, JavaLang, JUnitTestingFramework)
                as BuildTool
    }

    describe("Gradle build tool") {
        on("plugins addition") {
            val buildTool =
                    subject.addPlugin(firstPlugin)
                            .addPlugin(secondPlugin)
                            .addPlugin(secondPlugin)
            val environment = buildTool.getEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name == "build.gradle" }
                    ?: throw GradleException("build.gradle wasn't found")

            it("should have build.gradle with all passed plugins") {
                buildGradle.content() shouldHave substring("apply plugin: ").or(substring("id "))
                buildGradle.shouldHaveName(firstPlugin, secondPlugin)
            }

            it("should have build.gradle with a single line for repeated plugins") {
                buildGradle.shouldHaveSingle(secondPlugin)
            }
        }

        on("dependency addition") {
            val buildTool =
                    subject.addPlugin(javaPlugin())
                            .addDependency(firstDependency)
                            .addDependency(secondDependency)
                            .addDependency(secondDependency)
            val environment = buildTool.getEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name == "build.gradle" }
                    ?: throw GradleException("build.gradle wasn't found")

            it("should have build.gradle with build.gradle containing single dependency") {
                buildGradle.shouldHaveName(firstDependency, secondDependency)
            }

            it("should have build.gradle with a single line for repeated dependencies") {
                buildGradle.shouldHaveSingleName(secondDependency)
            }
        }

        on("different types of dependencies addition") {
            val buildTool =
                    subject.addPlugin(javaPlugin())
                            .addDependency(testingDependency)
                            .addDependency(compilingDependency)
            val environment = buildTool.getEnvironment()
            val buildGradle = environment.getFiles()
                    .find { it.name == "build.gradle" }
                    ?: throw GradleException("build.gradle wasn't found")

            it("should contain all dependencies") {
                buildGradle.shouldHaveDependency(testingDependency, compilingDependency)
            }

        }
    }
})

private fun EnvironmentFile.shouldHaveName(vararg plugins: GradlePlugin) {
    plugins.forEach {
        content() shouldHave
                substring("apply plugin: \"${it.id}\"")
                        .or(substring("""id "${it.id}""""))
    }
}

private fun EnvironmentFile.shouldHaveSingle(vararg plugins: GradlePlugin) {
    plugins.forEach {
        content()
                .split(
                        "apply plugin: \"${it.id}\"",
                        """id "${it.id}""""
                ).size - 1 shouldBe 1
    }
}

private fun EnvironmentFile.shouldHaveName(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content() shouldHave substring(it.name)
    }
}

private fun EnvironmentFile.shouldHaveDependency(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content() shouldHave substring(it.toString())
    }
}

private fun EnvironmentFile.shouldHaveSingleName(vararg dependencies: GradleDependency) {
    dependencies.forEach {
        content()
                .split(it.name)
                .size - 1 shouldBe 1
    }
}