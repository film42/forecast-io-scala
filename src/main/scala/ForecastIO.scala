package com.film42.forecastioapi

import com.eclipsesource.json.JsonObject
import java.net.URL
import java.util.{Date, Scanner}
import spray.json._
import model.ForecastJsonProtocol._
import model._

class ForecastIO(apiKey: String, lat: String, lon: String, date: Date = new Date()) {

  // Timestamp constructor
  def this(apiKey: String, lat: String, lon: String, timestamp: Int) =
    this(apiKey, lat, lon, new Date(timestamp * 1000L))

  private val forecastJson = getForecast.asJsObject

  private def getForecast = {
    val ts = date.getTime / 1000
    val u = new URL(s"https://api.forecast.io/forecast/$apiKey/$lat,$lon,$ts")
    val s = new Scanner(u.openStream(), "UTF-8")
    try {
      s.useDelimiter("\\A").next().asJson
    } finally {
      s.close()
    }
  }

  def latitude = lat

  def longitude = lon

  def datetime = date

  def timezone: String = {
    forecastJson.getFields("timezone")(0).convertTo[String]
  }

  def offset: Int = {
    forecastJson.getFields("offset")(0).convertTo[Int]
  }

  def currently: CurrentDataPoint = {
    forecastJson.getFields("currently")(0).convertTo[CurrentDataPoint]
  }

  def minutely: Minutely = {
    forecastJson.getFields("minutely")(0).convertTo[Minutely]
  }

  def hourly: Hourly = {
    forecastJson.getFields("hourly")(0).convertTo[Hourly]
  }

  def flags: Flags = {
    val jsonString = forecastJson.getFields("flags")(0).toJson.toString()
    val json = JsonObject.readFrom(jsonString)
    new Flags(json)
  }

  def daily: Daily = {
    val jsonString = forecastJson.getFields("daily")(0).toJson.toString()
    val json = JsonObject.readFrom(jsonString)
    new Daily(json)
  }

  def alerts: List[Alert] = {
    val size = forecastJson.getFields("alerts").size
    if(size == 0) return List()

    val a = forecastJson.getFields("alerts")(0).convertTo[Alerts]
    a.alerts
  }

}
