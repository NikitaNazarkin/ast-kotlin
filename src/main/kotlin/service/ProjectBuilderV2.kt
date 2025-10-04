package custom.project.service

import custom.project.model.ProjectAnalysisContext
import custom.project.model.ProjectAnalysisResult
import custom.project.service.cache.AnalysisCacheService
import custom.project.service.pipeline.ClassCollectorPipeline
import custom.project.service.pipeline.ClassEnricherPipeline
import custom.project.service.pipeline.EnvironmentFactoryPipeline
import custom.project.service.pipeline.KtFileExtractorPipeline
import custom.project.service.pipeline.PsiLoaderPipeline
import custom.project.service.pipeline.SemanticAnalyzerPipeline
import custom.project.utils.then
import custom.project.utils.useDisposable

/**
 * Класс для построения проекта.
 */
object ProjectBuilderV2 {

    fun analyzeProject(path: String): ProjectAnalysisResult {
        return AnalysisCacheService.withCache(path) {
            useDisposable { disposable ->
                println("Starting project analysis for path: $path")
                val ctx = ProjectAnalysisContext(projectDir = path)
                    .then(EnvironmentFactoryPipeline.withEnvironment(disposable))
                    .then(KtFileExtractorPipeline.extract())
                    .then(PsiLoaderPipeline.load())
                    .then(ClassCollectorPipeline.collect())
                    .then(SemanticAnalyzerPipeline.analyze())
                    .then(ClassEnricherPipeline.enrich())
                ProjectAnalysisResult(ctx.classes, ctx.bindingContext!!)
            }
        }
    }
}