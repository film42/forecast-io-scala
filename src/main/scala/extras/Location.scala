package com.film42.forecastioapi.extras

import java.util.Scanner
import java.net.URL
import com.eclipsesource.json._
import scala.util.{Failure, Success, Try}

case class LocationPoint(lat: String, lon: String)

object Location {

  private def getLocationJson(locationName: String) = {
    val es =  java.net.URLEncoder.encode(locationName, "ISO-8859-1")
    val u = new URL(s"http://maps.googleapis.com/maps/api/geocode/json?address=$es&sensor=true")
    val s = new Scanner(u.openStream(), "UTF-8")
    try {
      s.useDelimiter("\\A").next()
    } catch {
      case e: Exception => throw new Exception(e.getMessage)
    } finally {
      s.close()
    }
  }

  def search(locationName: String): Try[LocationPoint] = {
    val raw = {
      try { getLocationJson(locationName) }
      // Ew gross, this hack
      catch { case e: Exception => "{\"results\" : []}" }
    }

    val json = JsonObject.readFrom(raw)
    val results = json.get("results").asArray.values

    if(results.size > 0) {
      val result = results.get(0)
      val geo = result.asObject.get("geometry")
      val location = geo.asObject.get("location")
      val lat = location.asObject.get("lat").toString
      val lon = location.asObject.get("lng").toString

      Success(LocationPoint(lat, lon))
    } else {
      Failure(new Exception("invalid search string or network error"))
    }
  }

}