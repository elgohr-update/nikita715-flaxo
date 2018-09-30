package org.flaxo.cpp

import org.flaxo.core.env.Environment
import org.flaxo.core.lang.`C++Lang`
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.framework.BashInputOutputTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.lang.BashLang
import org.flaxo.core.lang.Language

/**
 * C++ environment supplier.
 */
class CppEnvironmentSupplier(private val language: Language,
                             private val testingLanguage: Language,
                             private val testingFramework: TestingFramework
) : EnvironmentSupplier {

    init {
        if (!isCppAndBashIOEnvironment()) {
            throw CppEnvironmentException("Given group of technologies are not supported: " +
                    "($language, $testingLanguage, $testingFramework)")
        }
    }

    private fun isCppAndBashIOEnvironment(): Boolean =
            language == `C++Lang` && testingLanguage == BashLang && testingFramework == BashInputOutputTestingFramework

    override fun environment(): Environment =
            if (isCppAndBashIOEnvironment()) {
                CppBashEnvironment()
            } else {
                throw CppEnvironmentException("Given group of technologies are not supported: " +
                        "($language, $testingLanguage, $testingFramework)")
            }

    override fun with(language: Language?,
                      testingLanguage: Language?,
                      testingFramework: TestingFramework?
    ): EnvironmentSupplier =
            CppEnvironmentSupplier(
                    language = language ?: this.language,
                    testingLanguage = testingLanguage ?: this.testingLanguage,
                    testingFramework = testingFramework ?: this.testingFramework
            )

}