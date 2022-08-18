package pureconfig.module.magnolia.auto

import scala.language.experimental.macros
import scala.reflect.ClassTag

import magnolia1.*
import pureconfig.generic.{CoproductHint, ProductHint}
import pureconfig.module.magnolia.MagnoliaConfigWriter
import pureconfig.{ConfigWriter, Exported}

/** An object that, when imported, provides implicit `ConfigWriter` instances for value classes, tuples, case classes
  * and sealed traits. The generation of `ConfigWriter`s is done by Magnolia.
  */
object writer extends AutoDerivation[ConfigWriter] {
  def join[A](ctx: CaseClass[ConfigWriter, A]): ConfigWriter[A] =
    MagnoliaConfigWriter.combine(ctx)(ProductHint.default)

  def split[A](ctx: SealedTrait[ConfigWriter, A]): ConfigWriter[A] =
    MagnoliaConfigWriter.dispatch(ctx)(CoproductHint.default)
}
