package custom.project.service.prompt

import custom.project.enums.TestLibraryType
import custom.project.model.CallNode

class JUnit5PromptIntro : PromptIntro {

    override fun supports(libraryType: TestLibraryType): Boolean {
        return libraryType == TestLibraryType.JUNIT5
    }

    override fun generateIntro(node: CallNode): String = buildString {
        appendLine("Напиши подробный юнит-тест для метода '${node.methodDetailed.name}' класса '${node.classInfo.className}'.")
        appendLine("Используй фреймворк JUnit5 для написания тестов.")
        appendLine("Аннотируй тестовые методы аннотацией @Test.")
        appendLine("Используй whenever и then если метод возвращает значение.")
        appendLine("Используй doNothing если метод возвращает Unit.")
        appendLine("Если тебе известна структура ДТО - не мокируй, а создавай объект и заполняй его значениями подходящими по смыслу")
        appendLine("Все моки пиши в формате val serviceName = mockk<ServiceName>() и обязательно в начале класса.")
        appendLine("Тесты должны быть детализированными и охватывать все возможные сценарии использования метода.")
        appendLine("Если тебе чего-то не хватает для полного формирования теста — напиши, что именно.")
        appendLine()
    }
}