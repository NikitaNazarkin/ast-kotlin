package custom.project.mapper

import custom.project.model.MethodDetailedInfo
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

object FunctionInfoMapper {

    fun of(
        ktNamedFunction: KtNamedFunction,
        ktClassOrObject: KtClassOrObject,
    ): MethodDetailedInfo {
        return MethodDetailedInfo(
            name = ktNamedFunction.name ?: "<not found>",
            text = ktNamedFunction.text,
            classOwner = ktClassOrObject.name ?: "<not found>",
            isPrivate = ktNamedFunction.hasModifier(KtTokens.PRIVATE_KEYWORD),
            ktNamedFunction = ktNamedFunction
        )
    }
}