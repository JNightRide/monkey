/*
 * Copyright 2022 wil.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.monkey;

/**
 * Un <code>JmeType</code> se encarga de enumerar los diferentes tipos de
 * datos que soportan <code>JmeProperties</code> y <code>JmeArray</code>.
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public enum JmeType {
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Integer}
     */
    Int,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Float}
     */
    Float, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Boolean}
     */
    Boolean, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.String}
     */
    String,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Long}
     */
    Long,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Double}
     */
    Double, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Short}
     */
    Short, 
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Byte}
     */
    Byte,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Enum}
     */
    Enum,
    
    /**
     * Tipo de dato: 
     *          {@link java.lang.Character}
     */
    Character,
    
    /**
     * Tipo de dato: 
     *          {@link java.math.BigDecimal}
     */
    BigDecimal,
    
    /**
     * Tipo de dato: 
     *          {@link java.math.BigInteger
     */
    BigInteger,
    
    /**
     * Tipo de dato: 
     *          {@link java.util.BitSet}
     */
    BitSet,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.FloatBuffer}
     */
    FloatBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.IntBuffer}
     */
    IntBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.ByteBuffer}
     */
    ByteBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.ShortBuffer}
     */
    ShortBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.CharBuffer}
     */
    CharBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.LongBuffer}
     */
    LongBuffer,
    
    /**
     * Tipo de dato: 
     *          {@link java.nio.DoubleBuffer}
     */
    DoubleBuffer;
    
    /**
     * Evalua un objeto para determinar su tipo de dato.
     * @param o Objeto a evaluer.
     * @return Tipo de dato.
     * @throws JmeException Si el objeto que se evalua, no esta
     *                          soportado o es <code>NULL</code>
     */
    public static JmeType jmeValueOf(Object o) throws JmeException {
        if (o == null)
            throw new NullPointerException("Object is Null.");
            
        if (o instanceof java
                        .lang
                        .Character) {
            return Character;
        } else if (o instanceof java
                                .lang
                                .Integer) {
            return Int;
        } else if (o instanceof java
                                .lang
                                .Float) {
            return Float;
        } else if (o instanceof java
                                .lang
                                .Boolean) {
            return Boolean;
        } else if (o instanceof java
                                .lang
                                .String) {
            return String;
        } else if (o instanceof java
                                .lang
                                .Long) {
            return Long;
        } else if (o instanceof java
                                .lang
                                .Double) {
            return Double;
        } else if (o instanceof java
                                .lang
                                .Short) {
            return Short;
        } else if (o instanceof java
                                .lang
                                .Byte) {
            return Byte;
        } else if (o instanceof java
                                .lang
                                .Enum) {
            return Enum;
        } else if (o instanceof java
                                .math
                                .BigInteger) {
            return BigInteger;
        } else if (o instanceof java
                                .math
                                .BigDecimal) {
            return BigDecimal;
        } else if (o instanceof java
                                .util
                                .BitSet) {
            return BitSet;
        } else if (o instanceof java
                                .nio
                                .FloatBuffer) {
            return FloatBuffer;
        } else if (o instanceof java
                                .nio
                                .IntBuffer) {
            return IntBuffer;
        } else if (o instanceof java
                                .nio
                                .ByteBuffer) {
            return ByteBuffer;
        } else if (o instanceof java
                                .nio
                                .ShortBuffer) {
            return ShortBuffer;
        } else if (o instanceof java
                                .nio
                                .CharBuffer) {
            return CharBuffer;
        } else if (o instanceof java
                                .nio
                                .LongBuffer) {
            return LongBuffer;
        } else if (o instanceof java
                                .nio
                                .DoubleBuffer) {
            return DoubleBuffer;
        }
        throw new JmeException("Object " + o.getClass().getName() + " type not supported.");
    }
}
