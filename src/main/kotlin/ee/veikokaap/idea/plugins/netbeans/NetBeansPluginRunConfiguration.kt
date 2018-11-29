package ee.veikokaap.idea.plugins.netbeans

import com.intellij.execution.Executor
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializer
import ee.veikokaap.idea.plugins.base.runconfig.BaseJavaApplicationRunConfiguration
import org.jdom.Element
import org.jetbrains.idea.maven.project.MavenConsoleImpl

class NetBeansPluginRunConfiguration(project: Project, factory: NetBeansConfigurationFactory, name: String)
  : BaseJavaApplicationRunConfiguration<NetBeansPluginRunConfiguration>(project, factory, name) {
  private var netBeansConfigurationBean = NetBeansPluginRunConfigurationBean()
  
  var netbeansInstallationDir: String
    get() = netBeansConfigurationBean.netbeansInstallation
    set(path) {
      netBeansConfigurationBean.netbeansInstallation = path
    }
  
  var netbeansUserDir: String
    get() = netBeansConfigurationBean.netbeansUserDir
    set(path) {
      netBeansConfigurationBean.netbeansUserDir = path
    }
  
  init {
    if (workingDirectory.isEmpty()) {
      workingDirectory = project.baseDir.canonicalPath ?: ""
    }
    if (netbeansUserDir.isEmpty()) {
      netbeansUserDir = "\${project.build.directory}/userdir";
    }
  }
  
  override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
    val state = NetBeansPluginCommandLineState(this, executor, environment)
    state.consoleBuilder = MavenConsoleImpl.createConsoleBuilder(this.project)
    return state
  }
  
  override fun getMainSettingsEditor(): SettingsEditor<NetBeansPluginRunConfiguration> {
    return NetBeansPluginSettingsEditor(project)
  }
  
  override fun readExternal(element: Element) {
    super.readExternal(element)
    XmlSerializer.deserializeInto(netBeansConfigurationBean, element)
  }
  
  override fun writeExternal(element: Element) {
    super.writeExternal(element)
    XmlSerializer.serializeInto(netBeansConfigurationBean, element)
  }
  
  override fun clone(): NetBeansPluginRunConfiguration {
    val clone = super.clone()
    clone.netBeansConfigurationBean = netBeansConfigurationBean.copy()
    return clone
  }
}

data class NetBeansPluginRunConfigurationBean(
    var netbeansInstallation: String = "",
    var netbeansUserDir: String = ""
)
