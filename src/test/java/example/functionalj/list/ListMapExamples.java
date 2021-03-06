// ============================================================================
// Copyright (c) 2017-2021 Nawapunth Manusitthipol (NawaMan - http://nawaman.net).
// ----------------------------------------------------------------------------
// MIT License
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
// ============================================================================
package example.functionalj.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Random;

import org.junit.jupiter.api.Test;

import functionalj.function.Func;
import functionalj.list.FuncList;
import functionalj.map.FuncMap;


class ListMapExamples {
    
    @Test
    void exampleListMap() {
        var list = FuncList.of("I", "Me", "Myself");
        var map  = FuncMap .of("One", 1.0, "PI", 3.14159, "E", 2.71828);
        assertEquals("[I, Me, Myself]",                  list.toString());
        assertEquals("{E:2.71828, One:1.0, PI:3.14159}", map.toString());
    }
    
    @Test
    void exampleReadOnly() {
        var list = FuncList.of("I", "Me", "Myself");
        var map  = FuncMap .of("One", 1.0, "PI", 3.14159, "E", 2.71828);
        assertEquals(3,         list.size());
        assertEquals(3,         map.size());
        assertEquals("Me",      list.get(1).toString());
        assertEquals("3.14159", map.get("PI").toString());
    }
    
    @Test
    void exampleUnsupportException() {
        var list = FuncList.of("I", "Me", "Myself");
        try {
            list.add("We");
            fail("Expect an error!");
        } catch (UnsupportedOperationException e) {
        }
        
        var map = FuncMap .of("One", 1.0, "PI", 3.14159, "E", 2.71828);
        try {
            map.put("Ten", 10.0);
            fail("Expect an error!");
        } catch (UnsupportedOperationException e) {
        }
    }
    
    @Test
    void exampleImmutableModification() {
        var list = FuncList.of("I", "Me", "Myself");
        var map  = FuncMap .of("One", 1.0, "PI", 3.14159, "E", 2.71828);
        
        var newList = list.append("First-Person");
        var newMap  = map .with("Ten", 10.0);
        
        assertEquals("[I, Me, Myself]",                            list.toString());
        assertEquals("{E:2.71828, One:1.0, PI:3.14159}",           map .toString());
        assertEquals("[I, Me, Myself, First-Person]",              newList.toString());
        assertEquals("{E:2.71828, One:1.0, PI:3.14159, Ten:10.0}", newMap .toString());
    }
    
    @Test
    void exampleFunctional() {
        var list = FuncList.of("I", "Me", "Myself");
        var map  = FuncMap .of("One", 1.0, "PI", 3.14159, "E", 2.71828);
        assertEquals("[1, 2, 6]",          list.map     (String::length).toString());
        assertEquals("{E:3, One:1, PI:3}", map .mapValue(Math::round)   .toString());
    }
    
    @Test
    void exampleImmutable() {
        var cats         = FuncList.of("Kitty", "Tigger", "Striped", "Oreo", "Simba", "Scar", "Felix", "Pete", "Schr??dinger's");
        var rand         = new Random();
        var deadNotAlive = Func.f((String s) -> rand.nextBoolean()).toPredicate();
        var deadCats     = cats.filter(deadNotAlive);
        assertNotEquals(deadCats, deadCats);
        
        var surelyDeadCats = deadCats.toImmutableList();
        assertEquals(surelyDeadCats, surelyDeadCats);
    }
    
}
