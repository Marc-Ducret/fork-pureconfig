package pureconfig.module.magnolia.auto

import magnolia1._

import pureconfig.generic.{CoproductHint, ProductHint}
import pureconfig.module.magnolia.MagnoliaConfigReader
import pureconfig.{ConfigReader, Exported}

/** An object that, when imported, provides implicit `ConfigReader` instances for value classes, tuples, case classes
  * and sealed traits. The generation of `ConfigReader`s is done by Magnolia.
  */
object reader extends AutoDerivation[ConfigReader] {
  def join[A](ctx: CaseClass[ConfigReader, A]): ConfigReader[A] =
    MagnoliaConfigReader.combine(ctx)(ProductHint.default)

  def split[A](ctx: SealedTrait[ConfigReader, A]): ConfigReader[A] =
    MagnoliaConfigReader.dispatch(ctx)(CoproductHint.default)
}

