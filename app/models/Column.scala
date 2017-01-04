package model

import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global


case class Column(id: Long, value: String)
case class ColumnFormData(value: String)

object ColumnForm {

  val form = Form(
    mapping(
      "text" -> nonEmptyText
    )(ColumnFormData.apply)(ColumnFormData.unapply)
  )
}

class ColumnTableDef(tag: Tag) extends Table[Column](tag, "text") {
  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def value = column[String]("text")

  override def * =
    (id, value) <>(Column.tupled, Column.unapply)
}


object Columns {

  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val columns = TableQuery[ColumnTableDef]

  def add(column: Column): Future[String] = {
    dbConfig.db.run(columns += column).map(res => "column successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(columns.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Column]] = {
    dbConfig.db.run(columns.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Column]] = {
    dbConfig.db.run(columns.result)
  }

}
