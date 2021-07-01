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
        var function   = (Func)((x) -> 3 + 2 * pow(x, 2) - pow(x, 3) + pow(2, x) - exp(-x));
        var startX     = -1.0;
        var stopX      =  3.0;
        var actualArea = 18.81838;
        
        var resultLogs = newListBuilder(String.class);
        for (var segmentCount : listOf(1, 2, 4, 8, 16, 200)) {
            var stepSize = (stopX - startX) / segmentCount;
            var xs       = StartAt(startX).step(stepSize).dropAfter(x -> x >= stopX);
            var ys       = xs.map(function);
            var area     = ys.mapTwo((y1, y2) -> (y1 + y2) * stepSize / 2).sum();
            var error    = abs((actualArea - area) / actualArea) * 100;
            resultLogs.add(format("n = % 4d, Area = %10.7f, error = %10.7f%%\n", segmentCount, area, error));
        }
        assertEquals("n =    1, Area = 11.4638622, error = 39.0815670%\n"
                   + "n =    2, Area = 16.9961722, error =  9.6831278%\n"
                   + "n =    4, Area = 18.3627508, error =  2.4211923%\n"
                   + "n =    8, Area = 18.7044427, error =  0.6054574%\n"
                   + "n =   16, Area = 18.7898966, error =  0.1513594%\n"
                   + "n =  200, Area = 18.8182024, error =  0.0009438%\n",
                    resultLogs.build().join());
    }
    
}
