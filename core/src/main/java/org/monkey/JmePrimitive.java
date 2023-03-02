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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import java.util.BitSet;
import java.util.Objects;

/**
 * Un <code>JmePrimitive</code> se encarga de envolver un dato primitivo, para
 * poder exportarlo como un objeto <code>Savable</code>.
 * <p>
 * Se utiliza solamente en los objeto <code>JmeProperties</code> y <code>JmeArray</code>
 * para elvolver sus datos.
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
final class JmePrimitive implements Cloneable, Savable, JmeString {

    /** Tipo de dato que se este envolviendo. */
    private JmeType jmeType;
    
    /** Dato primitivo a envolver. */
    private Object object;
    
    /**
     * Constructor predeterminado.
     */
    public JmePrimitive() {
    }

    /**
     * Construye un {@code JmePrimitive} con un valor espefecificado
     * a envolver.
     * 
     * @param object 
     *          Valor primitivo a envolver.
     */
    public JmePrimitive(Object object) {
        this.jmeType = JmeType.jmeValueOf(object);
        this.object  = object;
    }
    
    /**
     * Siempre que clonamos un {@code JmePrimitive} se hara de una manera profunda,
     * para clonar su valor y tipo de dato.
     * 
     * @throws InternalError Si ocurre un error interno(JVM) al clonar
     *                          el objeto/clase.
     * @return Clon del objeto generado.
     */
    @Override
    public JmePrimitive clone() {
        try {
            JmePrimitive clon = (JmePrimitive)
                                super.clone();
            
            clon.jmeType = jmeType;
            clon.object  = clone0(object, jmeType);
            
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Metodo auxiliar que se encarga de clonar el valor de esta clase, siempre
     * y cuando soporte la clonacion, los de tipo primitivo son una excepcion a
     * esa regla, dado que ellos solo se asignan.
     * 
     * @param obj
     *          Valor a clonar.
     * @param type
     *          El tipo del objeto que se clonara.
     * @return Valor clonado.
     */
    private Object clone0(Object obj, JmeType type) {
        switch (type) {
            case BitSet: return (BitSet) 
                                ((BitSet) obj).clone();
            case DoubleBuffer: return JmeBufferUtils.clone((DoubleBuffer) obj);
            case FloatBuffer:  return JmeBufferUtils.clone((FloatBuffer) obj);
            case ShortBuffer:  return JmeBufferUtils.clone((ShortBuffer) obj);            
            case ByteBuffer:   return JmeBufferUtils.clone((ByteBuffer) obj);            
            case CharBuffer:   return JmeBufferUtils.clone((CharBuffer) obj);
            case LongBuffer:   return JmeBufferUtils.clone((LongBuffer) obj);
            case IntBuffer:    return JmeBufferUtils.clone((IntBuffer) obj);
            default:
                return obj;
        }
    }

    /**
     * Genera un text({@code String}) con el comportamiento de
     * la clase {@code JmePrimitive}.
     * @return Un string como valor.
     */
    @Override
    public String toString() {
        return '[' + "jmeType="  + jmeType 
                   + ", object=" + object + ']';
    }
    
    /**
     * Implementamos el metodo de la interfaz <code>JmeString.toJmeString</code>
     * para generar una salida personalizada del comportamiento de la clase.
     * @return Un string como valor.
     */
    @Override
    public String toJmeString() {
        return String.valueOf(object);
    }

    // Getters de la clase 'JmePrimitive' que devuelvel el tipo de dato que se
    // esta envolviendo, a si como el dato establecido.
    public JmeType getJmeType() { return jmeType; }
    public Object getValue()    { return object;  }

    @Override
    public void write(JmeExporter ex) throws IOException {
        final OutputCapsule out = ex.getCapsule(this);
        
        out.write(jmeType, "JmeType", null);
        
        switch(jmeType) {
            case Boolean:
                boolean bool = (boolean) object;
                out.write(bool, "boolVal", false);
                break;
            case Byte:
                byte bit = (byte) object;
                out.write(bit, "bitVal", (byte)0);
                break;
            case Character:
                char car = (char) object;
                out.write(String.valueOf(car), "charVal", null);
                break;
            case Double:
                double d = (double) object;
                out.write(d, "doubleVal", Double.NaN);
                break;
            case Enum:
                Enum en = (Enum) object;
                Class className = en.getDeclaringClass();
                
                out.write(className.getName(), "className", null);
                out.write(en, "enumVal", (Enum)null);
                break;
            case Float:
                float f = (float) object;
                out.write(f, "floatVal", Float.NaN);
                break;
            case Int:
                int in = (int) object;
                out.write(in, "intVal", 0);
                break;
            case Long:
                long l = (long) object;
                out.write(l, "longVal", 0L);
                break;
            case Short:
                short s = (short) object;
                out.write(s, "shortVal", (short) 0);
                break;
            case String:
                String stl = (String) object;
                out.write(stl, "stringVal", null);
                break;
            case BigDecimal:
                BigDecimal decimal = (BigDecimal) object;
                BigInteger toint   = decimal.unscaledValue();
                
                out.write(decimal.scale(), "scaledValue", 0);
                out.write(toint.toByteArray(), "bigDecimal", null);                
                break;
            case BigInteger:
                BigInteger integer = (BigInteger) object;
                out.write(integer.toByteArray(), "bigInteger", null);
                break;
            case BitSet:
                BitSet bitSet = (BitSet) object;
                out.write(bitSet, "bitSet", null);
                break;
            case ByteBuffer:
                ByteBuffer byteBuffer = (ByteBuffer) object;
                out.write(byteBuffer, "byteBuffer", null);
                break;
            case CharBuffer:
                CharBuffer charBuffer = (CharBuffer) object;
                final char[] chars 
                                = JmeBufferUtils.toCharArray(charBuffer);                
                out.write(new String(chars), "charBuffer", null);
                break;
            case DoubleBuffer:
                DoubleBuffer doubleBuffer = (DoubleBuffer) object;
                final double[] ds
                                = JmeBufferUtils.toDoubleArray(doubleBuffer);
                out.write(ds, "doubleBuffer", null);
                break;
            case FloatBuffer:
                FloatBuffer floatBuffer = (FloatBuffer) object;
                out.write(floatBuffer, "floatBuffer", null);
                break;
            case IntBuffer:
                IntBuffer intBuffer = (IntBuffer) object;
                out.write(intBuffer, "intBuffer", null);
                break;
            case LongBuffer:
                LongBuffer longBuffer = (LongBuffer) object;
                final long[] ls 
                              = JmeBufferUtils.toLongArray(longBuffer);
                out.write(ls, "longBuffer", null);
                break;
            case ShortBuffer:
                ShortBuffer shortBuffer = (ShortBuffer) object;
                out.write(shortBuffer, "shortBuffer", null);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        final InputCapsule in = im.getCapsule(this);
        
        jmeType = in.readEnum("JmeType", JmeType.class, null);
        
        switch(jmeType) {
            case Boolean:
                object = in.readBoolean("boolVal", false);
                break;
            case Byte:
                object = in.readByte("bitVal", (byte) 0);
                break;
            case Character:
                object = readCharacter(in);
                break;
            case Double:
                object = in.readDouble("doubleVal", Double.NaN);
                break;
            case Enum:
                object = readEnum(in);
                break;
            case Float:
                object = in.readFloat("floatVal", Float.NaN);
                break;
            case Int:
                object = in.readInt("intVal", 0);
                break;
            case Long:
                object = in.readLong("longVal", 0L);
                break;
            case Short:
                object = in.readShort("shortVal", (short) 0);
                break;
            case String:
                object = in.readString("stringVal", null);
                break;
            case BigDecimal:                
                object = readBigNumber(in, true);
                break;
            case BigInteger:
                object = readBigNumber(in, false);
                break;
            case BitSet:
                object = in.readBitSet("bitSet", null);
                break;
            case ByteBuffer:
                object = in.readByteBuffer("byteBuffer", null);
                break;
            case CharBuffer:
                object = readCharBuffer(in);
                break;
            case DoubleBuffer:
                object = JmeBufferUtils.createDoubleBuffer(
                                        in.readDoubleArray("doubleBuffer", null));
                break;
            case FloatBuffer:
                object = in.readFloatBuffer("floatBuffer", null);
                break;
            case IntBuffer:
                object = in.readIntBuffer("intBuffer", null);
                break;
            case LongBuffer:
                object = JmeBufferUtils.createLongBuffer(
                                        in.readLongArray("longBuffer", null));
                break;
            case ShortBuffer:
                object = in.readShortBuffer("shortBuffer", null);
                break;
            default:
                throw new AssertionError();
            }
    }
    
    /**
     * El metodo <code>readCharacter</code> tiene como funcionalidad, leer un
     * objeto de tipo {@code char}.
     * 
     * @param in
     *          Administrador de entradas jme.
     * @return un caracter({@code char}) como valor.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si el valor leido tiene mas de un caracter, se considera
     *                          un error interno, ya que estamos importando un {@code char}.
     */
    private char readCharacter(InputCapsule in) throws IOException {
        final String valueOf = in.readString("charVal", null);
        
        if (valueOf == null 
                || valueOf.isEmpty()) {
            return '\u0000';
        }
        
        if (valueOf.length() == 1) {
            return valueOf.charAt(0);
        }
        throw new InternalError("Character.");
    }
    
    /**
     * El metodo <code>readEnum</code> tiene como funcionalidad, leer un
     * objeto de tipo {@code enum}.
     * 
     * @param in
     *          Administrador de entradas jme.
     * @return Un valor enumerado {@link java.lang.Enum}.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si los valores solicitados para leer un {@code enum}
     *                          no son validos, se lanzara un error interno.
     */
    private Enum<?> readEnum(InputCapsule in) throws IOException {
        try {
            String className = in.readString("className", null);
            Class forName    = Class.forName(className);
            
            return in.readEnum("enumVal", forName, null);
        } catch (ClassNotFoundException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * El metodo <code>readCharBuffer</code> tiene como funcionalidad, leer un
     * objeto de tipo {@code CharBuffer}.
     * 
     * @param in
     *          Administrador de entradas jme.
     * @return Un Buffer({@link java.nio.CharBuffer}) como valor.
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     */
    private Buffer readCharBuffer(InputCapsule in) throws IOException {
        String valueOf = in.readString("charBuffer", null);
        
        if (valueOf == null)
            return null;
        
        char[] cars = valueOf.toCharArray();
        return JmeBufferUtils.createCharBuffer(cars);
    }
    
    /**
     * El metodo <code>readBigNumber</code> tiene como funcionalidad, leer un
     * numero entero o decimal de gran capacidad conocidos como: <pre><code>
     * BigDecimal -> decimales
     * BigInteger -> enteros</code></pre>
     * 
     * @param in
     *          Administrador de entradas jme.
     * @param isDecimal 
     *              {@code true} si se va a leer un <code>BigDecimal</code>, de lo
     *                  contrario {@code false} para un <code>BigInteger</code>.
     * @return Un numero gigante/grande como valor.
     * 
     * @throws IOException Si al leer los datos en la entrada, genera
     *                      algun erro o excepcion.
     * @throws InternalError Si los valores solicitados no son <code>BigDecimal</code> o 
     *                          <code>BigInteger</code>, se lanzara un error interno.
     */
    private Number readBigNumber(InputCapsule in, boolean isDecimal) throws IOException {
        if (isDecimal) {
            byte[] decimalBits = in.readByteArray("bigDecimal", null);
            int scaleBits      = in.readInt("scaledValue", 0);
            
            if (decimalBits == null)
                throw new InternalError("BigDecimal.");
            
            BigInteger bigInt = new BigInteger(decimalBits);
            return new BigDecimal(bigInt, scaleBits, MathContext.UNLIMITED);
        } else {
            byte[] intBits = in.readByteArray("bigInteger", null);
            
            if (intBits == null)
                throw new InternalError("BigInteger.");
            
            return new BigInteger(intBits);
        }
    }

    /**
     * Metodo encargado de generar un hash para la clase.
     * @return {@code hashCode} de la clase.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.jmeType);
        hash = 43 * hash + Objects.hashCode(this.object);
        return hash;
    }

    /**
     * Metodo encargado de comparar un objeto para luego determinar si son
     * iguales a nivel de contenido o asi mismo.
     * 
     * @param obj
     *          Un objeto para comprobar.
     * @return {@code true} si son iguales, de lo contrario
     *          devolveria {@code false} como valor.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JmePrimitive other = (JmePrimitive) obj;
        if (this.jmeType != other.jmeType) {
            return false;
        }
        return Objects.equals(this.object, other.object);
    }
}
