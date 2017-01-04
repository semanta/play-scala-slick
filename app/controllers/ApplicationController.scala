package controllers

import model.{Column, ColumnForm, ColumnTableDef, User, UserForm, UserTableDef}
import play.api.Play
import play.api.mvc._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent._
import scala.concurrent.duration._
import service.UserService
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data.Form
import play.api.data.Forms._
import services.ColumnService
import play.api.libs.json._
import javax.inject.Inject


class ApplicationController @Inject() (webJarAssets: WebJarAssets) extends Controller {
  def index = Action.async { implicit request =>
    val columns = Await.result(ColumnService.listAllColumns, Duration.Inf)
    val allusers =  Await.result(UserService.listAllUsers, Duration.Inf)
    println(allusers.head.value)
    UserService.listAllUsers map { users =>
      Ok(views.html.index(UserForm.form, users, columns, webJarAssets))
    }
  }

  def create() = Action.async { implicit request =>
    val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
    val column = TableQuery[ColumnTableDef];
    val resultFuture = dbConfig.db.run(column.schema.create)
    ColumnService.listAllColumns map { columns =>
      Ok(views.html.create(ColumnForm.form, columns))
    }
  }

  def addColumn() = Action.async { implicit request =>
    ColumnForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.create(errorForm, Seq.empty[Column]))),
      data => {
        val newColumn = Column(0, data.value)
        ColumnService.addColumn(newColumn).map(res =>
          Redirect(routes.ApplicationController.create())
        )
      })
  }


  def addUser() = Action.async { implicit request =>
    val columns = Await.result(ColumnService.listAllColumns, Duration.Inf)
    val jsonResponse = request.body.asJson
    val jsonValue: JsValue = jsonResponse match {
      case None => null
      case Some(s: JsValue) => s
    }
    val minifiedString: String = Json.stringify(jsonValue)
    println(jsonResponse)
    //UserForm.form.bindFromRequest.fold(
      // if any error in submitted data
      //errorForm => Future.successful(Ok(views.html.index(errorForm, Seq.empty[User], columns, webJarAssets))),
      //data => {
        val newUser = User(0, minifiedString)
        UserService.addUser(newUser).map(res =>
          Redirect(routes.ApplicationController.index())
        )
      //})
  }


  def deleteUser(id: Long) = Action.async { implicit request =>
    UserService.deleteUser(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }
}