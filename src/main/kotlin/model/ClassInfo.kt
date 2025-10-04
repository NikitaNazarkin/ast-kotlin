package custom.project.model

import org.jetbrains.kotlin.descriptors.ClassDescriptor

data class ClassInfo(
    val packageName: String,
    val className: String,
    val constructorDependencies: List<String>,
    val text: String,
    val isDataClass: Boolean,
    val isEnum: Boolean = false
) {
    lateinit var fqName: String
    lateinit var descriptor: ClassDescriptor
}
