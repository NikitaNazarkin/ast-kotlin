package custom.project.service.component

import custom.project.model.ClassInfoDetailed
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

/**
 * Класс для дообогащения классов.
 */
object PsiEnricher {

    fun enrich(classes: List<ClassInfoDetailed>, bindingContext: BindingContext) {
        classes.forEach { cls ->
            bindingContext[BindingContext.CLASS, cls.ktClass]?.let { classDescriptor ->
                cls.classInfo.descriptor = classDescriptor
                cls.classInfo.fqName = classDescriptor.fqNameSafe.asString()
            }
            cls.methodsDetailed.forEach { method ->
                bindingContext[BindingContext.FUNCTION, method.ktNamedFunction]?.let { functionDescriptor ->
                    method.descriptor = functionDescriptor
                    method.fqName = functionDescriptor.fqNameSafe.asString()
                }
            }
        }
    }
}