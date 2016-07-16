package mrrobot

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Main extends App
{
  if (args.length < 1) usage else createActors(args(0))


  def createActors(url: String) =
  {
    implicit val system = ActorSystem("mrrobot")

    system.actorOf(Grapher.props(url, 3), "grapher")

    //implicit val timeout = Timeout(5.seconds)

    //system.terminate
  }

  def usage : Unit =
    System.err.println("Usage: mrrobot url")
}
