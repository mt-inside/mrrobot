package mrrobot

import akka.actor._
import java.io._

object Drawer
{
  /* Props */
  def props = Props[Drawer]

  /* Messages */
  final case class AddLink(source: String, target: String)
}

class Drawer extends Actor with ActorLogging
{
  import Drawer._

  log.info(s"Drawer")

  val pw = new PrintWriter(new File("output.dot"))
  pw.write("digraph {\n")
  var labels = Map.empty[String, Int]
  var next = 0

  def receive = 
  {
    case AddLink(page, target) =>
    {
      log.info(s"Adding link $page -> $target")
      // Least functional code ever, but I am very tired
      if (!labels.contains(page)) { labels += (page -> next); next+=1 }
      if (!labels.contains(target)) { labels += (target -> next); next+=1 }
      val i = labels(page)
      val j = labels(target)
      pw.write(s"""$i [label="$page"];\n""")
      pw.write(s"""$j [label="$target"];\n""")
      pw.write(s"$i -> $j;\n")
    }
  }

  override def postStop =
  {
    pw.write("}\n")
    pw.close
  }
}
