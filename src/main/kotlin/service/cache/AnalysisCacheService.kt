package custom.project.service.cache

import com.github.benmanes.caffeine.cache.Caffeine
import custom.project.model.ProjectAnalysisResult
import java.io.File
import java.util.concurrent.TimeUnit

object AnalysisCacheService {
    private val cacheContext = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .maximumSize(20)
        .build<String, ProjectAnalysisResult>()

    fun withCache(path: String, loader: () -> ProjectAnalysisResult): ProjectAnalysisResult {
        val normalizedPath = File(path).canonicalPath
        return cacheContext.get(normalizedPath) {
            loader()
        }
    }
}