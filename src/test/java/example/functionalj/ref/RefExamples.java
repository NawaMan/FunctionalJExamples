package example.functionalj.ref;

import static functionalj.ref.Run.With;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import functionalj.ref.Ref;


class RefExamples {
    
    static Ref<Function<String, String>> greeting = Ref.ofValue(RefExamples::defaultGreeting);
    static Ref<Consumer<String>>         println  = Ref.ofValue(System.out ::println);
    
    
    private static String defaultGreeting(String name) {
        return format("Hello %s!!", name);
    }
    
    static void greet(String name) {
        var greetingString = greeting.value().apply(name);
        println.value().accept(greetingString);
    }
    
    static void main(String[] args) {
        // Production
        greet("Jack");
    }
    
    @Test
    void testDefaultMessage() {
        var logs = new ArrayList<String>();
        With(println.butWith(logs::add))
        .run(()-> {
            greet("Jack");
        });
        assertEquals("[Hello Jack!!]", logs.toString());
    }
    
    @Test
    void testCustomMessage() {
        var logs = new ArrayList<String>();
        With(println .butWith(logs::add),
             greeting.butWith(name -> "What's up " + name + "?"))
        .run(()-> {
            greet("Jack");
        });
        assertEquals("[What's up Jack?]", logs.toString());
    }
    
}
