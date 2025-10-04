package custom.project.service.prompt

import custom.project.enums.TestLibraryType
import custom.project.model.CallNode

interface PromptIntro {

    fun supports(libraryType: TestLibraryType): Boolean

    fun generateIntro(node: CallNode): String

}