package custom.project.extensions

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

/**
 * Находит все классы (DTO или Enum), которые упоминаются в теле функции
 * через локальные переменные, вызовы или аргументы конструкторов.
 */
fun KtNamedFunction.getUsedTypeDescriptors(bindingContext: BindingContext): Set<ClassDescriptor> {
    val fromTypeRefs = collectDescendantsOfType<KtTypeReference>().flatMap { typeReference ->
        typeReference.allInnerTypeDescriptors(bindingContext)
    }
    val fromProperties = collectDescendantsOfType<KtProperty>().flatMap { property ->
        property.allInnerTypeDescriptors(bindingContext)
    }
    val fromParameters = valueParameters.flatMap { param ->
        param.allInnerTypeDescriptors(bindingContext)
    }

    return (fromTypeRefs + fromProperties + fromParameters).toSet()
}

/**
 * Возвращает все вложенные типы (через generics) для KtTypeReference
 */
fun KtTypeReference.allInnerTypeDescriptors(bindingContext: BindingContext): List<ClassDescriptor> {
    val type = bindingContext[BindingContext.TYPE, this] ?: return emptyList()
    return type.extractInnerTypeDescriptors()
}

/**
 * Возвращает все вложенные типы (через generics) для KtProperty
 */
fun KtProperty.allInnerTypeDescriptors(bindingContext: BindingContext): List<ClassDescriptor> {
    val descriptor = bindingContext[BindingContext.VARIABLE, this] ?: return emptyList()
    return descriptor.type.extractInnerTypeDescriptors()
}

private fun KotlinType.extractInnerTypeDescriptors(): List<ClassDescriptor> {
    val declaration = this.constructor.declarationDescriptor
    if (declaration !is ClassDescriptor) return emptyList()

    // Если нет generic-параметров → возвращаем только этот класс
    if (arguments.isEmpty()) return listOf(declaration)

    // Собираем рекурсивно все generic-параметры
    val nested = arguments.flatMap { arg ->
        arg.type.extractInnerTypeDescriptors()
    }

    return listOf(declaration) + nested
}

/**
 * Возвращает все вложенные типы (через generics) для KtParameter
 */
fun KtParameter.allInnerTypeDescriptors(bindingContext: BindingContext): List<ClassDescriptor> {
    val descriptor = bindingContext[BindingContext.VALUE_PARAMETER, this] ?: return emptyList()
    return descriptor.type.extractInnerTypeDescriptors()
}

/**
 * Рекурсивно собирает все классы из KotlinType, включая generics
 */
//private fun KotlinType.extractInnerTypeDescriptors(): List<ClassDescriptor> {
//    val declaration = this.constructor.declarationDescriptor
//    if (declaration !is ClassDescriptor) return emptyList()
//
//    // Если нет generic-параметров → возвращаем только этот класс
//    if (arguments.isEmpty()) return listOf(declaration)
//
//    // Собираем рекурсивно все generic-параметры
//    val nested = arguments.flatMap { arg ->
//        arg.type.extractInnerTypeDescriptors()
//    }
//
//    return listOf(declaration) + nested
//}