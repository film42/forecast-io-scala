package com.film42.forecastioapi.model

import spray.json._
import com.eclipsesource.json._
import java.util.Date

sealed trait DT { def datetime: Date }

case class Alert(title: String, time: Int, expires: Int, description: String, uri: String)
  extends DT { def datetime = new Date(time * 1000L) }
case class Alerts(alerts: Array[Alert])

case class MinuteDataPoint(time: Int, precipIntensity: Double, precipProbability: Double)
  extends DT { def datetime = new Date(time * 1000L) }
case class Minutely(summary: String, icon: String, data: Array[MinuteDataPoint])

class Flags(json: JsonObject) {
  private def asStringArray(v: JsonValue) =
    v.asArray.values.toArray.map(x => x.toString)

  def sources: Array[String] = asStringArray( json.get("sources") )
  def station(source: String): Array[String] = {
    try asStringArray( json.get(s"$source-stations") )
    catch { case e: Exception => Array() }
  }
  def units: String = json.get("units").asString
}

case class CurrentDataPoint(
  time: Int,
  summary: String ,
  icon: String ,
  nearestStormDistance: Option[Double],
  nearestStormBearing: Option[Double],
  precipIntensity: Double,
  precipProbability: Double,
  temperature: Double,
  apparentTemperature: Double,
  dewPoint: Double,
  humidity: Double,
  windSpeed: Double,
  windBearing: Double,
  visibility: Double,
  cloudCover: Double,
  pressure: Double,
  ozone: Option[Double]) extends DT { def datetime = new Date(time * 1000L) }

case class HourDataPoint(
  time: Int,
  summary: String ,
  icon: String ,
  precipIntensity: Double,
  precipProbability: Double,
  temperature: Double,
  apparentTemperature: Double,
  dewPoint: Double,
  humidity: Double,
  windSpeed: Double,
  windBearing: Double,
  visibility: Double,
  cloudCover: Double,
  pressure: Double,
  ozone: Double) extends DT { def datetime = new Date(time * 1000L) }

case class Hourly(
  summary: String,
  icon: String,
  data: Array[HourDataPoint])


/*
  Why is this so ugly? Because case class limits of 22, that's why!
 */
class Daily(json: JsonObject) {
  def summary: String =
    try json.get("summary").asString
    catch { case e: Exception => "" }
  def icon: String  =
    try json.get("icon").asString
    catch { case e: Exception => "" }
  def data: Array[DayDataPoint] = {
    val data = json.get("data").asArray.values.toArray
    data.map(x => new DayDataPoint(x.asInstanceOf[JsonValue].asObject))
  }
}

class DayDataPoint(json: JsonObject) extends DT {
  def time: Int = json.get("time").asInt
  def datetime = new Date(time * 1000L)
  def summary: String  = json.get("summary").asString
  def icon: String  = json.get("icon").asString
  def sunriseTime: Int = json.get("sunriseTime").asInt
  def sunsetTime: Int = json.get("sunsetTime").asInt
  def moonPhase: Double = json.get("moonPhase").asDouble
  def precipIntensity: Double = json.get("precipIntensity").asDouble
  def precipIntensityMax: Double = json.get("precipIntensityMax").asDouble
  def precipProbability: Double = json.get("precipProbability").asDouble
  def temperatureMin: Double = json.get("temperatureMin").asDouble
  def temperatureMinTime: Int = json.get("temperatureMinTime").asInt
  def temperatureMinDateTime: Date = new Date(temperatureMinTime * 1000L)
  def temperatureMax: Double = json.get("temperatureMax").asDouble
  def temperatureMaxTime: Int = json.get("temperatureMaxTime").asInt
  def temperatureMaxDateTime: Date = new Date(temperatureMaxTime * 1000L)
  def apparentTemperatureMin: Double = json.get("apparentTemperatureMin").asDouble
  def apparentTemperatureMinTime: Int = json.get("apparentTemperatureMinTime").asInt
  def apparentTemperatureMinDateTime: Date = new Date(apparentTemperatureMinTime * 1000L)
  def apparentTemperatureMax: Double = json.get("apparentTemperatureMax").asDouble
  def apparentTemperatureMaxTime: Int = json.get("apparentTemperatureMaxTime").asInt
  def apparentTemperatureMaxDateTime: Date = new Date(apparentTemperatureMaxTime * 1000L)
  def dewPoint: Double = json.get("dewPoint").asDouble
  def humidity: Double = json.get("humidity").asDouble
  def windSpeed: Double = json.get("windSpeed").asDouble
  def windBearing: Double = json.get("windBearing").asDouble
  def visibility: Double = json.get("visibility").asDouble
  def cloudCover: Double = json.get("cloudCover").asDouble
  def pressure: Double = json.get("pressure").asDouble
  def ozone: Double = json.get("ozone").asDouble
}

object ForecastJsonProtocol extends DefaultJsonProtocol {
  implicit val currentDataPointFormat = jsonFormat17(CurrentDataPoint)
  implicit val hourDataPointFormat = jsonFormat15(HourDataPoint)
  implicit val hourlyDataFormat = jsonFormat3(Hourly)
  implicit val alertDataFormat = jsonFormat5(Alert)
  implicit val minuteDataFormat = jsonFormat3(MinuteDataPoint)
  implicit val minutelyFormat = jsonFormat3(Minutely)

  // Root is an Array
  implicit object AlertsApiResultsFormat extends RootJsonFormat[Alerts] {
    def read(value: JsValue) = Alerts(value.convertTo[Array[Alert]])
    def write(obj: Alerts) = obj.alerts.toJson
  }
}