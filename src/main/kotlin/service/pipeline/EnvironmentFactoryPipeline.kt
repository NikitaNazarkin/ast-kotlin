package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.EnvironmentFactory
import org.jetbrains.kotlin.com.intellij.openapi.Disposable

/**
 * Пайплайн для создания окружения.
 */
object EnvironmentFactoryPipeline {

    /**
     * Создание окружения.
     * @param context Контекст анализа проекта.
     * @param disposable Диспосабл для управления жизненным циклом.
     */
    fun withEnvironment(
        disposable: Disposable
    ): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        val environment = EnvironmentFactory.create(disposable)
        ctx.copy(environment = environment)
    }

}