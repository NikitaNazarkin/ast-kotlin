package custom.project.model

import org.jetbrains.kotlin.resolve.BindingContext

data class ProjectAnalysisResult(
    val classes: List<ClassInfoDetailed>,
    val bindingContext: BindingContext
) {
    val enums: List<ClassInfoDetailed> = classes.filter { it.classInfo.isEnum }
    val dataClasses: List<ClassInfoDetailed> = classes.filter { it.classInfo.isDataClass }
}