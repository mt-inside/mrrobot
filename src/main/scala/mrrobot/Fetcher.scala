package mrrobot

import akka.actor._
import akka.pattern.pipe

import java.util.concurrent.Executor

object Fetcher
{
  /* Props */
  def props(url: String) = Props(classOf[Fetcher], url)

  /* Messages */
  final case object Fetch
  final case class Document(body: String)
  final case class Link(page: String, target: String)
//  final case class Graph(url: String)
}

class Fetcher(url: String) extends Actor with ActorLogging
{
  import context.dispatcher
  import Fetcher._

  def http = HttpClient 

  def receive = 
  {
    case Fetch =>
    {
      log.info(s"Fetcher for $url")

      //TODO to pre start?
      http.get(url).pipeTo(self)
    }
    case body: String =>
    {
      log.info(s"Successfully got body for $url")
      context.parent ! Document(body)
    }
    case error: Status.Failure =>
    {
      log.info(s"Error getting $url")
      context.parent ! Reader.Stop
    }
  }
}
