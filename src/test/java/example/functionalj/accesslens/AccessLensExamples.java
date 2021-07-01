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
package example.functionalj.accesslens;

import static example.functionalj.accesslens.User.theUser;
import static functionalj.lens.Access.$S;
import static functionalj.lens.Access.theString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import functionalj.lens.lenses.IntegerAccess;
import functionalj.lens.lenses.StringAccess;
import functionalj.list.FuncList;
import functionalj.types.Struct;


class AccessLensExamples {
    
    @Struct
    void UserSpec(String name) {}
    
    @Test
    void example01_Access() {
        var user1 = new User("John");
        
        StringAccess<User> userName = User::name;
        
        assertEquals("John", userName.apply(user1));
    }
    
    @Test
    void example02_Compose() {
        var user1 = new User("John");
        
        StringAccess<User>  userName       = User::name;
        IntegerAccess<User> userNameLength = userName.length();
        
        assertEquals("John", userName.apply(user1));
        assertEquals(4, (int)userNameLength.apply(user1));
    }
    
    @Test
    void example03_Compare() {
        var user1 = new User("John");
        
        StringAccess<User> userName = User::name;
        
        assertFalse(userName.length().thatGreaterThan(4).apply(user1));
        assertTrue (userName.length().thatGreaterThan(4).apply(new User("NawaMan")));
    }
    
    @Test
    void example04_Stream() {
        var users = FuncList.of(
                    new User("John"),
                    new User("NawaMan"),
                    new User("Jack")
                );
        
        StringAccess<User> userName = User::name;
        
        var usersWithLongName = users.filter(userName.length().thatGreaterThan(4));
        assertEquals("[User[name: NawaMan]]", usersWithLongName.toString());
    }
    
    @Test
    void example05_CommonAccess() {
        var names = FuncList.of("John", "David", "Adam", "Ben");
        
        var shortNames = names.filter(theString.length().thatLessThan(4));
        var longNames  = names.filter($S.length().thatGreaterThan(4));
        
        assertEquals("[Ben]",   shortNames.toString());
        assertEquals("[David]", longNames.toString());
    }
    
    @Test
    void example06_LensRead() {
        var userName = theUser.name;
        
        assertEquals("John", userName.apply(new User("John")));    // Read.
    }
    
    @Test
    void example07_LensChange() {
        var userName = theUser.name;
        
        var user1 = new User("John");
        var user2 = userName.apply(user1, "Jack");    // Write.
        assertEquals("User[name: John]", user1.toString());
        assertEquals("User[name: Jack]", user2.toString());
    }
    
}
