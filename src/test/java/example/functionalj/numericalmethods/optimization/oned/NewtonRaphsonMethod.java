package example.functionalj.numericalmethods.optimization.oned;

import static functionalj.function.Apply.$;
import static functionalj.function.Func.f;
import static functionalj.list.FuncList.iterate;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import functionalj.function.Func1;
import functionalj.types.Struct;


@Struct
interface IterationSpec {
    double x();
    double fx();
    double dfx();
}

interface DoubleFunc extends Func1<Double, Double> {
    default DiffertiableFunc withDiff(DoubleFunc diff) {
        return new DiffertiableFunc(this, diff);
    }
}

@Struct
interface DiffertiableFuncSpec extends Func1<Double, Iteration> {
    DoubleFunc func();
    DoubleFunc diff();
    
    @Override
    default Iteration applyUnsafe(Double x) throws Exception {
        return new Iteration(x, func().apply(x), diff().apply(x));
    }
}

class NewtonRaphsonMethod {
    
    @Test
    void test() {
        var x0           = 2.0;
        var epsilon      = 1e-10;
        var maxLoop      = 1000;
        var learningRate = 0.05;
        
        var func     = (DoubleFunc) (x -> 1*x*x*x - 1*x*x       - 1);
        var function = func.withDiff(x ->           3*x*x - 2*x    );
        
        var iteration0     = $(function, x0);
        var newtonUpdate   = f((Iteration it) -> it.x - it.fx / it.dfx);
        var gradientUpdate = f((Iteration it) -> it.x - learningRate*it.fx);
        
        var resultNR =
                iterate    (iteration0, $(function, newtonUpdate))
                .dropAfter (it -> abs(it.fx) < epsilon)
                .limit     (maxLoop)
                .toList()
                ;
        
        var resultGD =
                iterate    (iteration0, $(function, gradientUpdate))
                .takeUntil (it -> abs(it.fx) < epsilon)
                .limit     (maxLoop)
                .toList()
                ;
        
        assertEquals(
                "Newton (  6 iterations): "
                + "Result:{ Value: Iteration["
                  + "x: 1.4655712318767877, "
                  + "fx: 6.927791673660977E-14, "
                  + "dfx: 3.51255464336096] "
                + "}\n",
                format("Newton (%3d iterations): %s\n", resultNR.size(), resultNR.lastResult()));
        
        assertEquals(
                "GD (120 iterations): "
                + "Result:{ Value: Iteration["
                  + "x: 1.4655712319092813, "
                  + "fx: 1.142046457403012E-10, "
                  + "dfx: 3.5125546435817014] "
                + "}\n",
                format("GD (%3d iterations): %s\n", resultGD.size(), resultGD.lastResult()));
    }
    
}
