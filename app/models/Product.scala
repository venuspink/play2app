package models

case class Product (
  ean: Long, name: String, description: String
)

object Product {

  implicit val fmt = play.api.libs.json.Json.format[Product]

  var products = Set(
    Product(5050L, "Glove", "descente glove"),
    Product(5060L, "Backpack", "northface pack"),
    Product(5070L, "MobilePhone", "apple iphone")
  )

  def findAll = products.toList.sortBy(_.ean)

  def findByEan(ean: Long) = products.find(_.ean == ean) //여기서 _ 언더바는 product를 뜻함

  def findByName(query: String) = products.filter(_.name.contains(query))

  /**
   * Deletes a product from the catalog.
   */
  def remove(product: Product) = {
    val oldProducts = products
    products = products - product
    oldProducts.contains(product)
  }

  /**
   * Adds a product to the catalog.
   */
  def add(product: Product) {
    products = products + product
  }
}
