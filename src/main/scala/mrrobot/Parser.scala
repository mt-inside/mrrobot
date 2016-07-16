package mrrobot

import akka.actor._

object Parser
{
  /* Props */
  def props(url: String, depth: Integer) = Props(classOf[Parser], url, depth)

  /* Messages */
  final case class Parse(body: String)
  final case class Link(target: String)
}

class Parser(url: String, depth: Integer) extends Actor with ActorLogging
{
  import Parser._

  def receive = 
  {
    case Parse(body) =>
    {
      log.info(s"Parsing body of $url")
      /* Proper (X)HTML parsing can be offloaded to a library.
       * What's difficult is knowing where in the tree valid links might be
       * found */
      val anchor_tag = """(?i)<a\s*([^>]*)>[^<]*</a>""".r
      val href_attr = """(?i)href\s*=\s*(?:"([^"]*)"|'([^']*)')""".r

      for
      {
        anchor_match <- anchor_tag.findAllMatchIn(body)
        href <- anchor_match.subgroups
        href_matches <- href_attr.findAllMatchIn(href)
        url <- href_matches.subgroups if (url != null)
      } context.parent ! Link(url)

      context.parent ! Reader.Stop
    }
  }
}
