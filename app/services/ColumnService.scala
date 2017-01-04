package services

import model.{Column, Columns}
import scala.concurrent.Future

object ColumnService {

  def addColumn(column: Column): Future[String] = {
    Columns.add(column)
  }

  def deleteColumn(id: Long): Future[Int] = {
    Columns.delete(id)
  }

  def getColumn(id: Long): Future[Option[Column]] = {
    Columns.get(id)
  }

  def listAllColumns: Future[Seq[Column]] = {
    Columns.listAll
  }
}
