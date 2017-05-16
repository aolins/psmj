package controllers

import org.specs2.mutable.Specification

/**
  * Created by a.olins on 17/05/2017.
  */
class ApplicationTest extends Specification {

  "sample json" should {
    val a = new Application(null)(null)
    "have two countries" in {
      List("Andorra", "Zimbabwe") equals a.parseCountries(
        """
          [{"name":"Andorra","topLevelDomain":[".ad"]},{"name":"Zimbabwe","topLevelDomain":[".zw"]}]
        """.stripMargin)
    }
  }

}
