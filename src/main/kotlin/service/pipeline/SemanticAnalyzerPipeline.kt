package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.SemanticAnalyzer


/**
 * Пайплайн для семантического анализа.
 */
object SemanticAnalyzerPipeline {

    /**
     * Семантический анализ.
     * @throws IllegalStateException если окружение не инициализировано.
     */
    fun analyze(): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        val environment = requireNotNull(ctx.environment) { "Environment must be initialized first" }
        val bindingContext = SemanticAnalyzer.analyze(ctx.psiFiles, environment)
        ctx.copy(bindingContext = bindingContext)
    }

}