Forecast IO v2 API wrapper in scala
===================================

I know there are at least two Java wrappers, but this will feel cleaner to anyone working on a Scala project and needs weather data.

This is also completely immutable and thread-safe!

Review API Spec for specifics: https://developer.forecast.io/docs/v2

##Quick Examples:

###Standard US

```scala
val forecastIO = ForecastIO("my api key")

val Success(forecast) = forecastIO.forecast("45.157778", "-93.226944")

forecast.currently.summary //=> "Mostly Cloudy"
```

###International

```scala
val forecastIO = ForecastIO("my api key", "si")
```


