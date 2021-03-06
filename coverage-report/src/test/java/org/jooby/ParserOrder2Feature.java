package org.jooby;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jooby.test.ServerFeature;
import org.junit.Test;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

public class ParserOrder2Feature extends ServerFeature {

  Key<Set<Parser>> KEY = Key.get(new TypeLiteral<Set<Parser>>() {
  });

  {

    use((env, conf, binder) -> {
      Multibinder.newSetBinder(binder, Parser.class).addBinding().toInstance(new Parser() {

        @Override
        public Object parse(final TypeLiteral<?> type, final Context ctx) throws Exception {
          return ctx.next();
        }

        @Override
        public String toString() {
          return "p2";
        }

      });
    });

    parser(new Parser() {

      @Override
      public Object parse(final TypeLiteral<?> type, final Context ctx) throws Exception {
        assertEquals(
            "[p2, p1, p3, Basic, Collection, Optional, Enum, Upload, byte[], Date, LocalDate, Locale, bean, valueOf(String), fromString(String), forName(String), init(String)]",
            ctx.toString());
        return ctx.next();
      }

      @Override
      public String toString() {
        return "p1";
      }

    });

    parser(new Parser() {

      @Override
      public Object parse(final TypeLiteral<?> type, final Context ctx) throws Exception {
        return ctx.next();
      }

      @Override
      public String toString() {
        return "p3";
      }

    });

    get("/parser/order", req -> req.require(KEY));

  }

  @Test
  public void order() throws Exception {
    request()
        .get("/parser/order")
        .expect(
            "[p2, p1, p3, Basic, Collection, Optional, Enum, Upload, byte[], Date, LocalDate, Locale, bean, valueOf(String), fromString(String), forName(String), init(String)]");
  }
}
