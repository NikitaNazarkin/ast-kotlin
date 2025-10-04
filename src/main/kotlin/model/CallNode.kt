package custom.project.model

data class CallNode(
    val calledMethods: Set<CallNode> = emptySet(),
    val classInfo: ClassInfo,
    val usedDataClasses: Set<ClassInfoDetailed> = emptySet(),
    val usedEnums: Set<ClassInfoDetailed> = emptySet(),
    val methodDetailed: MethodDetailedInfo
)
