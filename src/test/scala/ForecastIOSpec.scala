package com.film42.forecastioapi

import com.eclipsesource.json.JsonObject
import com.film42.forecastioapi.model._
import org.scalatest._
import com.film42.forecastioapi._
import com.film42.forecastioapi.extras._
import scala.io.Source
import scala.util.Success
import scala.util.Failure
import java.util.Date

class ForecastIOSpec extends FunSpec {
  val apiKey = System.getenv("FORECASTIO_APIKEY")

  describe("Starting the test suite") {
    it("should have a set apiKey system var"){
      assert(apiKey != null)
    }
  }

  describe("Test Locations") {
    it("can find salt lake city UT") {
      val Success(LocationPoint(lon, lat)) = Location.search("salt lake city ut")
      assert(!lon.isEmpty)
      assert(!lat.isEmpty)
    }

    it("returns blank LocationPoint when bad data given") {
      val loc = Location.search("jfdowij0930jrf")
      assert(loc.isInstanceOf[Failure[LocationPoint]])
    }

    it("can get a forecast with a LocationPoint") {
      val loc = Location.search("salt lake city ut")
      assert(loc.isInstanceOf[Success[LocationPoint]])
      val Success(location) = loc

      val resp = ForecastIO(apiKey).forecast(location)
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)
    }

    it("can get a forecast with an older date") {
      val loc = Location.search("salt lake city ut")
      assert(loc.isInstanceOf[Success[LocationPoint]])
      val Success(location) = loc

      val date = new Date(1265076122 * 1000L)

      val resp = ForecastIO(apiKey).forecast(location, date)
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)
      assert(forecast.datetime == date)
    }
  }

  describe("Base ForecastIO things") {
    it("will return error with a bad api key") {
      val resp = ForecastIO("badkey").forecast("45.157778", "-93.226944")
      assert(resp.isInstanceOf[Failure[Forecast]])
    }

    it("will return error with no lon / lat") {
      val resp = ForecastIO(apiKey).forecast("", "")
      assert(resp.isInstanceOf[Failure[Forecast]])
    }

    it("can get with lat / lon") {
      val resp = ForecastIO(apiKey).forecast("45.157778", "-93.226944")
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)
    }

    it("can get with lat / lon and date") {
      val date = new Date(1265076122 * 1000L)
      val resp = ForecastIO(apiKey).forecast("45.157778", "-93.226944", date)
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)
      assert(forecast.datetime == date)
    }

    it("can get with lat / lon and si units") {
      val resp = ForecastIO(apiKey, "si").forecast("45.157778", "-93.226944")
      assert(resp.isInstanceOf[Success[Forecast]])
      val Success(forecast) = resp
      assert(forecast.currently.summary != null)
    }
  }

  describe("JSON protocols") {
    import spray.json._
    import model.ForecastJsonProtocol._

    lazy val jsonString = Source.fromURL(this.getClass.getResource("/test_response_nyc.json")).getLines().mkString("\n")
    lazy val forecastJson = jsonString.asJson.asJsObject

    it("parses timezone") {
      assert(forecastJson.getFields("timezone")(0).convertTo[String] == "Asia/Bishkek")
    }

    it("parses offset") {
      assert(forecastJson.getFields("offset")(0).convertTo[Int] == 6)
    }

    it("parses CurrentDataPoint") {
      assert(forecastJson.getFields("currently")(0).convertTo[CurrentDataPoint] != null)
    }

    // it("parses Minutely") {
    //   assert(forecastJson.getFields("minutely")(0).convertTo[Minutely] != null)
    // }

    it("parses Hourly") {
      assert(forecastJson.getFields("hourly")(0).convertTo[Hourly] != null)
    }

    it("parses Daily") {
      // Use separate json parser until Case Class limit is lifted
      val jsonString = forecastJson.getFields("daily")(0).toJson.toString()
      val json = JsonObject.readFrom(jsonString)
      assert(new Daily(json) != null)
    }

    it("parses Flags") {
      // Use separate json parser until Case Class limit is lifted
      val jsonString = forecastJson.getFields("flags")(0).toJson.toString()
      val json = JsonObject.readFrom(jsonString)
      assert(new Flags(json) != null)
    }

  }
}

