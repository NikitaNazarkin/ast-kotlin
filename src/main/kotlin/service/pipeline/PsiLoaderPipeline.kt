package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.PsiLoader

/**
 * Пайплайн для загрузки PSI файлов.
 */
object PsiLoaderPipeline {

    /**
     * Загрузка PSI файлов.
     * @param context Контекст анализа проекта.
     * @throws IllegalStateException если окружение не инициализировано.
     */
    fun load(): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        val environment = requireNotNull(ctx.environment) { "Environment must be initialized first" }
        if (ctx.files.isEmpty()) ctx else {
            val psiFiles = PsiLoader.load(ctx.files, environment)
            ctx.copy(psiFiles = psiFiles)
        }
    }

}