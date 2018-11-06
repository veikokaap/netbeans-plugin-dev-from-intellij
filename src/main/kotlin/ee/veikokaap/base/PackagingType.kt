package ee.veikokaap.base

import org.jetbrains.idea.maven.project.MavenProject

sealed class PackagingType(val name: String)

class NonTychoPackagingType(name: String) : PackagingType(name)
sealed class TychoPackagingType(name: String) : PackagingType(name)
sealed class NetBeansModulePackagingType(name: String) : PackagingType(name)

object EclipsePluginPackagingType : TychoPackagingType("eclipse-plugin");
object EclipseTestPluginPackagingType : TychoPackagingType("eclipse-test-plugin");
object EclipseFeaturePackagingType : TychoPackagingType("eclipse-feature");
object EclipseRepositoryPackagingType : TychoPackagingType("eclipse-repository");
object EclipseApplicationPackagingType : TychoPackagingType("eclipse-application");
object EclipseUpdateSitePackagingType : TychoPackagingType("eclipse-update-site");
object EclipseTargetDefinitionPackagingType : TychoPackagingType("eclipse-target-definition");
object P2InstallableUnitPackagingType : TychoPackagingType("p2-installable-unit");

object NbmPackagingType : NetBeansModulePackagingType("nbm")

val MavenProject.packagingType: PackagingType
  get() = when (packaging) {
    EclipsePluginPackagingType.name -> EclipsePluginPackagingType
    EclipseTestPluginPackagingType.name -> EclipseTestPluginPackagingType
    EclipseFeaturePackagingType.name -> EclipseFeaturePackagingType
    EclipseRepositoryPackagingType.name -> EclipseRepositoryPackagingType
    EclipseApplicationPackagingType.name -> EclipseApplicationPackagingType
    EclipseUpdateSitePackagingType.name -> EclipseUpdateSitePackagingType
    EclipseTargetDefinitionPackagingType.name -> EclipseTargetDefinitionPackagingType
    P2InstallableUnitPackagingType.name -> P2InstallableUnitPackagingType
    NbmPackagingType.name -> NbmPackagingType
    else -> NonTychoPackagingType(packaging)
  }