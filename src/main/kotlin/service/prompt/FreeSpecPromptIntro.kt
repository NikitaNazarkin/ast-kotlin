package custom.project.service.prompt

import custom.project.enums.TestLibraryType
import custom.project.model.CallNode

class FreeSpecPromptIntro : PromptIntro {
    override fun supports(libraryType: TestLibraryType): Boolean {
        return libraryType == TestLibraryType.FREE_SPEC
    }

    override fun generateIntro(node: CallNode): String = buildString {
        appendLine("Напиши подробный юнит-тест для метода '${node.methodDetailed.name}' класса '${node.classInfo.className}'.")
        appendLine("Используй фреймворк Kotest для написания тестов. Для наследования используй FreeSpec.")
        appendLine("Тесты пиши только внутри блока init { }.")
        appendLine("```kotlin")
        appendLine("class Name: FreeSpec() {")
        appendLine("    init {")
        appendLine("        \"test name\" {")
        appendLine("                // Ваш тест здесь")
        appendLine("           }")
        appendLine("    }")
        appendLine("}")
        appendLine("```")
        appendLine("Используй every {} если метод возвращает значение.")
        appendLine("Используй justRun {} если метод возвращает Unit.")
        appendLine("Если тебе известна структура ДТО - не мокируй, а создавай объект и заполняй его значениями подходящими по смыслу")
        appendLine("Все моки пиши в формате val serviceName = mockk<ServiceName>() и обязательно в начале класса.")
        appendLine("Тестируемый класс создавай сразу после создания моков.")
        appendLine("Тесты должны быть детализированными и охватывать все возможные сценарии использования метода.")
        appendLine("Если тебе чего-то не хватает для полного формирования теста — напиши, что именно.")
        appendLine()
    }
}