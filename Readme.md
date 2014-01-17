Forecast IO v2 API wrapper in scala
===================================

I know there are at least two Java wrappers, but this will feel cleaner to anyone working on a Scala project and needs weather data.

Quick Example:

```scala
val forecast = new ForecastIO(apiKey, lon, lat)

forecast.currently.summary //=> "Overcast"
```
