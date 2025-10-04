package custom.project.service.component

import custom.project.extensions.extractClassInfo
import custom.project.extensions.getMethods
import custom.project.mapper.FunctionInfoMapper
import custom.project.model.ClassInfoDetailed
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile


/**
 * Класс для сбора классов.
 */
object ClassCollector {

    /**
     * Сбор классов из списка PSI файлов. Учитывает все вложенные классы, а также все классы из файла.
     * @param psiFiles Список PSI файлов.
     * @return Список классов.
     * @see ClassInfoDetailed
     */
    fun collect(psiFiles: Collection<KtFile>): List<ClassInfoDetailed> =
        psiFiles.flatMap { file ->
            file.declarations
                .filterIsInstance<KtClassOrObject>()
                .flatMap { ktClass -> collectRecursive(file, ktClass) }
        }

    private fun collectRecursive(file: KtFile, ktClass: KtClassOrObject): List<ClassInfoDetailed> {
        val current = buildClassInfo(file, ktClass)
        val nested = ktClass.declarations
            .filterIsInstance<KtClassOrObject>()
            .flatMap { collectRecursive(file, it) }
        return listOf(current) + nested
    }

    private fun buildClassInfo(file: KtFile, ktClass: KtClassOrObject): ClassInfoDetailed {
        val methods = ktClass.getMethods().map { FunctionInfoMapper.of(it, ktClass) }
        val classInfo = ktClass.extractClassInfo()
        return ClassInfoDetailed(
            ktClass,
            methodsDetailed = methods,
            classInfo = classInfo,
            ktFile = file
        )
    }
}