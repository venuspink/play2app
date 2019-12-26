package controllers
import javax.inject.Inject
import play.api.i18n.{I18nSupport, Langs}
import play.api.mvc._

class Application @Inject()(val controllerComponents: ControllerComponents)(langs: Langs) extends BaseController with I18nSupport{

  def index() = Action { implicit request: Request[AnyContent] =>
    print("asdsad : "+request.host)
    Ok(views.html.index("TORONTO111"))
    Redirect(routes.Products.list)
  }

}
