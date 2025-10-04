package custom.project.model

sealed class CallTreeBuildResult {
    data class Success(val node: CallNode) : CallTreeBuildResult()
    data class Error(val reason: String) : CallTreeBuildResult()
}
