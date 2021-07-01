package example.functionalj.effect;

import static functionalj.function.Func.f;
import static functionalj.functions.RegExMatchResult.theResults;
import static functionalj.functions.StrFuncs.matches;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import functionalj.list.FuncList;
import functionalj.list.ImmutableFuncList;
import functionalj.promise.DeferAction;
import functionalj.store.Store;
import functionalj.stream.StreamPlus;
import functionalj.task.Task;


class EffectExamples {
    
    @Test
    void basicEffect() throws InterruptedException {
        var logs = new ArrayList<String>();
        
        // The preparation
        var loadFile = f((String fileName) -> {
            return DeferAction
                .from(()->Files.readAllBytes(Paths.get(fileName)))
                .start()
                .getPromise();
        });
        
        var latch = new CountDownLatch(1);
        
        // Use it in the business logic code
        loadFile
        .apply("fileNotFound.txt")
        .onComplete(result -> {
            // Process result
            logs.add(result.toString());
            
            latch.countDown();
        });
        
        latch.await();
        
        // Notice the error is sent in the same channel
        assertEquals("[Result:{ Exception: java.nio.file.NoSuchFileException: fileNotFound.txt }]", logs.toString());
    }
    
    // TODO - Enable this.
    @Disabled("Some problems. Will solve later.")
    @Test
    void basicTask() throws IOException {
        // Define task
        var wordCountOf = f((String fileName) -> 
                Task.from(()->Files.readAllBytes(Paths.get(fileName)))
                .map(String::new)
                .map(matches("[a-zA-Z]+"))
                .map(theResults.texts)
                .map(StreamPlus::size)
        );
        // Define operations -> Notice that this is a generic operation -- no mention of Task.
        var compareWordCount = f((Integer count1, Integer count2) -> {
            return (count1 == count2) ? "Same size."
                 : (count1 >  count2) ? "First file is larger."
                                      : "Second file is larger.";
        });
        
        // Declare tasks
        var task1 = wordCountOf.apply("../LICENSE");
        var task2 = wordCountOf.apply("../.github/workflows/maven.yml");
        // Compose the tasks
        var compareTask = compareWordCount.applyTo(task1, task2);
        
        // At this point, nothing is run.
        
        // Actually run
        assertEquals("First file is larger.", compareTask.createAction().getResult().get());
    }
    
    @Test
    void storeExample() {
        var apppend = f((String str, ImmutableFuncList<String> list)-> list.append(str).toImmutableList());
        
        var list = FuncList.of("One", "Two");
        var store = new Store<>(list);
        
        assertEquals("[One, Two]", store.value().toString());
        
        store
        .change(
            apppend.applyTo("Three"),
            apppend.applyTo("Four"),
            apppend.applyTo("Five"),
            apppend.applyTo("Six")
        );
        assertEquals("[One, Two, Three, Four, Five, Six]", store.value().toString());
    }
    
}
