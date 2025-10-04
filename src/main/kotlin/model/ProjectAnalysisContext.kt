package custom.project.model

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File

/**
 * Контекст анализа проекта.
 * Заполняется шаг за шагом по мере выполнения пайплайна.
 */
data class ProjectAnalysisContext(
    val projectDir: String,
    val environment: KotlinCoreEnvironment? = null,
    val files: List<File> = emptyList(),
    val psiFiles: List<KtFile> = emptyList(),
    val classes: List<ClassInfoDetailed> = emptyList(),
    val bindingContext: BindingContext? = null
)