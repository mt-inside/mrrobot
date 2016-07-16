package mrrobot

import scala.concurrent.{Future,Promise}

import org.asynchttpclient._
import java.util.concurrent.Executor
import com.typesafe.scalalogging._

/* TODO: holds an unmanaged resource (a socket) should be Scala equivalent of
 * AutoClosable / IDisposable */
object HttpClient extends LazyLogging
{
  final case class HttpError(status: Integer) extends RuntimeException

  private val http = new DefaultAsyncHttpClient

  def get(url: String)(implicit exec: Executor): Future[String] =
  {
    logger.info(s"HTTP GET for $url")
    /* Annoyingly, execute() returns the library's own future type, not a scala
     * future, so we have to mess about */
    val p = Promise[String]()
    /* Their future type's doesn't have a map() or anything, and its addListener
     * takes a Runnable not a Callable, so we have to extract it to a variable
     * and call get() :( */
    //TODO: deal with chunkec transfer encoding
    val response = http.prepareGet(url).execute
    response.addListener(
      new Runnable {
        def run =
        {
          val r = response.get
          if (r.getStatusCode < 400) // No method for "good status"
            p.success(r.getResponseBody)
          else
            p.failure(HttpError(r.getStatusCode))
        }
      },
      exec
    )
    p.future
  }

  def close : Unit = http.close
}
