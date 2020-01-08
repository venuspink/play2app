package dao

import javax.inject.{Inject, Singleton}
import scalikejdbc._
import models.Product

@Singleton
class ProductsDAO {

  implicit val session = AutoSession

  def testSelect: Option[Any]  = {
    val accounts = sql"select count(*) as CNT from products".toMap.single.apply()
//      try sql"select count(1) from products".toMap.list.apply().get
//      catch {
//        case e: Exception =>
//          println("DB ERROR : "+e)
//          505
//      }

    for(result <- accounts) println("값:"+result.get("cnt").getOrElse("BBB"))

    val res: Option[Any] = accounts.head.get("cnt")

    return res
  }
}

object Product extends SQLSyntaxSupport[Product] {
  implicit val session = AutoSession
  def create = {
    val id = sql"insert into products (ean,name,description) values(111, '오징어땅콩', '땅콩과자')".update.apply()
  }
}
