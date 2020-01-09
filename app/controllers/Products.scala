package controllers

import dao.ProductsDAO
import javax.inject.Inject
import models.Product
import play.api.data.Form
import play.api.i18n.{I18nSupport, Lang, Langs, Messages, MessagesApi}
import play.api.mvc._
import play.api.data.Forms._
import play.api.libs.json._

import scala.concurrent.Future

class Products @Inject() (productsDAO: ProductsDAO)(val controllerComponents: ControllerComponents)(langs: Langs, messagesApi: MessagesApi)
                          extends BaseController with I18nSupport{

  val enLang = Lang("en")

  implicit  val lang: Lang = langs.availables.head

  def list = Action { implicit request =>

    val products = Product.findAll

//    implicit  val lang: Lang = langs.availables.head

    val title = messagesApi("product.snack")(enLang)

    println("TITLE : " + title)

    val result: Option[Any] = productsDAO.testSelect

    Ok(views.html.products.list(products)(title)(result))

  }

  /**
   * 상세
   * @param ean
   * @return
   */
  def show(ean: Long) = Action { implicit request =>

//    val availableLangs: Seq[Lang] = langs.availables.filter(_ == Lang("en"))
//    println("availableLangs :" + availableLangs)

//    implicit  val messages = messagesApi.preferred(availableLangs)

    Product.findByEan(ean).map { product =>
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)

  }

  private def eanCheck(ean: Long) = {
    ean match {
//      case (_ : Long)  => println("EAN value is long")
      case x if x < 10000 => true
      case _ => false
    }
//    def sumDigits(digits: IndexedSeq[(Char, Int)]): Int = {
//      digits.map { _._1 }.map { _.toInt }.sum
//    }
//
//    val (singles, triples) = ean.toString.reverse.zipWithIndex.partition {
//      _._2 % 2 == 0
//    }
//
//    (sumDigits(singles) + sumDigits(triples) * 3) % 10 == 0
  }

  private def isUniqueEan(ean: Long): Boolean = Product.findByEan(ean).isEmpty
  private val productForm = makeProductForm("validation.ean.duplicate", isUniqueEan(_))

  private def makeProductForm(error: String, constraint: (Long) => Boolean) = Form(
    mapping(
      "ean" -> longNumber.verifying("validation.ean", eanCheck _).verifying(error, constraint), //첫번째로 입력형식을 체크하고, 두번째로 중복체크
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  def newProduct = Action { implicit request =>
    val form = if(request.flash.get("error").isDefined) {
      val errorForm = productForm.bind(request.flash.data)
      errorForm
    }else{
      productForm
    }

    Ok(views.html.products.edit(form))

  }

  def edit(ean: Long) = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      updateProductForm(ean).bind(request.flash.data)
    else
      updateProductForm(ean).fill(Product.findByEan(ean).get)

    Ok(views.html.products.edit(form, Some(ean)))
  }

  private def updateProductForm(ean: Long) =
    makeProductForm("validation.ean.duplicate", { newEan =>
      newEan == ean || isUniqueEan(newEan)
    })

  def update(ean: Long) = Action { implicit request =>
    if (Product.findByEan(ean).isEmpty) {
      println("NotFound")
      NotFound
    }else {
      val updatedProductForm = updateProductForm(ean).bindFromRequest()

      updatedProductForm.fold(
        hasErrors = { form =>
          Redirect(routes.Products.edit(ean)).flashing(Flash(form.data) +
            ("error" -> Messages("validation.errors")))
        },
        success = { updatedProduct =>
          Product.remove(Product.findByEan(ean).get)
          Product.add(updatedProduct)
          val successMessage = "success" -> Messages("products.update.success", updatedProduct.name)
          Redirect(routes.Products.show(updatedProduct.ean)).flashing(successMessage)
        }
      )
    }
  }

  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct()).flashing(Flash(form.data) +
          ("error" -> Messages("validation.errors")))
      },
      success = { newProduct =>
        Product.add(newProduct)
        val successMessage = ("success" -> Messages("products.new.success", newProduct.name))
        Redirect(routes.Products.show(newProduct.ean)).flashing(successMessage)
      }
    )
  }

  def create = Action.async {

    dao.Product.create

    val pt = new Product(100,"TTT","2222")

    println("시작 : " + java.time.LocalDateTime.now())

    Thread sleep 1 * 5000L //5초 sleep

    println("끝 : "+ java.time.LocalDateTime.now())



    Future.successful( Ok(Json.toJson(pt)))

  }


}
