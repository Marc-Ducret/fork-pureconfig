package pureconfig.module.magnolia

import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

import _root_.magnolia1._
import com.typesafe.config.{ConfigValue, ConfigValueFactory}

import pureconfig._
import pureconfig.generic.{CoproductHint, ProductHint}

/** An object containing Magnolia `combine` and `dispatch` methods to generate `ConfigWriter` instances.
  */
object MagnoliaConfigWriter {

  def combine[A](ctx: CaseClass[ConfigWriter, A])(implicit hint: ProductHint[A]): ConfigWriter[A] =
    if (ctx.typeInfo.full.startsWith("scala.Tuple")) combineTuple(ctx)
    else if (ctx.isValueClass) combineValueClass(ctx)
    else combineCaseClass(ctx)

  private def combineCaseClass[A](ctx: CaseClass[ConfigWriter, A])(implicit hint: ProductHint[A]): ConfigWriter[A] =
    new ConfigWriter[A] {
      def to(a: A): ConfigValue = {
        val fieldValues = ctx.params.map { param =>
          val valueOpt = param.typeclass match {
            case tc: WritesMissingKeys[param.PType @unchecked] =>
              tc.toOpt(param.deref(a))
            case tc =>
              Some(tc.to(param.deref(a)))
          }
          hint.to(valueOpt, param.label)
        }
        ConfigValueFactory.fromMap(fieldValues.flatten.toMap.asJava)
      }
    }

  private def combineTuple[A](ctx: CaseClass[ConfigWriter, A]): ConfigWriter[A] =
    new ConfigWriter[A] {
      override def to(a: A): ConfigValue =
        ConfigValueFactory.fromIterable(ctx.params.map { param => param.typeclass.to(param.deref(a)) }.toSeq.asJava)
    }

  private def combineValueClass[A](ctx: CaseClass[ConfigWriter, A]): ConfigWriter[A] =
    new ConfigWriter[A] {
      override def to(a: A): ConfigValue =
        ctx.params.map { param => param.typeclass.to(param.deref(a)) }.head
    }

  def dispatch[A](ctx: SealedTrait[ConfigWriter, A])(implicit hint: CoproductHint[A]): ConfigWriter[A] =
    new ConfigWriter[A] {
      def to(a: A): ConfigValue =
        ctx.choose(a) { subtype =>
          hint.to(subtype.typeclass.to(subtype.cast(a)), subtype.typeInfo.short)
        }
    }
}
