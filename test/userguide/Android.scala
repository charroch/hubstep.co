package userguide

import org.specs2.mock.Mockito
import org.specs2.Specification

class Android extends Specification  with Mockito {
  def is =
    """
      |A First Level Header
      |====================
      |
      |A Second Level Header
      |---------------------
      |
      |Now is the time for all good men to come to
      |the aid of their country. This is just a
      |regular paragraph.
      |
      |The quick brown fox jumped over the lazy
      |dog's back.
      |
      |### Header 3
      |
      |> This is a blockquote.
      |>
      |> This is the second paragraph in the blockquote.
      |>
      |> ## This is an H2 in a blockquote
    """.stripMargin ^
      p ^
      "The 'Hello world' string should" ^
      "contain 11 characters" ! e1 ^
      "start with 'Hello'" ! e2 ^
      "end with 'world'" ! e3 ^
      end

  def e1 = "Hello world" must have size (11)

  def e2 = "Hello world" must startWith("Hello")

  def e3 = "Hello world" must endWith("world")
}
