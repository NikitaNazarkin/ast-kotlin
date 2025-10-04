package custom.project.service.prompt

import custom.project.enums.TestLibraryType
import custom.project.model.CallNode
import custom.project.model.ClassInfoDetailed

object PromptBuilder {
    private val promptIntros = listOf(FreeSpecPromptIntro(), JUnit5PromptIntro())

    fun generatePrompt(
        node: CallNode,
        libraryType: TestLibraryType = TestLibraryType.FREE_SPEC
    ): String = buildString {
        appendLine(getIntro(node, libraryType))
        appendLine(generateDependencies(node))
        appendLine(generateMethodCode(node))
        appendLine(generateCalledMethods(node))
        appendLine(generateUsedDataClasses(node))
        appendLine(generateUsedEnums(node))
    }

    private fun getIntro(
        node: CallNode,
        libraryType: TestLibraryType
    ): String {
        val handler = promptIntros.firstOrNull { it.supports(libraryType) } ?: promptIntros.first()
        return handler.generateIntro(node)
    }

    private fun generateDependencies(node: CallNode): String = buildString {
        appendLine("Класс '${node.classInfo.className}' имеет зависимости в конструкторе:")
        if (node.classInfo.constructorDependencies.isEmpty()) {
            appendLine("  - Нет зависимостей")
        } else {
            node.classInfo.constructorDependencies.forEach { dependency ->
                appendLine("  - $dependency")
            }
        }
        appendLine()
    }

    private fun generateMethodCode(node: CallNode): String = buildString {
        appendLine("Исходный код метода:")
        appendLine("```kotlin")
        appendLine(node.methodDetailed.text.trim())
        appendLine("```")
        appendLine()
    }

    /**
     * Печатаем вызовы:
     * - из оригинального метода → public и private
     * - из private методов → рекурсивно разворачиваем дальше
     * - из public методов → не разворачиваем
     */
    private fun generateCalledMethods(node: CallNode, fromOriginal: Boolean = false, indent: String = ""): String =
        buildString {
            if (node.calledMethods.isEmpty()) return@buildString

            val sourceName = if (fromOriginal) node.methodDetailed.name else "метода ${node.methodDetailed.name}"
            appendLine("${indent}Вызовы из $sourceName:")

            node.calledMethods.forEach { called ->
                appendLine("$indent- '${called.methodDetailed.name}' (${if (called.methodDetailed.isPrivate) "private" else "public"}) класса '${called.classInfo.className}':")
                appendLine("$indent kotlin")
                appendLine(called.methodDetailed.text.trim())
                appendLine("$indent")

                if (called.methodDetailed.isPrivate) {
                    appendLine(generateCalledMethods(called, fromOriginal = false, indent = "$indent  "))
                }
            }
        }

    private fun generateUsedDataClasses(node: CallNode): String = buildString {
        fun collectDtos(n: CallNode, includeNested: Boolean): Set<ClassInfoDetailed> {
            val fromHere = n.usedDataClasses
            val fromChildren = if (includeNested) {
                n.calledMethods.flatMap {
                    collectDtos(it, true)
//                    if (it.methodDetailed.isPrivate) collectDtos(it, true) else emptyList()
                }
            } else emptyList()
            return fromHere + fromChildren
        }

        val allDtos = collectDtos(node, includeNested = true)
        if (allDtos.isEmpty()) return@buildString

        appendLine("Используемые data-классы (DTO):")
        allDtos.forEach { dataClassInfo ->
            appendLine("kotlin")
            appendLine(dataClassInfo.classInfo.text.trim())
            appendLine("")
        }
    }


    private fun generateUsedEnums(node: CallNode): String = buildString {
        fun collectEnums(n: CallNode, includeNested: Boolean): Set<ClassInfoDetailed> {
            val fromHere = n.usedEnums
            val fromChildren = if (includeNested) {
                n.calledMethods.flatMap {
                    collectEnums(it, true)
//                    if (it.methodDetailed.isPrivate) collectEnums(it, true) else emptyList()
                }
            } else emptyList()
            return fromHere + fromChildren
        }

        val allEnums = collectEnums(node, includeNested = true)
        if (allEnums.isEmpty()) return@buildString

        appendLine("Используемые enum-классы:")
        allEnums.filter { it.classInfo.className != "FeatureFlags" }.forEach { enumInfo ->
            appendLine("kotlin")
            appendLine(enumInfo.classInfo.text.trim())
            appendLine("")
        }
    }

}