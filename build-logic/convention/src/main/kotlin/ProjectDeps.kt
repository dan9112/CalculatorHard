import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.findCatalogLibrary(name: String) = libs
    .findLibrary(name)
    .get()

internal fun Project.findCatalogRequiredVersion(name: String) = libs
    .findVersion(name)
    .get()
    .requiredVersion
