package custom.project.service

import custom.project.extensions.allInnerTypeDescriptors
import custom.project.extensions.getUsedTypeDescriptors
import custom.project.model.*
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.utils.IDEAPluginsCompatibilityAPI

/**
 * Сервис для построения дерева вызовов методов.
 */
object CallChainBuilder {
    /**
     * Построение дерева вызовов для метода.
     */
    fun buildCallTreeForMethod(
        className: String,
        methodName: String,
        analysisResult: ProjectAnalysisResult
    ): CallTreeBuildResult {
        println("Building call tree for method: $className.$methodName")

        val targetClassDetailed = analysisResult.classes.find { it.classInfo.className == className }
        if (targetClassDetailed == null) {
            val msg = "Class '$className' not found in analysis result."
            return CallTreeBuildResult.Error(msg)
        }

        val targetMethod = targetClassDetailed.methodsDetailed.find { it.name == methodName }
        if (targetMethod == null) {
            val msg = "Method '$methodName' not found in class '$className'."
            return CallTreeBuildResult.Error(msg)
        }

        return try {
            val node = buildCallTree(
                function = targetMethod,
                ownerClass = targetClassDetailed,
                analysisResult = analysisResult,
                visited = mutableSetOf()
            )
            println("Call tree built successfully for method: $className.$methodName")
            CallTreeBuildResult.Success(node)
        } catch (ex: Exception) {
            val msg = "Failed to build call tree: ${ex.message}"
            CallTreeBuildResult.Error(msg)
        }


    }

    /**
     * Рекурсивное построение дерева вызовов.
     */
    @OptIn(IDEAPluginsCompatibilityAPI::class)
    private fun buildCallTree(
        function: MethodDetailedInfo,
        ownerClass: ClassInfoDetailed,
        analysisResult: ProjectAnalysisResult,
        visited: MutableSet<String> = mutableSetOf()
    ): CallNode {
        val uniqueKey = "${function.name}.${ownerClass.classInfo.className}"

        if (uniqueKey in visited) {
            return CallNode(emptySet(), ownerClass.classInfo, emptySet(), emptySet(), function)
        }
        visited.add(uniqueKey)

        val calls = mutableSetOf<CallNode>()

        function.ktNamedFunction.bodyExpression?.forEachDescendantOfType<KtCallExpression> { callExpr ->
            println(callExpr.node)
            val resolvedCall = callExpr.getResolvedCall(analysisResult.bindingContext)

            if (resolvedCall == null) return@forEachDescendantOfType

            val targetClassDescriptor = resolvedCall.resultingDescriptor.containingDeclaration

            val targetOwnerClass =
                analysisResult.classes.find { it.classInfo.descriptor == targetClassDescriptor } ?: ownerClass

            val targetFunction =
                targetOwnerClass.methodsDetailed.find { it.descriptor == resolvedCall.resultingDescriptor }

            if (targetFunction != null) {
                calls += buildCallTree(targetFunction, targetOwnerClass, analysisResult, visited)
            }
        }

        val usedDto = mutableSetOf<ClassInfoDetailed>()
        val usedEnums = mutableSetOf<ClassInfoDetailed>()
        val usedTypes = function.ktNamedFunction.getUsedTypeDescriptors(analysisResult.bindingContext)

        analysisResult.dataClasses.forEach { if (it.classInfo.descriptor in usedTypes) usedDto += it }
        analysisResult.enums.forEach { if (it.classInfo.descriptor in usedTypes) usedEnums += it }

        val recursiveUsedTypes = expandTypesRecursive(usedDto, analysisResult)

        val allDto = recursiveUsedTypes.filter { it.classInfo.isDataClass }.toSet()
        val allEnums = recursiveUsedTypes.filter { it.classInfo.isEnum }.toSet() + usedEnums

        return CallNode(calls, ownerClass.classInfo, allDto, allEnums, function)
    }

    /**
     * Рекурсивно раскрывает DTO или Enum, включая вложенные типы из свойств и конструкторов,
     */
    private fun expandTypesRecursive(
        startTypes: Set<ClassInfoDetailed>,
        analysisResult: ProjectAnalysisResult,
        visited: MutableSet<ClassDescriptor> = mutableSetOf()
    ): Set<ClassInfoDetailed> {
        val result = mutableSetOf<ClassInfoDetailed>()
        val knownTypes = analysisResult.dataClasses + analysisResult.enums

        for (type in startTypes) {
            val descriptor = type.classInfo.descriptor
            if (!visited.add(descriptor)) continue
            result += type

            val nestedDescriptors = buildSet {
                type.ktClass.declarations.filterIsInstance<KtProperty>().forEach { property ->
                    addAll(property.allInnerTypeDescriptors(analysisResult.bindingContext))
                }

                type.ktClass.primaryConstructorParameters.forEach { param ->
                    addAll(param.allInnerTypeDescriptors(analysisResult.bindingContext))
                }
            }
            val nestedTypes = knownTypes.filter { it.classInfo.descriptor in nestedDescriptors }.toSet()

            result += expandTypesRecursive(nestedTypes, analysisResult, visited)
        }

        return result
    }
}