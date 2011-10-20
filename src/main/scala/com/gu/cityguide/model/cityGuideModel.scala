package com.gu.cityguide.model


case class City(
  name: String,
  urlName: String,
  latitude: Option[Double],
  longitude: Option[Double],
  mapZoom: Int,
  picture: Option[String],
  intro: Option[String],
  whatsOnUrl: Option[String],
  transportUrl: Option[String],
  weatherUrl: Option[String],
  itemsUrl: String,
  categories: List[Category]
)

case class Category(
  id: String,
  name: String,
  urlName: String,
  itemsUrl: String,
  subCategories: List[SubCategory]
)

case class SubCategory(
  name: String,
  urlName: String,
  image: String,
  itemsUrl: String
)

case class Item(
  city: String,
  category: String,
  subcategory: String,
  name: String,
  urlName: String,
  url:Option[String],
  latitude: Option[Double],
  longitude: Option[Double],
  description: Option[String],
  image: Option[String],
  caption: Option[String],
  byline: Option[String],
  bylineUrl: Option[String],
  guardianArticleUrl: Option[String],
  guardianArticleTitle: Option[String],
  mapCentreLatitude: Option[Double],
  mapCentreLongitude: Option[Double],
  mapCentreZoom: Option[Int]
)