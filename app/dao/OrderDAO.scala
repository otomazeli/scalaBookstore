package dao

/**
  * Created by constie on 23.04.2017.
  */

import javax.inject.Inject

import scala.concurrent.{Future, ExecutionContext}

import models.{Order, OrderREST}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

class OrderDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Orders = TableQuery[OrdersTable]

  def all(implicit ec: ExecutionContext): Future[Seq[OrderREST]] = {
    val query = Orders
    val results = query.result
    val futureCartItems = db.run(results)
    futureCartItems.map(
      _.map {
        z => OrderREST(totalAmount = z.totalAmount)
      }.toList
    )
  }

  def insert(order: Order): Future[Unit] = db.run(Orders += order).map { _ => () }

  def delete(id: Int): Future[Unit] = db.run(Orders.filter(_.id === id).delete).map ( _ => () )

  private class OrdersTable(tag: Tag) extends Table[Order](tag, "TOrders"){
    def id = column[Int]("ID",O.AutoInc, O.AutoInc)

    def totalAmount = column[Int]("TOTALAMOUNT")

    def * = (id, totalAmount) <> ((models.Order.apply _).tupled, models.Order.unapply _)
  }
}

