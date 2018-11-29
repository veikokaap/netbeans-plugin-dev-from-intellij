package ee.veikokaap.idea.plugins.netbeans

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project
import com.intellij.util.ui.EmptyIcon
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class NetBeansConfigurationFactory(type: NetBeansPluginConfigurationType) : ConfigurationFactory(type) {
  override fun createTemplateConfiguration(project: Project): RunConfiguration {
    return NetBeansPluginRunConfiguration(project, this, "NetBeans IDE plugin")
  }
  
  override fun getName(): String {
    return "NetBeans IDE plugin configuration factory"
  }
}

class NetBeansPluginConfigurationType : ConfigurationType {
  @Nls
  override fun getDisplayName(): String {
    return "NetBeans plugin"
  }
  
  @Nls
  override fun getConfigurationTypeDescription(): String {
    return "Run NetBeans IDE plugins"
  }
  
  override fun getIcon(): Icon {
    return EmptyIcon.ICON_16
  }
  
  override fun getId(): String {
    return "NETBEANS_IDE_PLUGIN_RUN_CONFIGURATION"
  }
  
  override fun getConfigurationFactories(): Array<ConfigurationFactory> {
    return arrayOf(NetBeansConfigurationFactory(this))
  }
}