package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.KtFileExtractor

/**
 * Пайплайн для извлечения файлов.
 */
object KtFileExtractorPipeline {

    /**
     * Извлечение файлов.
     * @param context Контекст анализа проекта.
     */
    fun extract(): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        val files = KtFileExtractor.extract(ctx.projectDir)
        ctx.copy(files = files)
    }

}