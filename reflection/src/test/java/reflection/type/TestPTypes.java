/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package reflection.type;

import net.daporkchop.lib.reflection.type.PTypes;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import static net.daporkchop.lib.reflection.type.PTypes.*;

/**
 * @author DaPorkchop_
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPTypes {
    //
    // test cases for isSubtype
    //

    @Test
    public void test_isSubtype_0000() {
        //List<String> <: List<? extends CharSequence>
        assert isSubtype(
                parameterized(List.class, null, wildcardExtends(CharSequence.class)),
                parameterized(List.class, null, String.class));
    }

    @Test
    public void test_isSubtype_0001() {
        //ArrayList<String> <: List<? extends CharSequence>
        assert isSubtype(
                parameterized(List.class, null, wildcardExtends(CharSequence.class)),
                parameterized(ArrayList.class, null, String.class));
    }

    @Test
    public void test_isSubtype_0002() {
        //Scratch<String, String>.T<? extends CharSequence> <: Scratch<String, ? extends CharSequence>.T<? extends CharSequence>
        assert isSubtype(
                parameterized(Scratch.T.class, parameterized(Scratch.class, null, String.class, wildcardExtends(String.class)), wildcardExtends(CharSequence.class)),
                parameterized(Scratch.T.class, parameterized(Scratch.class, null, String.class, String.class), wildcardExtends(CharSequence.class)));
    }

    @Test
    public void test_isSubtype_0003() {
        //Scratch<String, ? extends CharSequence>.T<? extends CharSequence> <: Scratch<String, String>.T<? extends CharSequence>
        assert !isSubtype(
                parameterized(Scratch.T.class, parameterized(Scratch.class, null, String.class, String.class), wildcardExtends(CharSequence.class)),
                parameterized(Scratch.T.class, parameterized(Scratch.class, null, String.class, wildcardExtends(String.class)), wildcardExtends(CharSequence.class)));
    }

    @Test
    public void test_isSubtype_0004() {
        //Scratch2<? extends CharSequence> <: Scratch<String, ? extends CharSequence>
        assert isSubtype(
                parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                parameterized(Scratch2.class, null, wildcardExtends(CharSequence.class)));
    }

    @Test
    public void test_isSubtype_0005() {
        //Scratch2<? extends CharSequence> <: Scratch<String, ? extends CharSequence>
        assert isSubtype(
                parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                parameterized(Scratch2.class, null, wildcardExtends(CharSequence.class)));
    }

    @Test
    public void test_isSubtype_0006() {
        //Scratch2<? extends CharSequence>.Test<List<? extends CharSequence>, List<? super CharSequence>, List<?>> <: Scratch<String, ? extends CharSequence>.Test<? extends List<? extends CharSequence>, ? extends List<? super CharSequence>, ? extends List<?>>
        assert isSubtype(
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        wildcardExtends(parameterized(List.class, null, wildcardExtends(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardSuper(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardUnbounded()))),
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        parameterized(List.class, null, wildcardExtends(CharSequence.class)),
                        parameterized(List.class, null, wildcardSuper(CharSequence.class)),
                        parameterized(List.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_isSubtype_0007() {
        //Scratch2<? extends CharSequence>.Test<List<? extends CharSequence>, List<Object>, List<?>> <: Scratch<String, ? extends CharSequence>.Test<? extends List<? extends CharSequence>, ? extends List<? super CharSequence>, ? extends List<?>>
        assert isSubtype(
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        wildcardExtends(parameterized(List.class, null, wildcardExtends(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardSuper(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardUnbounded()))),
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        parameterized(List.class, null, wildcardExtends(CharSequence.class)),
                        parameterized(List.class, null, Object.class),
                        parameterized(List.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_isSubtype_0008() {
        //Scratch2<? extends CharSequence>.Test<ArrayList<? extends CharSequence>, ArrayList<? super CharSequence>, ArrayList<?>> <: Scratch<String, ? extends CharSequence>.Test<? extends List<? extends CharSequence>, ? extends List<? super CharSequence>, ? extends List<?>>
        assert isSubtype(
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        wildcardExtends(parameterized(List.class, null, wildcardExtends(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardSuper(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardUnbounded()))),
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        parameterized(ArrayList.class, null, wildcardExtends(CharSequence.class)),
                        parameterized(ArrayList.class, null, wildcardSuper(CharSequence.class)),
                        parameterized(ArrayList.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_isSubtype_0009() {
        //Scratch2<? extends CharSequence>.Test<ArrayList<? extends CharSequence>, ArrayList<? super Object>, ArrayList<?>> <: Scratch<String, ? extends CharSequence>.Test<? extends List<? extends CharSequence>, ? extends List<? super CharSequence>, ? extends List<?>>
        assert isSubtype(
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        wildcardExtends(parameterized(List.class, null, wildcardExtends(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardSuper(CharSequence.class))),
                        wildcardExtends(parameterized(List.class, null, wildcardUnbounded()))),
                parameterized(Scratch.Test.class, parameterized(Scratch.class, null, String.class, wildcardExtends(CharSequence.class)),
                        parameterized(ArrayList.class, null, wildcardExtends(CharSequence.class)),
                        parameterized(ArrayList.class, null, wildcardSuper(Object.class)),
                        parameterized(ArrayList.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_isSubtype_0010() {
        //? extends CharSequence <: Object
        assert isSubtype(Object.class, wildcardExtends(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0011() {
        //? extends CharSequence <: CharSequence
        assert isSubtype(CharSequence.class, wildcardExtends(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0012() {
        //? extends CharSequence <: String
        assert !isSubtype(String.class, wildcardExtends(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0013() {
        //? extends Class<? extends String> <: Class<? extends CharSequence>
        assert isSubtype(parameterized(Class.class, null, wildcardExtends(CharSequence.class)), wildcardExtends(parameterized(Class.class, null, wildcardExtends(String.class))));
    }

    @Test
    public void test_isSubtype_0014() {
        //? extends Class<? extends String> <: Class<?>
        assert isSubtype(parameterized(Class.class, null, wildcardUnbounded()), wildcardExtends(parameterized(Class.class, null, wildcardExtends(String.class))));
    }

    @Test
    public void test_isSubtype_0015() {
        //? extends Class<? extends String> <: Class<? extends String>
        assert isSubtype(parameterized(Class.class, null, wildcardExtends(String.class)), wildcardExtends(parameterized(Class.class, null, wildcardExtends(String.class))));
    }

    @Test
    public void test_isSubtype_0016() {
        //? extends Class<String> <: Class<? extends String>
        assert isSubtype(parameterized(Class.class, null, wildcardExtends(String.class)), wildcardExtends(parameterized(Class.class, null, String.class)));
    }

    @Test
    public void test_isSubtype_0017() {
        //? extends Class<String> <: Class<? extends String>
        assert !isSubtype(parameterized(Class.class, null, String.class), wildcardExtends(parameterized(Class.class, null, wildcardExtends(String.class))));
    }

    @Test
    public void test_isSubtype_0018() {
        //? super CharSequence <: Object
        assert isSubtype(Object.class, wildcardSuper(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0019() {
        //? super CharSequence <: CharSequence
        assert !isSubtype(CharSequence.class, wildcardSuper(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0020() {
        //? super String <: CharSequence
        assert !isSubtype(CharSequence.class, wildcardSuper(String.class));
    }

    @Test
    public void test_isSubtype_0021() {
        /*
         * List<? super CharSequence> list = new ArrayList<>();
         * list.add(new Object()); //error
         */

        //Object <: ? super CharSequence
        assert !isSubtype(wildcardSuper(CharSequence.class), Object.class);
    }

    @Test
    public void test_isSubtype_0022() {
        /*
         * List<? super CharSequence> list = new ArrayList<>();
         * list.add((CharSequence) ""); //compiles
         */

        //CharSequence <: ? super CharSequence
        assert isSubtype(wildcardSuper(CharSequence.class), CharSequence.class);
    }

    @Test
    public void test_isSubtype_0023() {
        /*
         * List<? super CharSequence> list = new ArrayList<>();
         * list.add(""); //compiles
         */

        //String <: ? super CharSequence
        assert isSubtype(wildcardSuper(CharSequence.class), String.class);
    }

    @Test
    public void test_isSubtype_0024() {
        /*
         * List<? extends CharSequence> list = new ArrayList<>();
         * list.add(new Object()); //error
         */

        //Object <: ? extends CharSequence
        assert !isSubtype(wildcardExtends(CharSequence.class), Object.class);
    }

    @Test
    public void test_isSubtype_0025() {
        /*
         * List<? extends CharSequence> list = new ArrayList<>();
         * list.add((CharSequence) ""); //error
         */

        //CharSequence <: ? extends CharSequence
        assert !isSubtype(wildcardExtends(CharSequence.class), CharSequence.class);
    }

    @Test
    public void test_isSubtype_0026() {
        /*
         * List<? extends CharSequence> list = new ArrayList<>();
         * list.add(""); //error
         */

        //String <: ? extends CharSequence
        assert !isSubtype(wildcardExtends(CharSequence.class), String.class);
    }

    @Test
    public void test_isSubtype_0027() {
        /*
         * List<?> list = new ArrayList<>();
         * list.add(new Object()); //error
         */

        //String <: ?
        assert !isSubtype(wildcardUnbounded(), Object.class);
    }

    @Test
    public void test_isSubtype_0028() {
        /*
         * List<?> list = new ArrayList<>();
         * list.add(""); //error
         */

        //String <: ?
        assert !isSubtype(wildcardUnbounded(), String.class);
    }

    @Test
    public void test_isSubtype_0029() {
        /*
         * List<?> list = Arrays.asList("asdf", "jklö");
         * Object o = list.get(0); //compiles
         */

        //? <: Object
        assert isSubtype(Object.class, wildcardUnbounded());
    }

    @Test
    public void test_isSubtype_0030() {
        /*
         * List<?> list = Arrays.asList("asdf", "jklö");
         * CharSequence c = list.get(0); //error
         */

        //? <: CharSequence
        assert !isSubtype(CharSequence.class, wildcardUnbounded());
    }

    @Test
    public void test_isSubtype_0031() {
        /*
         * List<?> l0 = new ArrayList<>();
         * List<?> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? <: ?
        assert !isSubtype(wildcardUnbounded(), wildcardUnbounded());
    }

    @Test
    public void test_isSubtype_0032() {
        /*
         * List<? super CharSequence> l0 = new ArrayList<>();
         * List<? extends String> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //compiles
         */

        //? extends String <: ? super CharSequence
        assert isSubtype(wildcardSuper(CharSequence.class), wildcardExtends(String.class));
    }

    @Test
    public void test_isSubtype_0033() {
        /*
         * List<? super String> l0 = new ArrayList<>();
         * List<? extends CharSequence> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? extends CharSequence <: ? super String
        assert !isSubtype(wildcardSuper(String.class), wildcardExtends(CharSequence.class));
    }

    @Test
    public void test_isSubtype_0034() {
        /*
         * List<?> l0 = new ArrayList<>();
         * List<? extends String> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? extends String <: ?
        assert !isSubtype(wildcardUnbounded(), wildcardExtends(String.class));
    }

    @Test
    public void test_isSubtype_0035() {
        /*
         * List<? super CharSequence> l0 = new ArrayList<>();
         * List<?> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? <: ? super CharSequence
        assert !isSubtype(wildcardSuper(CharSequence.class), wildcardUnbounded());
    }

    @Test
    public void test_isSubtype_0036() {
        /*
         * List<? extends CharSequence> l0 = new ArrayList<>();
         * List<? super String> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? super String <: ? extends CharSequence
        assert !isSubtype(wildcardExtends(CharSequence.class), wildcardSuper(String.class));
    }

    @Test
    public void test_isSubtype_0037() {
        /*
         * List<? extends String> l0 = new ArrayList<>();
         * List<? super CharSequence> l1 = new ArrayList<>();
         * l0.add(l1.get(0)); //error
         */

        //? super CharSequence <: ? extends String
        assert !isSubtype(wildcardExtends(String.class), wildcardSuper(CharSequence.class));
    }

    //
    // test cases for containsTypeArgument
    //

    @Test
    public void test_containsTypeArgument_0000() {
        //CharSequence <= ? extends CharSequence
        assert containsTypeArgument(wildcardExtends(CharSequence.class), CharSequence.class);
    }

    @Test
    public void test_containsTypeArgument_0001() {
        //String <= ? extends CharSequence
        assert containsTypeArgument(wildcardExtends(CharSequence.class), String.class);
    }

    @Test
    public void test_containsTypeArgument_0002() {
        //Comparable <= ? extends CharSequence
        assert !containsTypeArgument(wildcardExtends(CharSequence.class), Comparable.class);
    }

    @Test
    public void test_containsTypeArgument_0003() {
        //Comparable<? extends CharSequence> <= ? extends CharSequence
        assert !containsTypeArgument(wildcardExtends(CharSequence.class), parameterized(Comparable.class, null, wildcardExtends(CharSequence.class)));
    }

    @Test
    public void test_containsTypeArgument_0004() {
        //Comparable<CharSequence> <= ? extends CharSequence
        assert !containsTypeArgument(wildcardExtends(CharSequence.class), parameterized(Comparable.class, null, CharSequence.class));
    }

    @Test
    public void test_containsTypeArgument_0005() {
        //Comparable<String> <= ? extends CharSequence
        assert !containsTypeArgument(wildcardExtends(CharSequence.class), parameterized(Comparable.class, null, String.class));
    }

    //
    // test cases for validate
    //

    @Test
    public void test_validate_0000() {
        //DependentGenerics<String, String, StringList>
        validate(parameterized(DependentGenerics.class, null, String.class, String.class, StringList.class));
    }

    @Test(expected = IllegalStateException.class)
    public void test_validate_0001() {
        //DependentGenerics<String, CharSequence, StringList>
        validate(parameterized(DependentGenerics.class, null, String.class, CharSequence.class, StringList.class));
    }

    @Test(expected = IllegalStateException.class)
    public void test_validate_0002() {
        //DependentGenerics<String, String, ArrayList<?>>
        validate(parameterized(DependentGenerics.class, null, String.class, String.class, parameterized(ArrayList.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_validate_0003() {
        //DependentGenerics<String, String, ArrayList<String>>
        validate(parameterized(DependentGenerics.class, null, String.class, String.class, parameterized(ArrayList.class, null, String.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void test_validate_0004() {
        //DependentGenerics<String, String, ArrayList<? extends String>>
        validate(parameterized(DependentGenerics.class, null, String.class, String.class, parameterized(ArrayList.class, null, wildcardExtends(String.class))));
    }

    //
    // test cases for resolver and resolve
    //

    @Test
    public void test_resolve_0000() {
        assert PTypes.equals(
                parameterized(AbstractList.class, null, String.class),
                resolve(resolver(StringList.class), ArrayList.class.getGenericSuperclass()));
    }

    @Test
    public void test_resolve_0001() {
        assert PTypes.equals(
                parameterized(Collection.class, null, String.class),
                resolve(resolver(StringList.class), List.class.getGenericInterfaces()[0]));
    }

    @Test
    public void test_resolve_0002() {
        assert PTypes.equals(
                parameterized(Collection.class, null, ArrayList.class.getTypeParameters()[0]),
                resolve(resolver(ArrayList.class), List.class.getGenericInterfaces()[0]));
    }

    @Test
    public void test_resolve_0003() {
        assert PTypes.equals(
                parameterized(Comparable.class, null, CharSequence[].class),
                resolve(resolver(Scratch3.Test3.Nested3.class), inheritedGenericSupertype(fillParametersWithTypeVariables(Scratch.Test.class), Comparable.class)));
    }

    @Test
    public void test_resolve_0004() {
        assert PTypes.equals(
                parameterized(List.class, null, wildcardUnbounded()),
                resolve(resolver(Scratch3.Test3.Nested3.class), inheritedGenericSupertype(fillParametersWithWildcards(Scratch.Test.class), List.class)));
    }

    //
    // test cases for inheritedGenericSupertype
    //

    @Test
    public void test_inheritedGenericSupertype_0000() {
        //String implements Comparable<String>
        assert PTypes.equals(
                parameterized(Comparable.class, null, String.class),
                inheritedGenericSupertype(String.class, Comparable.class));
    }

    @Test
    public void test_inheritedGenericSupertype_0001() {
        //StringList extends ArrayList<String> implements List<String> implements Collection<String> implements Iterable<String>
        assert PTypes.equals(
                parameterized(Iterable.class, null, String.class),
                inheritedGenericSupertype(StringList.class, Iterable.class));
    }

    @Test
    public void test_inheritedGenericSupertype_0002() {
        //ArrayList<? extends CharSequence> implements List<? extends CharSequence> implements Collection<? extends CharSequence>
        assert PTypes.equals(
                parameterized(Collection.class, null, wildcardExtends(CharSequence.class)),
                inheritedGenericSupertype(parameterized(ArrayList.class, null, wildcardExtends(CharSequence.class)), Collection.class));
    }

    @Test
    public void test_inheritedGenericSupertype_0003() {
        //ArrayList<? extends CharSequence> implements RandomAccess
        assert PTypes.equals(
                RandomAccess.class,
                inheritedGenericSupertype(parameterized(ArrayList.class, null, wildcardExtends(CharSequence.class)), RandomAccess.class));
    }
    
    //
    // test cases for canonicalize (and, by extension, equals/hashCode/toString)
    //

    private void test_canonicalize_pos(Type a, Type b) {
        if (a != null && b != null) {
            Type ca = canonicalize(a);
            Type cb = canonicalize(b);

            assert ca.equals(cb) : "canonicalize(a).equals(canonicalize(b)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
            assert cb.equals(ca) : "canonicalize(b).equals(canonicalize(a)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
            assert ca.hashCode() == cb.hashCode() : "canonicalize(a).hashCode() == canonicalize(b).hashCode(): " + PTypes.toString(a) + "   " + PTypes.toString(b);
            assert ca.getTypeName().equals(cb.getTypeName()) : "canonicalize(a).getTypeName().equals(canonicalize(b).getTypeName()): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        }

        assert PTypes.equals(a, b) : "equals(a, b): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        assert PTypes.equals(b, a) : "equals(b, a): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        assert PTypes.hashCode(a) == PTypes.hashCode(b) : "hashCode(a) == hashCode(b): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        assert PTypes.toString(a).equals(PTypes.toString(b)) : "toString(a).equals(toString(b)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
    }

    private void test_canonicalize_neg(Type a, Type b) {
        if (a != null && b != null) {
            Type ca = canonicalize(a);
            Type cb = canonicalize(b);

            assert !ca.equals(cb) : "canonicalize(a).equals(canonicalize(b)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
            assert !cb.equals(ca) : "canonicalize(b).equals(canonicalize(a)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
            assert !ca.getTypeName().equals(cb.getTypeName()) : "canonicalize(a).getTypeName().equals(canonicalize(b).getTypeName()): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        }

        assert !PTypes.equals(a, b) : "equals(a, b): " + PTypes.toString(a) + "   " + PTypes.toString(b);
        assert !PTypes.toString(a).equals(PTypes.toString(b)) : "toString(a).equals(toString(b)): " + PTypes.toString(a) + "   " + PTypes.toString(b);
    }

    @Test
    public void test_canonicalize_0000() { //nulls
        this.test_canonicalize_pos(null, null);
        this.test_canonicalize_neg(null, Object.class);
    }

    @Test
    public void test_canonicalize_0001() { //classes
        this.test_canonicalize_pos(Object.class, Object.class);
        this.test_canonicalize_neg(String.class, Object.class);
    }

    @Test
    public void test_canonicalize_0002() { //more complex cases
        this.test_canonicalize_pos(Scratch3.Test2.Nested2.class.getGenericSuperclass(), Scratch3.Test2.Nested2.class.getGenericSuperclass());
        this.test_canonicalize_pos(Scratch3.Test.Nested.class.getGenericSuperclass(), Scratch3.Test.Nested.class.getGenericSuperclass());
        this.test_canonicalize_neg(Scratch3.Test2.Nested2.class.getGenericSuperclass(), Scratch3.Test.Nested.class.getGenericSuperclass());
    }

    @Test
    public void test_canonicalize_0003() { //hand-crafted edge cases: array
        this.test_canonicalize_pos(int[].class, array(int.class));
        this.test_canonicalize_neg(int[].class, array(array(int.class)));
        this.test_canonicalize_neg(int[][].class, array(int.class));
    }

    @Test
    public void test_canonicalize_0004() { //hand-crafted edge cases: generic array
        this.test_canonicalize_neg(List[].class, array(parameterized(List.class, null, wildcardUnbounded())));
    }

    @Test
    public void test_canonicalize_0005() { //hand-crafted edge cases: wildcards with unusual upper bounds
        this.test_canonicalize_pos(wildcard(new Type[]{Object.class}, EMPTY_TYPE_ARRAY), wildcard(EMPTY_TYPE_ARRAY, EMPTY_TYPE_ARRAY));
        this.test_canonicalize_neg(wildcard(new Type[]{Object.class}, new Type[]{Object.class}), wildcard(EMPTY_TYPE_ARRAY, EMPTY_TYPE_ARRAY));
        this.test_canonicalize_pos(wildcard(new Type[]{Object.class}, new Type[]{Object.class}), wildcard(EMPTY_TYPE_ARRAY, new Type[]{Object.class}));
    }

    @Test
    public void test_canonicalize_0006() { //hand-crafted edge cases: parameterized types with redundant owner type
        this.test_canonicalize_pos(parameterized(StringList.class, TestPTypes.class), parameterized(StringList.class, null));
        this.test_canonicalize_pos(parameterized(StringList.class, TestPTypes.class), StringList.class);
        this.test_canonicalize_pos(
                parameterized(DependentGenerics.class, TestPTypes.class, String.class, String.class, StringList.class),
                parameterized(DependentGenerics.class, null, String.class, String.class, StringList.class));
        this.test_canonicalize_pos(
                parameterized(Scratch.T.class, parameterized(Scratch.class, TestPTypes.class, String.class, String.class), String.class),
                parameterized(Scratch.T.class, parameterized(Scratch.class, null, String.class, String.class), String.class));
    }

    //
    // some classes used by the test cases
    //

    private static class StringList extends ArrayList<String> {
    }

    private static class StringList2 extends StringList {
    }

    private static class DependentGenerics<FIRST, SECOND extends Comparable<FIRST> & Serializable, THIRD extends List<SECOND>> {
    }

    private static class Scratch<ASDF extends String, JKL> {
        class Abc {
        }

        class T<P> {
        }

        class Test<UPPER extends List<? extends CharSequence>, LOWER extends List<? super CharSequence>, ANY extends List<?>> extends ArrayList<ASDF> implements Comparable<JKL> {
            public List<ASDF>[][] asdfListArray;
            public List<UPPER>[][] upperListArray;
            public List<Object>[][] objectListArray;
            public List<? extends CharSequence>[][] wildcardListArray;
            public List<? extends LOWER>[][] wildcardLowerListArray;
            public List<? extends ASDF>[][] wildcardAsdfListArray;
            public Object[][] objectArray;

            @Override
            public int compareTo(JKL o) {
                throw new UnsupportedOperationException();
            }

            class Nested extends ArrayList<JKL> {
            }
        }
    }

    private static class Scratch2<JKL> extends Scratch<String, JKL> {
        class Test2 extends Test<List<String>, List<Object>, List<?>> {
            Test rawTest;
            Test<?, ?, ?> wildcardTest;

            class Nested2 extends Nested {
            }
        }
    }

    private static class Scratch3 extends Scratch2<CharSequence[]> {
        class Test3 extends Test2 {
            class Nested3 extends Nested2 {
            }
        }
    }
}
