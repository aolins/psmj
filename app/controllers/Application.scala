package controllers

import com.mongodb.casbah.commons.MongoDBObject
import models.Applicant
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.libs.ws.ning.NingWSClient
import play.api.mvc._
import play.filters.csrf.CSRFConfig

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
class Application(val messagesApi: MessagesApi)(implicit csrfConfig: CSRFConfig) extends Controller with I18nSupport {


  private val countries = Seq( (null, "Choose Country"))

  def getCountries():Seq[(String, String)] = {
      val url = "https://restcountries.eu/rest/v1/region/Europe"

    val wsClient = NingWSClient()
    val wsr = wsClient
      .url(url)
      .get()

    val result = Await.ready(wsr, Duration.Inf).value.get

    return result match {
      case Success(wsResponse) =>{
        if (! (200 to 299).contains(wsResponse.status)) {
          sys.error(s"Received unexpected status ${wsResponse.status} : ${wsResponse.body}")
          countries
        }else {
          val names = parseCountries(wsResponse.body)
          (null, "Choose Country") :: names.map(x => (x, x))
        }
      }
      case Failure(t) =>{
        countries
      }
    }
  }


  def parseCountries(body: String): List[String] = {
    val js = Json.parse(body)
    (js \\ "name").map(_.as[String]).toList
  }

  def getForm = Action { implicit request => Ok(views.html.getForm(Application.createReqForm, getCountries))}

  def createReq = Action { implicit request =>
    val formValidationResult = Application.createReqForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.getForm( formWithErrors, getCountries()))
    }, { widget =>
      MongoFactory.create(widget)
      Ok(views.html.postForm(widget))
    })
  }
}

object Application {
  val createReqForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "sex" -> nonEmptyText,
      "age" -> number(0,150),
      "country" -> nonEmptyText
    )(Applicant.apply)(Applicant.unapply)
  )
}

object MongoFactory {
  private val SERVER = "localhost"
  private val PORT   = 27017
  private val DATABASE = "nodetest"
  private val COLLECTION = "userdata"
  val collection = com.mongodb.casbah.MongoClient(SERVER, PORT)(DATABASE)(COLLECTION)

  def create(a:Applicant): Unit ={
    val obj = MongoDBObject("name" -> a.name, "sex" -> a.sex, "age" -> a.age , "country" -> a.country, "dateCreated" -> System.currentTimeMillis)
    collection.insert(obj)
  }
}