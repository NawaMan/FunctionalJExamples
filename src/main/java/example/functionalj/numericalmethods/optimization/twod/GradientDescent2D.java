package example.functionalj.numericalmethods.optimization.twod;

import static example.functionalj.numericalmethods.optimization.twod.Iteration.theIteration;
import static functionalj.list.FuncList.iterate;
import static java.lang.Math.abs;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import functionalj.function.Func2;
import functionalj.list.intlist.IntFuncList;
import functionalj.types.Struct;


public class GradientDescent2D {
    
    static interface XYFunc extends Func2<Double, Double, Double> {}
    
    @Struct
    void DiffertiableFunction(
            XYFunc func, 
            XYFunc dfdx, 
            XYFunc dfdy) {}
    
    @Struct
    void Iteration(
            double x, 
            double y, 
            double fxy, 
            double dfx, 
            double dfy) {}
    
    static DiffertiableFunction differtiableFunction(XYFunc func) {
        return differtiableFunction(func, 1.0e-10);
    }
    
    static DiffertiableFunction differtiableFunction(XYFunc func, double delta) {
        return new DiffertiableFunction.Builder()
                .func(func)
                .dfdx((x, y) -> (func.apply(x + delta, y) - func.apply(x, y))/delta)
                .dfdy((x, y) -> (func.apply(x, y + delta) - func.apply(x, y))/delta)
                .build  ();
    }
    
    static Iteration newIteration(Double x, Double y, DiffertiableFunction function) {
        return new Iteration(
                x, y,
                function.func().apply(x, y),
                function.dfdx().apply(x, y),
                function.dfdy().apply(x, y));
    }
    
    public static void main(String[] args) {
        var startTime = System.currentTimeMillis();
        
        var function = differtiableFunction((x, y) -> 2*x*y + 2*x - x*x - 2*y*y);
        
        var x0       = -1.0;
        var y0       =  1.0;
        var stepSize = 0.2;
        var epsilon  = 1.0e-10;
        var maxLoop  = 100;
        
        var iteration0 = newIteration(x0, y0, function);
        var iterations =
                iterate(iteration0, iter -> {
                    var newX = iter.x + stepSize*iter.dfx;
                    var newY = iter.y + stepSize*iter.dfy;
                    return newIteration(newX, newY, function);
                })
                .dropAfter((prev, iter) -> abs(prev.fxy - iter.fxy) < epsilon)
                .limit    (maxLoop)
                .toList()
                ;
        
        var calculated = iterations.lastResult().get();
        System.out.printf("Newton (%3d iterations): %s\n", iterations.size(), calculated);
        
        Iteration exact = newIteration(2.0, 1.0, function);
        System.out.printf("   %10s vs %10s => %10s\n", "Calculated",   "Exact  ",   "Error  ");
        System.out.printf("X: %10f vs %10f => %10f\n", calculated.x,   exact.x,   calculated.x   - exact.x);
        System.out.printf("Y: %10f vs %10f => %10f\n", calculated.y,   exact.y,   calculated.y   - exact.y);
        System.out.printf("F: %10f vs %10f => %10f\n", calculated.fxy, exact.fxy, calculated.fxy - exact.fxy);
        
        System.out.printf("Calculation time: %d ms\n", System.currentTimeMillis() - startTime);
        
        var xData = IntFuncList.wholeNumbers(iterations.size()).mapToDouble().boxed();
        var yData = iterations.mapToDouble(theIteration.fxy).boxed();
        
        XYChart chart = QuickChart.getChart("Gradient Descent 2D", "iteration", "f", "f", xData, yData);
        new SwingWrapper<>(chart).displayChart();
    }
    
}
