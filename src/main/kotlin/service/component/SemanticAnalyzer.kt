package custom.project.service.component

import org.jetbrains.kotlin.cli.jvm.compiler.CliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Класс для семантического анализа.
 */
object SemanticAnalyzer {

    /**
     * Семантический анализ.
     * @param files Список PSI файлов.
     * @param environment Окружение KotlinCoreEnvironment.
     * @return BindingContext
     */
    fun analyze(files: List<KtFile>, environment: KotlinCoreEnvironment): BindingContext {
        val analysisResult = TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project,
            files,
            CliBindingTrace(environment.project),
            environment.configuration,
            { scope -> environment.createPackagePartProvider(scope) }
        )

        return analysisResult.bindingContext
    }
}