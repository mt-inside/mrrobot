package mrrobot

import akka.actor._
import java.net.URI

object Grapher
{
  /* Props */
  def props(url: String, maxDepth: Integer) = Props(classOf[Grapher], url, maxDepth)

  /* Messages */
}

class Grapher(url: String, maxDepth: Integer) extends Actor with ActorLogging
{
  import Grapher._
  import context._

  log.info(s"Graphing $url")
  val drawer = actorOf(Drawer.props)
  var children = Set.empty[ActorRef]
  var visited = Set.empty[String]
  children += watch(actorOf(Reader.props(url, maxDepth)))
  visited += url

  def receive = 
  {
    case Reader.Link(page, rel_target, depth) =>
    {
      val upage = new URI(page)
      val urel_target = new URI(rel_target)
      val uabs_target = upage.resolve(urel_target)
      val abs_target = uabs_target.toString

      drawer ! Drawer.AddLink(page, abs_target)
      if (!visited(abs_target) &&
          depth > 0 &&
          uabs_target.getHost.equals(upage.getHost))
      {
        children += watch(actorOf(Reader.props(abs_target, depth - 1)))
        visited += abs_target
      }
    }
    case Terminated(target) =>
    {
      children -= target
      if (children.isEmpty)
      {
        stop(drawer)
        stop(self)
        //assume we're the only grapher...
        system.terminate
      }
    }
  }
}
