package custom.project.service.pipeline

import custom.project.model.ProjectAnalysisContext
import custom.project.service.component.PsiEnricher

/**
 * Пайплайн для дообогащения классов.
 */
object ClassEnricherPipeline {

    /**
     * Дообогащение классов.
     * @param context Контекст анализа проекта.
     * @throws IllegalStateException если BindingContext не инициализирован.
     */
    fun enrich(): (ProjectAnalysisContext) -> ProjectAnalysisContext = { ctx ->
        val bindingContext = requireNotNull(ctx.bindingContext) { "BindingContext must be initialized first" }
        PsiEnricher.enrich(ctx.classes, bindingContext)
        ctx
    }
}