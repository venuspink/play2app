package models

case class Product (
  ean: Long, name: String, description: String
)

object Product {
  var products = Set(
    Product(5050L, "Glove", "descente glove"),
    Product(5060L, "Backpack", "northface pack"),
    Product(5070L, "MobilePhone", "apple iphone")
  )

  def findAll = products.toList.sortBy(_.ean)
}
