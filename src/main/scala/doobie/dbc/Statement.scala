package doobie
package dbc

import scalaz._
import Scalaz._
import scalaz.effect.IO
import java.sql

object statement extends DWorld[sql.Statement] with StatementOps[sql.Statement] {

  type Statement[+A] = Action[A]

  private[dbc] def run[A](a: Action[A], l: Log[LogElement], s: sql.Statement): IO[A] = 
    eval(a, l, s).map(_._2)

}

trait StatementOps[A <: sql.Statement] { this: DWorld[A] => 
  
  def addBatch(sql: String): Action[Unit] =
    primitive(s"addBatch($sql)", _.addBatch(sql))

  def cancel: Action[Unit] =
    primitive(s"cancel", _.cancel)

  def clearBatch: Action[Unit] =
    primitive(s"clearBatch", _.clearBatch)

  def clearWarnings: Action[Unit] =
    primitive(s"clearWarnings", _.clearWarnings)

  def close: Action[Unit] =
    primitive("close", _.close)

  def execute(sql: String): Action[Boolean] =
    primitive(s"execute($sql)", _.execute(sql))

  // def execute(sql: String, autoGeneratedKeys: Int): Action[Boolean] =
  //   ???

  // def execute(sql: String, columnIndexes: Array[Int]): Action[Boolean] =
  //   ???

  // def execute(sql: String, columnNames: Array[String]): Action[Boolean] =
  //   ???

  def executeBatch: Action[List[Int]] =
    primitive(s"executeBatch", _.executeBatch.toList)

  def executeQuery[A](sql: String)(k: ResultSet[A]): Action[A] =
    for {
      l <- log
      r <- primitive(s"executeQuery", _.executeQuery(sql))
      a <- resultset.run(k, l, r).ensuring(resultset.run(resultset.close, l, r)).liftIO[Action]
    } yield a

  def executeUpdate(sql: String): Action[Int] =
    primitive(s"executeUpdate($sql)", _.executeUpdate(sql))

  // def executeUpdate(sql: String, autoGeneratedKeys: Int): Action[Int] =
  //   ???

  // def executeUpdate(sql: String, columnIndexes: Array[Int]): Action[Int] =
  //   ???

  // def executeUpdate(sql: String, columnNames: Array[String]): Action[Int] =
  //   ???

  def getConnection[A](k: Connection[A]): Action[A] =
    for {
      l <- log
      c <- primitive(s"", _.getConnection)
      a <- connection.run(k, l, c).liftIO[Action]
    } yield a

  def getFetchDirection: Action[FetchDirection] =
    primitive(s"getFetchDirection", _.getFetchDirection).map(FetchDirection.unsafeFromInt)

  def getFetchSize: Action[Int] =
    primitive(s"getFetchSize", _.getFetchSize)

  def getGeneratedKeys(k: ResultSet[A]): Action[A] =
    for {
      l <- log
      r <- primitive(s"getGeneratedKeys", _.getGeneratedKeys)
      a <- resultset.run(k, l, r).ensuring(resultset.run(resultset.close, l, r)).liftIO[Action]
    } yield a

  def getMaxFieldSize: Action[Int] =
    primitive(s"getMaxFieldSize", _.getMaxFieldSize)

  def getMaxRows: Action[Int] =
    primitive(s"getMaxRows", _.getMaxRows)

  def getMoreResults: Action[Boolean] =
    primitive(s"getMoreResults", _.getMoreResults)

  def getMoreResults(current: Int): Action[Boolean] =
    primitive(s"getMoreResults($current)", _.getMoreResults(current))

  def getQueryTimeout: Action[Int] =
    primitive(s"getQueryTimeout", _.getQueryTimeout)

  def getResultSet(k: ResultSet[A]): Action[A] =
    for {
      l <- log
      r <- primitive(s"getResultSet", _.getResultSet)
      a <- resultset.run(k, l, r).ensuring(resultset.run(resultset.close, l, r)).liftIO[Action]
    } yield a

  def getResultSetConcurrency: Action[ResultSetConcurrency] =
    primitive(s"getResultSetConcurrency", _.getResultSetConcurrency).map(ResultSetConcurrency.unsafeFromInt)

  def getResultSetType: Action[ResultSetType] =
    primitive(s"getResultSetType", _.getResultSetType).map(ResultSetType.unsafeFromInt)

  def getUpdateCount: Action[Int] =
    primitive(s"getUpdateCount", _.getUpdateCount)

  def getWarnings: Action[sql.SQLWarning] = 
    primitive(s"getWarnings", _.getWarnings)

  def setCursorName(name: String): Action[Unit] =
    primitive(s"setCursorName($name)", _.setCursorName(name))

  def setEscapeProcessing(enable: Boolean): Action[Unit] =
    primitive(s"setEscapeProcessing($enable)", _.setEscapeProcessing(enable))

  def setFetchDirection(direction: FetchDirection): Action[Unit] =
    primitive(s"setFetchDirection($direction)", _.setFetchDirection(direction.toInt))

  def setFetchSize(rows: Int): Action[Unit] =
    primitive(s"setFetchSize($rows)", _.setFetchSize(rows))

  def setMaxFieldSize(max: Int): Action[Unit] =
    primitive(s"setMaxFieldSize($max)", _.setMaxFieldSize(max))

  def setMaxRows(max: Int): Action[Unit] =
    primitive(s"setMaxRows($max)", _.setMaxRows(max))

  def setQueryTimeout(seconds: Int): Action[Unit] =
    primitive(s"setQueryTimeout($seconds)", _.setQueryTimeout(seconds))

}
