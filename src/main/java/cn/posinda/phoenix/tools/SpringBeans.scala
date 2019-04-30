package cn.posinda.phoenix.tools

import org.springframework.context.{ApplicationContext, ApplicationContextAware}
import org.springframework.stereotype.Component

@Component
class SpringBeans extends ApplicationContextAware {

  var context: ApplicationContext = _

  override def setApplicationContext(applicationContext: ApplicationContext): Unit = context = applicationContext
}

