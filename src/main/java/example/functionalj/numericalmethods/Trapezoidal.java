package example.functionalj.numericalmethods;

import static functionalj.list.FuncList.listOf;
import static functionalj.list.FuncList.newListBuilder;
import static functionalj.stream.Step.StartAt;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Test;

class Trapezoidal {
    
    static interface Func extends DoubleUnaryOperator {}
    
    @Test
    void testTrapezoidalRule() {
        var function   = (Func) ((x) -> 3 + 2 * pow(x, 2) - pow(x, 3) + pow(2, x) - exp(-x));
        var startX     = -1.0;
        var stopX      = 3.0;
        var actualArea = 18.81838;
        
        var logs       = newListBuilder(String.class);
        for (var segment : listOf(1, 2, 4, 8, 16, 200)) {
            var stepSize = (stopX - startX) / segment;
            var xs       = StartAt(startX).step(stepSize).dropAfter(x -> x >= stopX);
            var ys       = xs.map(function);
            var area     = ys.mapTwo((value1, value2) -> (value1 + value2) * stepSize / 2).sum();
            var error    = abs((actualArea - area) / actualArea) * 100;
            logs.add(format("n = %d, I_%d = %f, error = %f%%\n", segment, segment, area, error));
        }
        assertEquals(
                      "n = 1, I_1 = 11.463862, error = 39.081567%\n"
                    + "n = 2, I_2 = 16.996172, error = 9.683128%\n"
                    + "n = 4, I_4 = 18.362751, error = 2.421192%\n"
                    + "n = 8, I_8 = 18.704443, error = 0.605457%\n"
                    + "n = 16, I_16 = 18.789897, error = 0.151359%\n"
                    + "n = 200, I_200 = 18.818202, error = 0.000944%\n",
                    logs.build().join());
    }
    
}
