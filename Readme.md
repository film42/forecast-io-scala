Forecast IO v2 API wrapper in scala
===================================

I know there are at least two Java wrappers, but this will feel cleaner to anyone working on a Scala project and needs weather data.

Docum`

#Quick Examples:

###Standard US

```scala
ForecastIO.init("my api key")

val Success(forecast) = ForecastIO.forecast("45.157778", "-93.226944")

forecast.currently.summary //=> "Overcast"
```

###International

```scala
ForecastIO.init("my api key", "si")
```


