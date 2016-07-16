package mrrobot

import akka.actor._
import akka.actor.SupervisorStrategy._

object Reader
{
  /* Props */
  def props(url: String, depth: Integer) = Props(classOf[Reader], url, depth)

  /* Messages */
  final case class Link(page: String, target: String, depth: Integer)
  final case object Done
  final case object Stop
}

class Reader(url: String, depth: Integer) extends Actor with ActorLogging
{
  import Reader._

  log.info(s"Going $url at depth $depth")
  val fetcher = context.actorOf(Fetcher.props(url))
  fetcher ! Fetcher.Fetch

  //TODO: context setrecieveTimeout
  //TODO: handle ReceiveTimeout message, kill children, Done to parent
  
  override val supervisorStrategy = AllForOneStrategy()
  {
    case e => log.warning(s"Error Going $url: " + e); context.parent ! Done; SupervisorStrategy.Stop
  }

  def receive = fetching
  def fetching: Receive =
  {
    case Fetcher.Document(doc) =>
    {
      val parser = context.actorOf(Parser.props(url, depth))
      parser ! Parser.Parse(doc)
      context.become(parsing(parser))
    }
    case Stop =>
    {
      context.stop(fetcher)
      context.stop(self)
    }
  }
  def parsing(parser: ActorRef): Receive =
  {
    case Parser.Link(target) =>
    {
      context.parent ! Link(url, target, depth)
    }
    case Stop =>
    {
      context.parent ! Done
      context.stop(parser)
      context.stop(self)
    }
  }
}
