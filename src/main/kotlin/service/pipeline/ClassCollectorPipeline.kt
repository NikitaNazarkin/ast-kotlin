package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.ClassCollector

/**
 * Пайплайн для сбора классов.
 */
object ClassCollectorPipeline {

    /**
     * Сбор классов.
     * @param context Контекст анализа проекта.
     */
    fun collect(): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        if (ctx.psiFiles.isEmpty()) ctx else {
            val classes = ClassCollector.collect(ctx.psiFiles)
            ctx.copy(classes = classes)
        }
    }

}