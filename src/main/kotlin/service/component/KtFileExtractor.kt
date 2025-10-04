package custom.project.service.component

import java.io.File

/**
 * Класс для извлечения файлов.
 */
object KtFileExtractor {
    private const val KT_FILES = "kt"
    private const val TEST_PATH = "/test/"
    private const val TESTS_PATH = "/tests/"

    /**
     * Собирает файлы с расширением .kt, исключая файлы в директориях test и tests.
     * @param path Путь к директории проекта.
     * @return Список файлов.
     */
    fun extract(path: String): List<File> =
        File(path)
            .walkTopDown()
            .filterNot { it.path.contains(TEST_PATH) || it.path.contains(TESTS_PATH) }
            .filter { it.extension == KT_FILES }
            .toList()
}