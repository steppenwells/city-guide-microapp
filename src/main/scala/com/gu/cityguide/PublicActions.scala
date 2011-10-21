package com.gu.cityguide

import model.{City, Category, Item}
import org.scalatra.ScalatraFilter
import org.slf4j.{LoggerFactory, Logger}
import net.liftweb.json._
import net.liftweb.json.Extraction._
import net.liftweb.json.DefaultFormats
import org.scalatra.scalate.ScalateSupport
import com.gu.openplatform.contentapi.Api
import com.gu.openplatform.contentapi.connection.JavaNetHttp

class PublicActions extends ScalatraFilter with ScalateSupport {

  protected val log = LoggerFactory.getLogger(getClass)
  implicit val formats = DefaultFormats

  before() { contentType = "text/html" }

  get("/trail/:city/:cat") {
    val cityTrailItems = CityGuideClient.getItemsFor(params("city"), params("cat"))
    val randomItem = util.Random.shuffle(cityTrailItems).head
    layoutTemplate("templates/trailComponent.ssp", "item" -> randomItem)
  }

  get("/images/:city/:cat") {
    val cityTrailItems = CityGuideClient.getItemsFor(params("city"), params("cat"))
    val city = CityGuideClient.getCity(params("city"))
    val category = CityGuideClient.getCategoryFor(params("city"), params("cat"))
    val randomItems = util.Random.shuffle(cityTrailItems).take(9)
    val grid = List(
      randomItems.slice(0, 3),
      randomItems.slice(3, 6),
      randomItems.slice(6, 9)
    )

    layoutTemplate("templates/catImages.ssp", "data" -> CategoryWithItems(city.get, category.get, grid))
  }

  get("/passnotes") {
    val passnotes = ApiClient.item.itemId("theguardian/series/pass-notes").showFields("all").results
    layoutTemplate("templates/passnotes.ssp", "passnotes" -> passnotes)
  }

  get("/passnote/*") {
    val path = params("splat")
    val passnote = ApiClient.item.itemId(path).showFields("all").content.get
    val body = passnote.fields.get("body")
    println(body)
    body
    //layoutTemplate("templates/passnotes.ssp", "passnotes" -> passnotes)
  }

  error { case e => {
      log.error(e.toString)
      val stackTrace = e.getStackTraceString.split("\n") map { "\tat " + _ } mkString "\n"
      e.toString + "\n" + stackTrace
    }
  }

  val tempBody = "<p><strong>Age:</strong> Launched this month.</p><p><strong>Appearance:</strong> Hand-held HAL.</p><p><strong>HAL?</strong> As in the talking computer from 2001: A Space Odyssey. Think of Siri as HAL's much more helpful little sister.</p><p><strong>All right, but what is she?</strong> She's a feature on Apple's new iPhone 4S, a&nbsp;sort of all-purpose voice-activated personal assistant. She makes appointments, sends texts, checks the weather, finds local businesses and answers just about any question you'd care to ask.</p><p><strong>Like the internet?</strong> Well, yes, but only if the internet could talk back, would occasionally mishear you and was only available on an expensive new phone.</p><p><strong>And was a woman. Bit sexist, no?</strong> She's only a woman in the US, Australia and Germany. In Britain and France, she's a man.</p><p><strong>So why are we calling her \"she\"?</strong> Because that's what the techies and trendies raving about her online are calling her.</p><p><strong>Raving about her? Is she really that useful?</strong> Not useful, no. Most of her functions are easier to do manually, and she can currently access maps and business directories in the US only. It's just fun to ask her stupid questions.</p><p><strong>Really?</strong> Yep. So much fun, in fact, that several websites have sprung up to chronicle her responses. Ask her, for example, for her favourite colour, and she replies \"My favourite colour is... well, I don't know how to say it in your language. It's sort of greenish, but with more dimensions.\"</p><p><strong>I'm in love.</strong> You're not the only one. Several users have already proposed to Siri, to which she responds \"My end user licensing agreement does not cover marriage. My apologies.\"</p><p><strong>She's even funnier than us.</strong> High praise indeed.</p><p><strong>So where can I buy one of these new iPhones?</strong> \"Sorry, I can only look for businesses, maps and traffic in the United States.\"</p><p><strong>I guess I'll look that one up myself, then. </strong>That would definitely be easier, yes.</p><p><strong>Do say:</strong> \"What's the meaning of life, Siri?\"</p><p><strong>Don't say:</strong> \"This is the best thing since Furbies.\"</p><!-- Guardian Watermark: technology/2011/oct/19/pass-notes-siri-iphone-apple|2011-10-21T10:05:06+01:00|847f106631d772c8b10cfd4aa654b6d757f69415 -->"
}

case class CategoryWithItems(city: City, category: Category, items: List[List[Item]])

object ApiClient extends Api with JavaNetHttp {
  apiKey = Some("techdev-partner")
}

object CityGuideClient extends JavaNetHttp {

  implicit val formats = DefaultFormats

  val cityGuideApiRoot = "http://localhost:8080"

  def getItemsFor(city: String, cat: String): List[Item] = {
    val url = cityGuideApiRoot + "/city/" + city + "/" + cat
    val r = GET(url)
    parse(r.body).children.map(_.extract[Item]).toList
  }

  def getCategoryFor(city: String, cat: String): Option[Category] = {
    val url = cityGuideApiRoot + "/city/" + city
    val r = GET(url)
    parse(r.body).children.map(_.extract[Category]).toList.find(_.urlName == cat)
  }

  def getCity(city: String): Option[City] = {
    val url = cityGuideApiRoot + "/cities"
    val r = GET(url)
    parse(r.body).children.map(_.extract[City]).toList.find(_.urlName == city)
  }

}
