package custom.project.model

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction

data class MethodDetailedInfo(
    val name: String,
    val text: String,
    val classOwner: String,
    val isPrivate: Boolean,
    val ktNamedFunction: KtNamedFunction,
) {
    lateinit var descriptor: FunctionDescriptor
    lateinit var fqName: String
}