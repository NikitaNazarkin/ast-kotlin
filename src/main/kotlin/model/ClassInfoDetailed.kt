package custom.project.model

import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

data class ClassInfoDetailed(
    val ktClass: KtClassOrObject,
    val methodsDetailed: List<MethodDetailedInfo> = emptyList(),
    val classInfo: ClassInfo,
    val ktFile: KtFile
)