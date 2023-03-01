import java.sql.{Connection, PreparedStatement}

object ConvertPreparedStatement {
  def buildPreparedStatementSql(sql: String, map: Map[String, Any]): (String, List[Any]) = {
    val params = List[Any]()

    def next(_sql: String, _params: List[Any]): (String, List[Any]) = {
      if (!_sql.contains("${")) return (_sql, _params)
      val st = _sql.indexOf("${")
      val et = _sql.indexOf("}")
      val name = _sql.substring(st + 2, et)
      val value = map(name)
      next(_sql.substring(0, st) + "?" + _sql.substring(et + 1), _params :+ value)
    }

    next(sql, params)
  }

  def setParmas(pstmt: PreparedStatement, params: List[Any]) = {
    params.zipWithIndex.foreach { case (param, index) =>
      param match {
        case p: Int => pstmt.setInt(index + 1, p)
        case p: Any => pstmt.setString(index + 1, p.toString)
      }
    }
  }

  def main(args: Array[String]) = {
    val sql =
      """select url
        |     , p_dt
        |  from access_log
        | where p_dt = ${p_dt}
        |   and num between ${start} and ${end}
        |   and s_num = ${start}
        |   """.stripMargin

    val (buildSql, params) = buildPreparedStatementSql(sql, Map("p_dt" -> "20221031", "start" -> 1, "end" -> 10))
    println(buildSql)
    println(params)

    // DB Connection ..
    val conn: Connection = null
    val pstmt = conn.prepareStatement(buildSql)
    setParmas(pstmt, params)
    val res = pstmt.executeQuery()
    // ..
  }
}
