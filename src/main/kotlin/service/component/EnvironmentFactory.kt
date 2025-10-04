package custom.project.service.component

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.utils.PathUtil


/**
 * Фабрика для создания окружения KotlinCoreEnvironment.
 */
object EnvironmentFactory {

    /**
     * Создание окружения KotlinCoreEnvironment.
     * @param disposable Диспосабл для управления жизненным циклом.
     */
    fun create(disposable: Disposable): KotlinCoreEnvironment {
        val configuration = CompilerConfiguration().apply {
            put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
            put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_17)
            put(
                CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS,
                LanguageVersionSettingsImpl(LanguageVersion.KOTLIN_1_8, ApiVersion.KOTLIN_1_8)
            )
            put(CommonConfigurationKeys.MODULE_NAME, "analyzed-module")

            // JDK
            val jdkLibs = PathUtil.getJdkClassesRootsFromCurrentJre()
            addJvmClasspathRoots(jdkLibs)
        }

        return KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }
}