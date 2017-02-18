package gf

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.mongodb.MongoClient
import gf.app.core.ServiceController
import gf.app.roulette.RouletteController
import gf.infra.core.DemoWallet
import gf.infra.roulette.RouletteRepo
import gf.model.core.{Money, Wallet}
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.web._
import org.springframework.context.annotation.{Bean, Configuration, Import}
import org.springframework.core.convert.converter.Converter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter


//noinspection TypeAnnotation
@Configuration
@Import(Array(
  classOf[DispatcherServletAutoConfiguration],
  classOf[EmbeddedServletContainerAutoConfiguration],
  classOf[ErrorMvcAutoConfiguration],
  classOf[HttpEncodingAutoConfiguration],
  classOf[HttpMessageConvertersAutoConfiguration],
  classOf[JacksonAutoConfiguration],
  classOf[ServerPropertiesAutoConfiguration],
  classOf[PropertyPlaceholderAutoConfiguration],
  classOf[WebMvcAutoConfiguration],
  classOf[MongoAutoConfiguration]
  //,classOf[SessionAutoConfiguration]
)) class Config {

  @Bean def mappingJackson2HttpMessageConverter() =
    new MappingJackson2HttpMessageConverter((new ObjectMapper() with ScalaObjectMapper)
      .registerModule(DefaultScalaModule)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))


  @Bean def stringToMoney() = new Converter[String, Money] {
    override def convert(source: String): Money = Money(BigDecimal(source))
  }

  @Bean def wallet() = new DemoWallet()

  @Bean def serviceController(wallet: Wallet) = new ServiceController(wallet)

  @Bean def rouletteController(mongo: MongoClient, wallet: Wallet) = new RouletteController(new RouletteRepo(mongo), wallet)
}


