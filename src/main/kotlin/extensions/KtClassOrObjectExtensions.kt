package custom.project.extensions

import custom.project.model.ClassInfo
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

fun KtClassOrObject.getMethods(): List<KtNamedFunction> {
    return this.declarations.filterIsInstance<KtNamedFunction>()
}

fun KtClassOrObject.extractClassInfo(): ClassInfo {
    val packageName = this.containingKtFile.packageFqName.asString()
    val className = this.name ?: "UnknownClass"
    val constructorDependencies = this.extractConstructorDependencies()
    val isEnum = (this as? KtClass)?.isEnum() == true
    return ClassInfo(
        packageName = packageName,
        className = className,
        constructorDependencies = constructorDependencies,
        text = this.text,
        isDataClass = this.isData(),
        isEnum = isEnum
    )
}

private fun KtClassOrObject.extractConstructorDependencies(): List<String> {
    val constructor = this.primaryConstructor ?: return emptyList()
    return constructor.valueParameters.mapNotNull { it.typeReference?.text }
}