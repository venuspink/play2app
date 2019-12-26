package controllers

import javax.inject.Inject
import models.Product
import play.api.i18n.{I18nSupport, Lang, Langs, MessagesApi}
import play.api.mvc._

class Products @Inject() (val controllerComponents: ControllerComponents)(langs: Langs, messagesApi: MessagesApi) extends BaseController with I18nSupport{

  def list = Action { implicit request =>

    val products = Product.findAll

    val lang: Lang = langs.availables.head

    val title = messagesApi("product.snack")(lang)

    println("TITLE : ",title)

    Ok(views.html.products.list(products)(title))

  }

}
