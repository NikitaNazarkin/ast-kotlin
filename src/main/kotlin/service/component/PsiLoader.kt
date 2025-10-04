package custom.project.service.component

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * Класс для загрузки PSI файлов.
 */
object PsiLoader {

    /**
     * Загрузка PSI файлов.
     * @param files Список файлов.
     * @param environment Окружение KotlinCoreEnvironment.
     * @return Список PSI файлов.
     */
    fun load(files: List<File>, environment: KotlinCoreEnvironment): List<KtFile> {
        val psiFactory = PsiFileFactory.getInstance(environment.project)
        return files.mapNotNull { file ->
            try {
                val text = file.readText()
                psiFactory.createFileFromText(file.name, KotlinFileType.INSTANCE, text) as? KtFile
            } catch (ex: Exception) {
                println("❌ Failed to parse PSI: ${file.absolutePath}")
                null
            }
        }
    }
}