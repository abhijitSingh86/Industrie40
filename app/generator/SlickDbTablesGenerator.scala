package generator

/**
  * Created by billa on 2016-12-22.
  */
object SlickDbTablesGenerator {

  def main(args: Array[String]) {
    val o = new SlickDbTablesGenerator()
    o.run()
  }

}

class SlickDbTablesGenerator {
  var slickDriver = "slick.driver.MySQLDriver"

  var jdbcDriver = "com.mysql.jdbc.Driver"

  var url = "jdbc:mysql://localhost/dikscheduling"

  var outputFolder = "app"

  var pkg = "db"

  var user = "root"

  var password = "toor"


  def run() {
    slick.codegen.SourceCodeGenerator.main(
      Array(slickDriver, jdbcDriver, url, outputFolder, pkg, user, password)
    )
  }
}
