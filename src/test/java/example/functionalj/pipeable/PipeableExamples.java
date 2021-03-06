package example.functionalj.pipeable;

import static functionalj.functions.StrFuncs.replaceAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import functionalj.list.FuncList;
import functionalj.pipeable.PipeLine;
import functionalj.pipeable.Pipeable;


class PipeableExamples {
    
    @Test
    void testPipeable() {
        var str = Pipeable.of("Hello world.")
                .pipeTo(
                    String::toUpperCase,
                    replaceAll("\\.", "!!")
                );
        assertEquals("HELLO WORLD!!", str);
    }
    
    @Test
    void testPipeLine() {
        var readFile = PipeLine
                .of  (String.class)
                .then(Paths ::get)
                .then(Files ::readAllBytes)
                .then(String::new)
                .thenReturn();
          
        var fileNames = FuncList.of("file1.txt", "file2.txt");
        var fileContents = fileNames.map(readFile);
        // Notice that the error is suppressed.
        assertEquals("[null, null]", fileContents.toString());
    }
    
    static class User implements Pipeable<User> {
        private String name;
        public User(String name) { this.name = name; }
        public String name() { return name; }
        @Override
        public User __data() throws Exception { return this; }
    }
    
    @Test
    void testPipeableClass() {
        var user = new User("root");
        assertEquals("User name: root", user.pipeTo(User::name, "User name: "::concat));
    }
    
}
