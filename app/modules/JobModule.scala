package modules

import jobs.AuthTokenCleaner
import jobs.Scheduler
import net.codingwell.scalaguice.ScalaModule
import play.api.libs.concurrent.AkkaGuiceSupport

/**
  * The job module.
  */
class JobModule extends ScalaModule with AkkaGuiceSupport {

  /**
    * Configures the module.
    */
  def configure(): Unit = {
    bindActor[AuthTokenCleaner]("auth-token-cleaner")
    bind[Scheduler].asEagerSingleton()
  }
}
