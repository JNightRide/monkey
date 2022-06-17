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

import com.jme3.export.Savable;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Interfaz encargado de implementar la lista de metodo de todo los posibles datos
 * que posee un objeto {@code JmeArray}.
 * <p>
 * Es llamdo cuando se hace un <code>forEach</code> de un objeto JmeArray o bien
 * cuando el metodo <pre><code>JmeArrat.iterator();</code></pre> es llamado.</p>
 *
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 * 
 * @param <E> tipo de datoss.
 */
public interface JmeIterator<E extends Object> extends Iterator<E> {
    
    // Objeto
    @Override
    public E next() throws JmeException;
    
    // Savables.
    public <T extends Savable> T nextSavable() throws JmeException;
    
    // Mapas y listas.
    public Map<String, Object> nextMap() throws JmeException;
    public List<Object> nextList() throws JmeException;
    
    // Primitivos.
    public int nextInt() throws JmeException;    
    public long nextLong() throws JmeException;    
    public Number nextNumber() throws JmeException;    
    public short nextShort() throws JmeException;    
    public byte nextByte() throws JmeException;    
    public char nextChar() throws JmeException;    
    public float nextFloat() throws JmeException;    
    public double nextDouble() throws JmeException;    
    public String nextString() throws JmeException;    
    public boolean nextBoolean() throws JmeException;    
    public <T extends Enum<T>> T nextEnum() throws JmeException;
    
    // Buffers
    public BigDecimal nextBigDecimal() throws JmeException;    
    public BigInteger nextBigInteger() throws JmeException;    
    public BitSet nextBitSet() throws JmeException;    
    public FloatBuffer nextFloatBuffer() throws JmeException;    
    public IntBuffer nextIntBuffer() throws JmeException;    
    public ByteBuffer nextByteBuffer() throws JmeException;    
    public ShortBuffer nextShortBuffer() throws JmeException;    
    public CharBuffer nextCharBuffer() throws JmeException;    
    public LongBuffer nextLongBuffer() throws JmeException;    
    public DoubleBuffer nextDoubleBuffer() throws JmeException;
    
    // Objeto
    public E opt();
    
    // Savables.
    public <T extends Savable> T optSavable();    
    public <T extends Savable> T optSavable(T defaultVal);
    
    // Mapas y listas.
    public Map<String, Object> optMap();    
    public Map<String, Object> optMap(Map<String, Object> defaultValue);
    
    public List<Object> optList();    
    public List<Object> optList(List<Object> defaultValue);
    
    
    // Primitivos
    public int optInt();    
    public int optInt(int defaultValue);
    
    public long optLong();    
    public long optLong(long defaultValue);
    
    public Number optNumber();    
    public Number optNumber(Number defaultValue);
    
    public short optShort();    
    public short optShort(short defaultValue);
    
    public byte optByte();    
    public byte optByte(byte defaultValue);
    
    public char optChar();    
    public char optChar(char defaultvalue);
    
    public float optFloat();    
    public float optFloat(float defaultValue);
    
    public double optDouble();    
    public double optDouble(double defaultvalue);
    
    public String optString();    
    public String optString(String defaultValue);
    
    public boolean optBoolean();    
    public boolean optBoolean(boolean defaultValue);
    
    public <T extends Enum<T>> T optEnum();    
    public <T extends Enum<T>> T optEnum(T defaultValue);
    
    // BigNumber
    public BigDecimal optBigDecimal();    
    public BigDecimal optBigDecimal(BigDecimal defaultValue);
    
    public BigInteger optBigInteger();    
    public BigInteger optBigInteger(BigInteger defaultValue);
    
    // BitSet
    public BitSet optBitSet();    
    public BitSet optBitSet(BitSet defaultValue);
    
    
    // Buffers.
    public FloatBuffer optFloatBuffer();    
    public FloatBuffer optFloatBuffer(FloatBuffer defaultValue);
    
    public IntBuffer optIntBuffer();    
    public IntBuffer optIntBuffer(IntBuffer defaultValue);
    
    public ByteBuffer optByteBuffer();    
    public ByteBuffer optByteBuffer(ByteBuffer defaultValue);
    
    public ShortBuffer optShortBuffer();    
    public ShortBuffer optShortBuffer(ShortBuffer defaultValue);
    
    public CharBuffer optCharBuffer();    
    public CharBuffer optCharBuffer(CharBuffer defaultValue);
    
    public LongBuffer optLongBuffer();    
    public LongBuffer optLongBuffer(LongBuffer defaultValue);
    
    public DoubleBuffer optDoubleBuffer();    
    public DoubleBuffer optDoubleBuffer(DoubleBuffer defaultValue);
    
    // Tipo del objeto
    public JmeType getType() throws JmeException;
}
