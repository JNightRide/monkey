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

import com.jme3.util.clone.Cloner;
import com.jme3.util.clone.JmeCloneable;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.lang.reflect.Array;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.util.function.Consumer;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * <p>Un <code>JmeArray</code> es un objeto donde se pueden alojar datos
 * de tipo primitivo(int. short, byte, double, etc.), tambien soporta
 * objeto que implementes la interfaz {@code Savable}.</p>
 * 
 * <p>Se puede utilizar principalmente pata guardar los datos de nuestros juego 
 * creados von <code>jme</code> (<a href="https://jmonkeyengine.org/">jMonkeyEngine3</a>).
 * Solo necesita crear una instancia a esta clase.
 * 
 * <p><b>NOTA: este objeto actua como una lista o areglo de datos. Los objetos <pre>
 * <code>Object[] y Object[][]</code></pre> son convertido a un {@link JmeArray}.</b></p>
 * 
 * <p>Funcion similar que una lista({@code List<?,?>}).
 * 
 * @author wil
 * @version 1.5.0
 * 
 * @since 1.0.0
 */
public /*final*/ 
class JmeArray implements Cloneable, JmeCloneable, 
                            Savable, JmeIterable<Object> {

    /** Logger de la clase. */
    static final Logger LOG = Logger.getLogger(JmeArray.class.getName());
    
    /** Version que tiene la clase {@code JmeArray} actualemente. */
    public static final int SAVABLE_VERSION = 1;
    
    /** El arrayList donde se guardan las propiedades del {@code JmeArray}. */
    private ArrayList<Savable> myArrayList;

    /**
     * Construya un JmeArray vacío.
     */
    public JmeArray() {
        this.myArrayList = new ArrayList<>();
    }
    
    /**
     * Construya un JmeArray a partir de una colección.
     *
     * @param collection
     *            A Collection.
     */
    public JmeArray(Collection<?> collection) {
        if (collection == null) {
            this.myArrayList = new ArrayList<>();
        } else {
            this.myArrayList = new ArrayList<>(collection.size());
            this.addAll(collection, true);
        }
    }

    /**
     * Construya un JmeArray a partir de un Iterable. Esta es una copia superficial.
     *
     * @param iter
     *            Una colección iterable.
     */
    public JmeArray(Iterable<?> iter) {
        this();
        if (iter == null) {
            return;
        }
        this.addAll(iter, true);
    }

    /**
     * Construya un JmeArray a partir de otro JmeArray. Esta es una copia superficial.
     *
     * @param array
     *            un array.
     */
    public JmeArray(JmeArray array) {
        if (array == null) {
            this.myArrayList = new ArrayList<>();
        } else {
            // copia superficial directamente las listas de matrices internas como cualquier envoltura
            // ya debería haberse hecho en el JmeArray original
            this.myArrayList = new ArrayList<>(array.myArrayList);
        }
    }

    /**
     * Construya un JmeArray a partir de una matriz.
     *
     * @param array
     *            Array. Si el parámetro pasado es nulo, o no es una matriz,
     *            se lanzará una excepción.
     *
     * @throws JmeException
     *            Si no es una matriz o si un valor de matriz es un número no finito.
     * @throws NullPointerException
     *            Thrown si el parámetro de la matriz es nulo.
     */
    public JmeArray(Object array) throws JmeException {
        this();
        if (!array.getClass().isArray()) {
            throw new JmeException(
                    "JmeArray initial value should be a string or collection or array.");
        }
        this.addAll(array, true);
    }

    /**
     * Construya un JmeArray con la capacidad inicial especificada.
     *
     * @param initialCapacity
     *            La capacidad inicial del JmeArray.
     * @throws JmeException
     *             Si la capacidad inicial es negativa.
     */
    public JmeArray(int initialCapacity) throws JmeException {
    	if (initialCapacity < 0) {
            throw new JmeException(
                    "JmeArray initial capacity cannot be negative.");
    	}
    	this.myArrayList = new ArrayList<>(initialCapacity);
    }
    
    /**
     * Iterator del {@link JmeArray}.
     * 
     * <p>Esto se utiliza cuando se hace un <code>forEach</code>
     * del objeto o la lista</p>
     * 
     * @return Iterado de la lista.
     */
    @Override
    public JmeIterator<Object> iterator() {
        return new Itr<>(toList().iterator());
    }
    
    /**
     * Obtiene el valor del objeto asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor de objeto.
     * @throws JmeException
     *             Si no hay ningún valor para el índice.
     */
    public Object get(int index) throws JmeException {
        Object object = this.opt(index);
        if (object == null) {
            throw new JmeException("JmeArray[" + index + "] not found.");
        }
        return object;
    }
    
    /**
     * Obtiene el valor booleano asociado con un índice. Los valores de cadena "true"
     * y "false" se convierten en booleanos.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Valor verdadero o real.
     * @throws JmeException
     *             Si no hay valor para el índice o si el valor no es
     *              convertible a booleano.
     */
    public boolean getBoolean(int index) throws JmeException {
        Object object = this.get(index);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw wrongValueFormatException(index, "boolean", object, null);
    }
    
    /**
     * Obtiene el valor double asociado a un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *              a un número.
     */
    public double getDouble(int index) throws JmeException {
        final Object object = this.get(index);
        if(object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "double", object, e);
        }
    }
    
    /**
     * Obtiene el valor float asociado a un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Valor numerico.
     * @throws JmeException
     *              Si no se encuentra la clave o si el valor no se puede convertir
     *              a un número.
     */
    public float getFloat(int index) throws JmeException {
        final Object object = this.get(index);
        if(object instanceof Number) {
            return ((Number)object).floatValue();
        }
        try {
            return Float.parseFloat(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "float", object, e);
        }
    }
    
    /**
     * Obtenga el valor numérico asociado con una clave.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor numérico.
     * @throws JmeException
     *             if the key is not found or if the value is not a Number
     *             object and cannot be converted to a number.
     */
    public Number getNumber(int index) throws JmeException {
        Object object = this.get(index);
        try {
            if (object instanceof Number) {
                return (Number)object;
            }
            return JmeProperties.stringToNumber(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "number", object, e);
        }
    }
    
    /**
     * Obtenga el valor de enumeración asociado con un índice.
     * 
     * @param <E>
     *            Tipo enum.
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor de enumeración en la ubicación del índice
     * @throws JmeException
     *            si no se encuentra la clave o si el valor no se puede convertir
     *            a una enumeración.
     */
    public <E extends Enum<E>> E getEnum(int index) throws JmeException {
        E val = optEnum(index);
        if(val==null) {
            // JmeException realmente debería tomar un argumento arrojable.
            // Si lo hiciera, lo volvería a implementar con Enum.valueOf
            // método y coloque cualquier excepción lanzada en JmeException
            throw wrongValueFormatException(index, "enum of type "
                    + JmeProperties.quote("null"), opt(index), null);
        }
        return val;
    }
    
    /**
     * Obtenga el valor BigDecimal asociado con un índice. Si el valor es float
     * o double, el constructor {@link BigDecimal#BigDecimal(double)}
     * se utilizará. Consulte las notas sobre el constructor para problemas de conversión que
     * podría surgir.
     *
     * @param index
     *           El índice debe estar entre 0 y length() - 1.
     * @return El valore.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un BigDecimal.
     */
    public BigDecimal getBigDecimal (int index) throws JmeException {
        Object object = this.get(index);
        BigDecimal val = JmeProperties.objectToBigDecimal(object, null);
        if(val == null) {
            throw wrongValueFormatException(index, "BigDecimal", object, null);
        }
        return val;
    }
    
    /**
     * Obtenga el valor BigInteger asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valore.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un entero grande.
     */
    public BigInteger getBigInteger (int index) throws JmeException {
        Object object = this.get(index);
        BigInteger val = JmeProperties.objectToBigInteger(object, null);
        if(val == null) {
            throw wrongValueFormatException(index, "BigInteger", object, null);
        }
        return val;
    }
    
    /**
     * Obtiene el valor BitSet asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un BitSet.
     */
    public BitSet getBitSet(int index) throws JmeException {
        final Object object = this.get(index);
        if (object 
                instanceof BitSet) {
            return (BitSet) object;
        } else if (object 
                        instanceof ByteBuffer) {
            return BitSet.valueOf((ByteBuffer)
                                    object);
        } else if (object 
                        instanceof LongBuffer) {
            return BitSet.valueOf((LongBuffer)
                                    object);
        }
        
        throw wrongValueFormatException(index, "BitSet", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public FloatBuffer getFloatBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof FloatBuffer) {
            return (FloatBuffer) object;
        }        
        throw wrongValueFormatException(index, "FloatBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public IntBuffer getIntBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof IntBuffer) {
            return (IntBuffer) object;
        }        
        throw wrongValueFormatException(index, "IntBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public ByteBuffer getByteBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof ByteBuffer) {
            return (ByteBuffer) object;
        }        
        throw wrongValueFormatException(index, "ByteBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public ShortBuffer getShortBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof ShortBuffer) {
            return (ShortBuffer) object;
        }        
        throw wrongValueFormatException(index, "ShortBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public CharBuffer getCharBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof CharBuffer) {
            return (CharBuffer) object;
        }        
        throw wrongValueFormatException(index, "CharBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public LongBuffer getLongBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof LongBuffer) {
            return (LongBuffer) object;
        }        
        throw wrongValueFormatException(index, "LongBuffer", object, null);
    }
    
    /**
     * Obtiene el valor buffer asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un Buffer.
     */
    public DoubleBuffer getDoubleBuffer(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof DoubleBuffer) {
            return (DoubleBuffer) object;
        }        
        throw wrongValueFormatException(index, "DoubleBuffer", object, null);
    }
    
    /**
     * Obtiene el valor int asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no es un número.
     */
    public int getInt(int index) throws JmeException {
        final Object object = this.get(index);
        if(object instanceof Number) {
            return ((Number)object).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "int", object, e);
        }
    }
    
    /**
     * Obtenga el JmeArray asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor JmeArray.
     * @throws JmeException
     *             Si no hay ningún valor para el índice. o si el valor no es un
     *             JmeArray
     */
    public JmeArray getJmeArray(int index) throws JmeException {
        Object object = this.get(index);
        if (object instanceof JmeArray) {
            return (JmeArray) object;
        }
        throw wrongValueFormatException(index, "JmeArray", object, null);
    }
    
    /**
     * Obtenga las JmeProperties asociadas con un índice.
     *
     * @param index
     *            subindice.
     * @return Un valor de JmeProperties.
     * @throws JmeException
     *             Si no hay valor para el índice o si el valor no es un
     *             JmePropiedades
     */
    public JmeProperties getJmeProperties(int index) throws JmeException {
        Object object = this.get(index);
        if (object instanceof JmeProperties) {
            return (JmeProperties) object;
        }
        throw wrongValueFormatException(index, "JmeProperties", object, null);
    }
    
    /**
     * Obtiene el valor long asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor long.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un número.
     */
    public long getLong(int index) throws JmeException {
        final Object object = this.get(index);
        if(object instanceof Number) {
            return ((Number)object).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "long", object, e);
        }
    }
    
    /**
     * Obtiene el valor short asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor short.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un número.
     */
    public short getShort(int index) throws JmeException {
        final Object object = this.get(index);
        if(object instanceof Number) {
            return ((Number)object).shortValue();
        }
        try {
            return Short.parseShort(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "short", object, e);
        }
    }
    
    /**
     * Obtiene el valor byte asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor bytet.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un byte.
     */
    public byte getByte(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof Byte) {
            return (byte) object;
        } else if(object instanceof Number) {
            return ((Number)object).byteValue();
        }
        try {
            return Byte.parseByte(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(index, "byte", object, e);
        }
    }
    
    /**
     * Obtiene el valor char asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor bchar.
     * @throws JmeException
     *             Si no se encuentra la clave o si el valor no se puede convertir
     *             a un char.
     */
    public char getChar(int index) throws JmeException {
        final Object object = this.get(index);
        if (object instanceof Character) {
            return (char) object;
        } else {
            if (object instanceof String) {
                if (!((String) object).isEmpty() 
                        && ((String) object).length() == 1) {
                    return ((String) object).charAt(0);
                }
            }
        }
        throw wrongValueFormatException(index, "char", object, null);
    }
    
    /**
     * Obtiene un string asociada con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor de cadena.
     * @throws JmeException
     *             Si no hay ningún valor de cadena para el índice.
     */
    public String getString(int index) throws JmeException {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw wrongValueFormatException(index, "string", object, null);
    }
    
    /**
     * Obtiene un Savable asociada con un índice.
     *
     * @param <E>
     *          Tipo savable
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor savable.
     * @throws JmeException
     *             Si no hay ningún valor savable para el índice.
     */
    public <E extends Savable> E getSavable(int index) throws JmeException {
        Object object = this.get(index);
        if (object instanceof Savable) {
            return (E) object;
        }
        throw wrongValueFormatException(index, "savable", object, null);
    }
    
    /**
     * Determine si el valor es <code>null</code>.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return true si el valor en el índice es <code>null</code>, o si no hay ningún valor o
     *          es un <code>JmeNull</code>.
     */
    public boolean isNull(int index) {
        final Object object = this.opt(index);
        if (object instanceof JmeNull) {
            return true;
        }
        return JmeNull.NULL.equals(object);
    }

    /**
     * Obtenga la cantidad de elementos en JmeArray, incluidos los nulos.
     *
     * @return La longitud (o tamaño).
     */
    public int length() {
        return this.myArrayList.size();
    }

    /**
     * Elimina todos los elementos de este JmeArray.
     * El JmeArray estará vacío después de que regrese esta llamada.
     */
    public void clear() {
        this.myArrayList.clear();
    }

    @Override
    public JmeArray jmeClone() {
        try {
            return (JmeArray) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {
        myArrayList = cloner.clone(myArrayList);        
        for (int index = 0;
                index < myArrayList.size(); index++) {
            final Object element = myArrayList.get(index);
            if (element == null)
                continue;
            
            Object result = cloner.clone(element);
            myArrayList.set(index, JmeProperties.wrap(result));
        }
    }
    
    /**
     * Clonara el objeto {@link JmeArray} junto con sus datos que se almacenan
     * en ela lista. Los datos que no soportan la clonacion simplemente se agregaran
     * al la lista clonada.
     * 
     * <p><b>
     *  ADVERTENCIA: Si tiene objeto que no son clonables ten cudidado al modificarlos ya
     * que afectara el objeto original, utilize este metodo con precaucion.</b></p>
     * 
     * @see Cloner#clone(java.lang.Object) 
     * @see JmePrimitive#clone() 
     * 
     * @return Clon del objeto {@link JmeArray}.
     * @throws NullPointerException
     *                  Si la clave es <code>null</code>.
     */
    @Override
    public JmeArray clone() {
        try {
            // Clonamos el objeto JmeArray
            JmeArray clon = (JmeArray)
                            super.clone();
            
            // Objeto clonaro.
            final Cloner cloner = new Cloner();
            
            // Ya que la clonacion solo afecta de manera superficial
            // los objeto, tambine clonaremos la coleccion junto con sus
            // datos que soportan la clonacion.
            clon.myArrayList = (ArrayList<Savable>) myArrayList.clone();
            
            for (int index = 0; 
                    index < clon.myArrayList.size(); index++) {
                final Object element = clon.myArrayList.get(index);                
                if (element == null)
                    continue;
                
                Object result;
                if (element instanceof JmePrimitive) {
                    result = ((JmePrimitive) element).clone();
                } else if (element instanceof Savable) {
                    if (element instanceof JmeCloneable ||
                        element instanceof Cloneable ||
                        cloner.isCloned(element)) {
                        
                        // ¡Clonamos el objeto!.
                        result = cloner.clone(element);
                    } else {
                        result = element;
                        log("The object[key={0}, object={1}] does not support cloning.", new Object[] {
                                                                                        JmeProperties.quote(String.valueOf(index)),
                                                                                        JmeProperties.quote(element.getClass().getName())});
                    }
                } else {
                    throw new UnsupportedOperationException("Object [" + element.getClass().getName() + "] not supported.");
                }
                
                clon.myArrayList.set(index, JmeProperties.wrap(result));
            }
            
            // Devolvemos el objeto clonado junto con
            // su propiedades.
            return clon;
        } catch (CloneNotSupportedException e) {            
            // Si da un erro o por alguna razon JmeArray
            // no soporto la clonacion.
            throw new InternalError(e);
        }
    }
    
    /**
     * Obtenga el valor del objeto opcional asociado con un índice.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1. De lo contrario, se devuelve nulo.
     * @return Un valor de objeto, o nulo si no hay ningún objeto en ese índice.
     */
    public Object opt(int index) {
        if ((index < 0 || index >= this.length())) {
            return null;
        }
        final Object object = this.myArrayList.get(index);
        if (object instanceof JmePrimitive) {
            return ((JmePrimitive) object).getValue();
        } else {
            if (object instanceof JmeNull) {
                // Si el objeto o valor es una instancia de la interfaz
                // "JmeNull", se asume que es un valor nulo "NULL".
                return null;
            }
        }
        return object;
    }
    
    /**
     * Obtenga el valor booleano opcional asociado con un índice. devuelve falso
     * si no hay ningún valor en ese índice, o si el valor no es booleano.TRUE
     * o la cadena "true".
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Valor verdadero.
     */
    public boolean optBoolean(int index) {
        return this.optBoolean(index, false);
    }

    /**
     * Obtenga el valor booleano opcional asociado con un índice. devuelve el
     * defaultValue si no hay ningún valor en ese índice o si no es un valor booleano
     * o la cadena "true" o "falso" (sin distinción entre mayúsculas y minúsculas).
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Un boolean predeterminadp.
     * @return Valor verdadero o real.
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        Object val = this.opt(index);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean){
            return ((boolean) val);
        }
        try {            
            return this.getBoolean(index);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga el valor double opcional asociado con un índice. se devuelve NaN
     * si no hay valor para el índice, o si el valor no es un número y
     * no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public double optDouble(int index) {
        return this.optDouble(index, Double.NaN);
    }

    /**
     * Obtenga el valor double opcional asociado con un índice. El valor predeterminado
     * se devuelve si no hay valor para el índice, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param index
     *            subindice.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public double optDouble(int index, double defaultValue) {
        final Number val = this.optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        final double doubleValue = val.doubleValue();
        // if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
        // return defaultValue;
        // }
        return doubleValue;
    }
    
    /**
     * Obtenga el valor float opcional asociado con un índice. se devuelve NaN
     * si no hay valor para el índice, o si el valor no es un número y
     * no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public float optFloat(int index) {
        return this.optFloat(index, Float.NaN);
    }

    /**
     * Obtenga el valor float opcional asociado con un índice. El valor predeterminado
     * se devuelve si no hay valor para el índice, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param index
     *            subindice
     * @param defaultValue
     *            The default value.
     * @return El valor.
     */
    public float optFloat(int index, float defaultValue) {
        final Number val = this.optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        final float floatValue = val.floatValue();
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return floatValue;
        // }
        return floatValue;
    }
    
    /**
     * Obtenga el valor int opcional asociado con un índice. Se devuelve cero si
     * no hay valor para el índice, o si el valor no es un número y
     * no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public int optInt(int index) {
        return this.optInt(index, 0);
    }

    /**
     * Obtenga el valor int opcional asociado con un índice. El valor predeterminado es
     * devuelto si no hay ningún valor para el índice, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public int optInt(int index, int defaultValue) {
        final Number val = this.optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
    
    /**
     * Obtenga el valor BitSet asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor BitSet en la ubicación del índice o nulo si no se encuentra
     */
    public BitSet optBitSet(int index) {
        return this.optBitSet(index, null);
    }
    
    /**
     * Obtener el valor BitSet asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor.
     */
    public BitSet optBitSet(int index, BitSet defaultValue) {
        try {
            final Object object = this.opt(index);
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            }            
            if (object instanceof BitSet) {
                return (BitSet) object;
            }

            // usaremos get ya que hace una
            // conversion.
            return this.getBitSet(index);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public FloatBuffer optFloatBuffer(int index) {
        return this.optFloatBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public FloatBuffer optFloatBuffer(int index, FloatBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof FloatBuffer) {
                return (FloatBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public IntBuffer optIntBuffer(int index) {
        return this.optIntBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public IntBuffer optIntBuffer(int index, IntBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof IntBuffer) {
                return (IntBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public ByteBuffer optByteBuffer(int index) {
        return this.optByteBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public ByteBuffer optByteBuffer(int index, ByteBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof ByteBuffer) {
                return (ByteBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public ShortBuffer optShortBuffer(int index) {
        return this.optShortBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public ShortBuffer optShortBuffer(int index, ShortBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof ShortBuffer) {
                return (ShortBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public CharBuffer optCharBuffer(int index) {
        return this.optCharBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public CharBuffer optCharBuffer(int index, CharBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof CharBuffer) {
                return (CharBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public LongBuffer optLongBuffer(int index) {
        return this.optLongBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public LongBuffer optLongBuffer(int index, LongBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof LongBuffer) {
                return (LongBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor Buffer en la ubicación del índice o nulo si no se encuentra
     */
    public DoubleBuffer optDoubleBuffer(int index) {
        return this.optDoubleBuffer(index, null);
    }
    
    /**
     * Obtener el valor buffer asociado con una clave.
     * 
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor buffer.
     */
    public DoubleBuffer optDoubleBuffer(int index, DoubleBuffer defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else {
            if (object instanceof DoubleBuffer) {
                return (DoubleBuffer) object;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor de enumeración asociado con una clave.
     * 
     * @param <E>
     *            Tipo enum.
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor de enumeración en la ubicación del índice o nulo si no se encuentra
     */
    public <E extends Enum<E>> E optEnum(int index) {
        return this.optEnum(index, null);
    }

    /**
     * Obtener el valor de enumeración asociado con una clave.
     * 
     * @param <E>
     *            Tipo enum.
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor de enumeración en la ubicación del índice o defaultValue si
     *          el valor no se encuentra o no se puede asignar a clazz
     */
    public <E extends Enum<E>> E optEnum(int index, E defaultValue) {
        final Object val = this.opt(index);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Enum) {
            // Lo acabamos de comprobar!
            @SuppressWarnings("unchecked")
            E myE = (E) val;
            return myE;
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor BigInteger opcional asociado con un índice.
     * Se devuelve <code>null</code> si no hay ningún valor para el índice, o si el
     * el valor no es un número y no se puede convertir en un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public BigInteger optBigInteger(int index) {
        return this.optBigInteger(index, null);
    }
    
    /**
     * Obtenga el valor BigInteger opcional asociado con un índice. El
     * se devuelve defaultValue si no hay ningún valor para el índice, o si el
     * el valor no es un número y no se puede convertir en un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado
     * @return El valor.
     */
    public BigInteger optBigInteger(int index, BigInteger defaultValue) {
        Object val = this.opt(index);
        return JmeProperties.objectToBigInteger(val, defaultValue);
    }
    
    /**
     * Obtenga el valor BigDecimal opcional asociado con un índice. 
     * Se devuelve <code>null</code> si no hay ningún valor para el índice, o si el
     * el valor no es un número y no se puede convertir en un número. si el valor
     * es flotante o double, el {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión.
     * para problemas que puedan surgir.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public BigDecimal optBigDecimal(int index) {
        return this.optBigDecimal(index, null);
    }

    /**
     * Obtenga el valor BigDecimal opcional asociado con un índice. 
     * Se devuelve defaultValue si no hay ningún valor para el índice, o si el
     * el valor no es un número y no se puede convertir en un número. si el valor
     * es flotante o double, el {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión.
     * para problemas que puedan surgir.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado
     * @return El valor.
     */
    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue) {
        Object val = this.opt(index);
        return JmeProperties.objectToBigDecimal(val, defaultValue);
    }
    
    /**
     * Obtenga el JmeArray opcional asociado con un índice.
     *
     * @param index
     *            subindice.
     * @return Un valor JmeArray, o el valor null si el índice no tiene valor, o si el
     *              el valor no es un JmeArray.
     */
    public JmeArray optJmeArray(int index) {
        return this.optJmeArray(index, null);
    }
    
    /**
     * Obtenga el JmeArray opcional asociado con un índice.
     *
     * @param index
     *            subindice.
     * @param defaultValue
     *              Valor predeterminado.
     * @return Un valor JmeArray, o el valor predeterminao si el índice no tiene valor, o si el
     *              el valor no es un JmeArray.
     */
    public JmeArray optJmeArray(int index, JmeArray defaultValue) {
        Object o = this.opt(index);
        return o instanceof JmeArray ? (JmeArray) o : defaultValue;
    }
    
    /**
     * Obtenga las JmeProperties opcionales asociadas con un índice. Se devuelve NULL
     * si la clave no se encuentra, o es nula si el índice no tiene valor, o si el valor
     * no es un JmeProperties.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor de JmeProperties.
     */
    public JmeProperties optJmeProperties(int index) {
        return this.optJmeProperties(index, null);
    }
    
    /**
     * Obtenga las JmeProperties opcionales asociadas con un índice. Se devuelve el valor
     * perdeterminado si la clave no se encuentra, o es nula si el índice no tiene valor, o si el valor
     * no es un JmeProperties.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *                  Valor predeterminado.
     * @return Un valor de JmeProperties.
     */
    public JmeProperties optJmeProperties(int index, JmeProperties defaultValue) {
        Object o = this.opt(index);
        return o instanceof JmeProperties ? (JmeProperties) o : defaultValue;
    }
    
    /**
     * Obtenga el valor long opcional asociado con un índice. Se devuelve cero si
     * no hay valor para el índice, o si el valor no es un número y
     * no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public long optLong(int index) {
        return this.optLong(index, 0L);
    }

    /**
     * Obtenga el valor long opcional asociado con un índice. El valor predeterminado es
     * devuelto si no hay ningún valor para el índice, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public long optLong(int index, long defaultValue) {
        final Number val = this.optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return val.longValue();
    }
    
    /**
     * Obtenga el valor short opcional asociado con un índice. Se devuelve cero si
     * no hay valor para el índice, o si el valor no es un número y
     * no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public short optShort(int index) {
        return this.optShort(index,(short) 0);
    }

    /**
     * Obtenga el valor short opcional asociado con un índice. El valor predeterminado es
     * devuelto si no hay ningún valor para el índice, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public short optShort(int index, short defaultValue) {
        final Number val = this.optNumber(index, null);
        if (val == null) {
            return defaultValue;
        }
        return val.shortValue();
    }
    
    /**
     * Obtenga el valor byte opcional asociado con un índice. Se devuelve cero bytes si
     * no hay valor para el índice, o si el valor no es un byte y
     * no se puede convertir a un byte.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public byte optByte(int index) {
        return this.optByte(index, (byte) 0);
    }

    /**
     * Obtenga el valor byte opcional asociado con un índice. El valor predeterminado es
     * devuelto si no hay ningún valor para el índice, o si el valor no es un
     * byte y no se puede convertir a un byte.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public byte optByte(int index, byte defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        }
        if (object instanceof Byte) {
            return (byte) object;
        }
        try {
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getByte(index);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga el valor char opcional asociado con un índice. Se devuelve un valor
     * vacio si no hay valor para el índice, o si el valor no es un char y
     * no se puede convertir a un char.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return El valor.
     */
    public char optChar(int index) {
        return this.optChar(index, '\u0000');
    }

    /**
     * Obtenga el valor char opcional asociado con un índice. El valor predeterminado es
     * devuelto si no hay ningún valor para el índice, o si el valor no es un
     * char y no se puede convertir a un char.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return El valor.
     */
    public char optChar(int index, char defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        }
        if (object instanceof Character) {
            return (char) object;
        }
        try {
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getChar(index);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un valor {@link Number} opcional asociado con una clave, o <code>null</code>
     * si no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número ({@link BigDecimal}). Este método
     * se usaría en los casos en que no se desee la coerción de tipo del valor numérico.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un objeto que es el valor.
     */
    public Number optNumber(int index) {
        return this.optNumber(index, null);
    }

    /**
     * Obtenga un valor {@link Number} opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número ({@link BigDecimal}). Este método
     * se usaría en los casos en que no se desee la coerción de tipo del valor numérico.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public Number optNumber(int index, Number defaultValue) {
        Object val = this.opt(index);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number){
            return (Number) val;
        }
        
        if (val instanceof String) {
            try {
                return JmeProperties.stringToNumber((String) val);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtenga el valor stirng opcional asociado con un índice. Devuelve un
     * cadena vacía si no hay ningún valor en ese índice. Si el valor no es un
     * string y no es nulo, entonces se convierte en una cadena.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor String.
     */
    public String optString(int index) {
        return this.optString(index, "");
    }

    /**
     * Obtenga la cadena opcional asociada con un índice. El valor predeterminado es
     * devuelto si no se encuentra la clave.
     *
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un valor String.
     */
    public String optString(int index, String defaultValue) {
        Object object = this.opt(index);
        return JmeNull.NULL.equals(object) 
                                ? defaultValue 
                                : String.valueOf(object);
    }
    
    /**
     * Obten el valor Savable opcional asociado con un índice.Devuelve <code>NULL</code>
     * si no hay ningún valor en ese índice. Si el valor no es un
     * Savable y no es nulo..
     *
     * @param <E>
     *          Tipo Savable.
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @return Un valor Savable.
     */
    public <E extends Savable> E optSavable(int index) {
        return this.optSavable(index, null);
    }
    
    /**
     * Obten un objeto Savable asociada con un índice.El valor predeterminado es
     * devuelto si no se encuentra la clave.
     *
     * @param <E>
     *          Tipo Savable.
     * @param index
     *            El índice debe estar entre 0 y length() - 1.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un valor Savable
     */
    public <E extends Savable> E optSavable(int index, E defaultValue) {
        final Object object = this.opt(index);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        }
        if (object instanceof Savable) {
            return (E) object;
        }
        return defaultValue;
    }
    
    /**
     * Agregue un valor booleano. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor booleano
     * @return this.
     */
    public JmeArray put(boolean value) {
        return this.put(value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Ponga un valor en JmeArray, donde el valor será un JmeArray que
     * se produce a partir de una Colección.
     *
     * @param value
     *            Un valor de colección.
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     */
    public JmeArray put(Collection<?> value) {
        return this.put(new JmeArray(value));
    }
    
    /**
     * Agregue un valor double. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor double.
     * @return this.
     * @throws JmeException
     *             Si el valor es un número no finito.
     */
    public JmeArray put(double value) throws JmeException {
        return this.put(Double.valueOf(value));
    }
    
    /**
     * Agregue un valor flotante. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor float.
     * @return this.
     * @throws JmeException
     *             Si el valor es un número no finito.
     */
    public JmeArray put(float value) throws JmeException {
        return this.put(Float.valueOf(value));
    }
    
    /**
     * Agregue un valor int. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor int.
     * @return this.
     */
    public JmeArray put(int value) {
        return this.put(Integer.valueOf(value));
    }
    
    /**
     * Agregue un valorshortt. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor short.
     * @return this.
     */
    public JmeArray put(short value) {
        return this.put(Short.valueOf(value));
    }
    
    /**
     * Agregue un valor byte. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor byte.
     * @return this.
     */
    public JmeArray put(byte value) {
        return this.put(Byte.valueOf(value));
    }
    
    /**
     * Agregue un valor char. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor char.
     * @return this.
     */
    public JmeArray put(char value) {
        return this.put(Character.valueOf(value));
    }
    
    /**
     * Agregue un valor clong. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor long-
     * @return this.
     */
    public JmeArray put(long value) {
        return this.put(Long.valueOf(value));
    }
    
    /**
     * Ponga un valor en el JmeArray, donde el valor será un JmeProperties que
     * se produce a partir de un Map.
     *
     * @param value
     *            Un valor Map.
     * @return this.
     * @throws JmeException
     *            Si un valor en el mapa es un número no finito.
     * @throws NullPointerException
     *            Si una clave en el mapa es <code>null</code>
     */
    public JmeArray put(Map<?, ?> value) {
        return this.put(new JmeProperties(value));
    }
    
    
    /**
     * Agregue un valor de objeto. Esto aumenta la longitud de la matriz en uno.
     *
     * @param value
     *            Un valor de objeto. El valor debe ser un boolean, double,
     *            Intger, JmeArray, JmeProperties, Long o String, o el
     *            Objeto JmeNullL.
     * @return this.
     * @throws JmeException
     *            If the value is non-finite number.
     */
    public JmeArray put(Object value) {
        if (value == null) 
            return null;
        
        if (value instanceof Savable) {
            this.myArrayList.add((Savable) value);
        } else {
            JmeProperties.testValidity(value);
            this.myArrayList.add(new JmePrimitive(value));
        }
        return this;
    }
    
    /**
     * Coloque o reemplace un valor booleano en JmeArray. Si el índice es mayor
     * que la longitud de JemArray, entonces se agregarán elementos nulos como
     * necesario para rellenarlo.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor boolean.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, boolean value) throws JmeException {
        return this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Put a value in the JSONArray, where the value will be a JSONArray which
     * is produced from a Collection.
     *
     * @param index
     *            The subscript.
     * @param value
     *            A Collection value.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo o si el valor no es finito.
     */
    public JmeArray put(int index, Collection<?> value) throws JmeException {
        return this.put(index, new JmeArray(value));
    }
    
    /**
     * Poner o reemplazar un valor doble. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor double.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo o si el valor no es finito.
     */
    public JmeArray put(int index, double value) throws JmeException {
        return this.put(index, Double.valueOf(value));
    }

    /**
     * Poner o reemplazar un valor flotante. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor float.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo o si el valor no es finito.
     */
    public JmeArray put(int index, float value) throws JmeException {
        return this.put(index, Float.valueOf(value));
    }
    
    /**
     * Poner o reemplazar un valor int. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor int.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, int value) throws JmeException {
        return this.put(index, Integer.valueOf(value));
    }
    
    /**
     * Poner o reemplazar un valor short. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor short.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, short value) throws JmeException {
        return this.put(index, Short.valueOf(value));
    }

    /**
     * Poner o reemplazar un valor byte. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valorbytet.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, byte value) throws JmeException {
        return this.put(index, Byte.valueOf(value));
    }
    
    /**
     * Poner o reemplazar un valor char. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor char.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, char value) throws JmeException {
        return this.put(index, Character.valueOf(value));
    }
    
    /**
     * Poner o reemplazar un valor long. Si el índice es mayor que la longitud de
     * el JmeArray, luego se agregarán elementos nulos según sea necesario para rellenarlo
     * fuera.
     *
     * @param index
     *            El subindice.
     * @param value
     *            Un valor long.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo.
     */
    public JmeArray put(int index, long value) throws JmeException {
        return this.put(index, Long.valueOf(value));
    }
    
    /**
     * Ponga un valor en JmeArray, donde el valor será un JmePropertiest que
     * se produce a partir de un Map.
     *
     * @param index
     *            El subindice.
     * @param value
     *            El valor del Map.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo o si el valor no es válido
     *             número.
     * @throws NullPointerException
     *             Si una clave en el mapa es <code>null</code>
     */
    public JmeArray put(int index, Map<?, ?> value) throws JmeException {
        this.put(index, new JmeProperties(value));
        return this;
    }
    
    /**
     * Coloque o reemplace un valor de objeto en JmeArray. Si el índice es mayor
     * que la longitud del JmeArray, entonces se agregarán elementos nulos como
     * necesario para rellenarlo.
     *
     * @param index
     *            El subindice.
     * @param value
     *            El valor a poner en la matriz. El valor debe ser un
     *            Boolean, Double, Integer, JmeArray, JmeProperties, Long o
     *            String, o el objeto JmeNull.
     * @return this.
     * @throws JmeException
     *             Si el índice es negativo o si el valor no es un numero
     *             valido.
     */
    public JmeArray put(int index, Object value) throws JmeException {
        if (index < 0) {
            throw new JmeException("JmeArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            Savable jmeValue;
            if (value instanceof Savable) {
                jmeValue = (Savable) value;
            } else {
                JmeProperties.testValidity(value);
                jmeValue = new JmePrimitive(value);
            }
            this.myArrayList.set(index, jmeValue);
            return this;
        }
        if (index == this.length()) {
            // agregar simplemente
            return this.put(value);
        }
        // si estamos insertando más allá de la longitud, queremos hacer crecer la matriz de una vez
        // en lugar de incrementalmente.
        this.myArrayList.ensureCapacity(index + 1);
        while (index != this.length()) {            
            // no necesitamos probar la validez de los objetos NULL
            this.myArrayList.add((Savable) JmeNull.NULL);
        }
        return this.put(value);
    }
    
    /**
     * Encargado de exportar el JmeArray a binarios. Se utiza el objeto
     * {@link com.jme3.export.JmeExporter} para hacer la codificacion.
     * 
     * @see JmeExporter#getCapsule(com.jme3.export.Savable)
     *          Encargado de la escritura binaria.
     * @param je
     *          Exportador o escritor binario <code>jme</code>
     * @throws IOException 
     *              Si da un error o excepcion durante el proceso
     *              de exportacion.
     */
    @Override
    public void write(JmeExporter je) throws IOException {
        OutputCapsule out = je.getCapsule(this);
        
        // Exportamos la coleccion o la lista del
        // JmeArray en binario.
        out.writeSavableArrayList(myArrayList, "JmeArray", null);
    }

    /**
     * Importara los datos del JmeArray, Se utilizara el obejto
     * {@link com.jme3.export.JmeImporter} para hacer la decodificacion.
     * 
     * @see JmeImporter#getCapsule(com.jme3.export.Savable) 
     *                  Lector de datos binarios.
     * @param ji
     *          Importado o lector binario <code>jme</code>
     * @throws IOException 
     *              Si da un error o excepcion durante el proceso
     *              de importacion.
     */
    @Override
    public void read(JmeImporter ji) throws IOException {
        InputCapsule in = ji.getCapsule(this);
        
        // Importamos los datos de ña ñosta o coleccion que
        // utiliza JmeArray como almacenamiento de datos.
        this.myArrayList = in.readSavableArrayList("JmeArray", myArrayList);
    }
    
    /**
     * Coloque los elementos de una colección en JmeArray.
     *
     * @param collection
     *            Una colección.
     * @return this. 
     */
    public JmeArray putAll(Collection<?> collection) {
        this.addAll(collection, false);
        return this;
    }
    
    /**
     * Coloque los elementos de Iterable en JmeArray.
     *
     * @param iter
     *            Un Iterable.
     * @return this. 
     */
    public JmeArray putAll(Iterable<?> iter) {
        this.addAll(iter, false);
        return this;
    }

    /**
     * Coloque los elementos de un JmeArray en el JmeArray.
     *
     * @param array
     *            Un JSONArray.
     * @return this. 
     */
    public JmeArray putAll(JmeArray array) {
        // copia directamente los elementos de la matriz de origen a esta
        // ya que todo el ajuste ya debería haberse hecho en la fuente.
        this.myArrayList.addAll(array.myArrayList);
        return this;
    }

    /**
     * Coloque los elementos de una matriz en JmeArray.
     *
     * @param array
     *            Array. Si el parámetro pasado es nulo, o no es una matriz o Iterable,
     *            e lanzará una excepción.
     * @return this. 
     *
     * @throws JmeException
     *            Si no es una matriz, JmeArray, Iterable o si un valor es un número no finito.
     * @throws NullPointerException
     *            Thrown si el parámetro de la matriz es nulo.
     */
    public JmeArray putAll(Object array) throws JmeException {
        this.addAll(array, false);
        return this;
    }
    
    /**
     * Retire un índice y cierre el orificio.
     *
     * @param index
     *            El índice del elemento que se va a eliminar.
     * @return El valor que se asoció con el índice, o nulo si
     *          no hay tenía valor.
     */
    public Object remove(int index) {
        return index >= 0 && index < this.length()
            ? this.myArrayList.remove(index)
            : null;
    }
    
    /**
     * Produce un JmeProperties combinando un JmeArray de nombres con los valores de
     * este JmeArray.
     *
     * @param names
     *            Un JmeArray que contiene una lista de cadenas de claves. Estos serán
     *              emparejado con los valores.
     * @return Un JmeProperties, o nulo si no hay nombres o si este JmeArray
     *          no tiene valores.
     * @throws JmeException
     *             Si alguno de los nombres es nulo.
     */
    public JmeProperties toJmeProperties(JmeArray names) throws JmeException {
        if (names == null || names.isEmpty() || this.isEmpty()) {
            return null;
        }
        JmeProperties jo = new JmeProperties(names.length());
        for (int i = 0; i < names.length(); i += 1) {
            Object object = this.opt(i);
            if (object == null) {
                object = JmeNull.NULL;
            }
            jo.put(names.getString(i), object);
        }
        return jo;
    }

    /**
     * Genera el codigo hahs de la clase u objeto {@link JmeArray}.
     * 
     * @return codigo hahs del objeto.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.myArrayList);
        return hash;
    }

    /**
     * Un objeto {@link JmeArray} es igual a sí mismo.
     *
     * @param obj
     *            Un objeto para probar la nulidad.
     * @return true si el parámetro del objeto es el objeto JmeArray o
     *         el mismo.
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
        final JmeArray other = (JmeArray) obj;
        return Objects.equals(this.myArrayList, other.myArrayList);
    }
    
    private static void log(String smg, Object[] args) {
        JmeArray.LOG.log(Level.WARNING, new String(new StringBuilder().append("JmeArray")
                                                                           .append('\n')
                                                                           .append('\t')
                                                                           .append(smg)), args);
    }

    /**
     * Haz un texto JSON de este JmeArray. Para compacidad, no innecesario
     * Se agregan espacios en blanco. Si no es posible producir sintácticamente
     * texto JSON correcto, luego se devolverá nulo en su lugar. Esto podría ocurrir si
     * la matriz contiene un número no válido.
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b></p>
     *
     * @return una representación imprimible, visualizable y transmisible del
     *          matriz.
     */
    @Override
    public String toString() {
        try {
            return this.toString(0);
        } catch (JmeException e) {
            return null;
        }
    }

    /**
     * Haga un texto JSON bastante impreso de este JmeArray.
     *
     * <p>Si <pre><code> indentFactor &#62; 0</code></pre> y el {@link JmeArray} solo tiene
     * un elemento, entonces la matriz se generará en una sola línea:
     * <pre><code> [1]</code></pre>
     * </p>
     * <p>Si una matriz tiene 2 o más elementos, se generará a través de
     * líneas múltiples: <pre><code>
     * [
     * 1,
     * "valor 2",
     * 3
     * ]
     * }</code></pre></p>
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b>
     * </p>
     * @param indentFactor
     *            El número de espacios para agregar a cada nivel de sangría.
     * @return una representación imprimible, visualizable y transmisible del
     *          objeto, comenzando con <code>[</code>&nbsp;<small>(Corchete
     *          izquierdo)</small> y terminando en <code>]</code>
     *          &nbsp;<small>(corchete derecho)</small>.
     * @throws JmeException si una función llamada falla
     */
    public String toString(int indentFactor) throws JmeException {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    /**
     * Escriba el contenido de JmeArray como texto JSON para un escritor. Para
     * compacidad, no se agregan espacios en blanco.
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b></p>
     * @param writer El objeto escritor
     * @return Lo escrito.
     * @throws JmeException si una función llamada falla
     */
    public Writer write(Writer writer) throws JmeException {
        return this.write(writer, 0, 0);
    }

    /**
     * Escriba el contenido de JmeArray como texto JSON para un escritor.
     *
     * <p>Si <pre><code> indentFactor &#62; 0</code></pre> y el {@link JmeArray} solo tiene
     * un elemento, entonces la matriz se generará en una sola línea:
     * <pre><code> [1]</code></pre>
     * </p>
     * <p>Si una matriz tiene 2 o más elementos, se generará a través de
     * líneas múltiples: <pre><code>
     * [
     * 1,
     * "valor 2",
     * 3
     * ]
     * </code></pre></p>
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b></p>
     *
     * @param writer
     *            Escribe el JSON serializado
     * @param indentFactor
     *            El número de espacios para agregar a cada nivel de sangría.
     * @param indent
     *            La sangría del nivel superior.
     * @return Lo escrito-
     * @throws JmeException si una función llamada falla o no puede escribir
     */
    @SuppressWarnings("resource")
    public Writer write(Writer writer, int indentFactor, int indent)
            throws JmeException {
        try {
            boolean needsComma = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                try {
                    JmeProperties.writeValue(writer, this.myArrayList.get(0),
                            indentFactor, indent);
                } catch (JmeException e) {
                    throw new JmeException("Unable to write JmeArray value at index: 0", e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    JmeProperties.indent(writer, newIndent);
                    try {
                        JmeProperties.writeValue(writer, this.myArrayList.get(i),
                                indentFactor, newIndent);
                    } catch (JmeException e) {
                        throw new JmeException("Unable to write JmeArray value at index: " + i, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                JmeProperties.indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new JmeException(e);
        }
    }
    
    /**
     * Devuelve una java.util.List que contiene todos los elementos de esta matriz.
     * Si un elemento en la matriz es JmeArray o JmeProperties, también
     * ser convertido a una Lista y un Mapa respectivamente.
     *
     * <p>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     *</p>
     * @return una java.util.List que contiene los elementos de esta matriz
     */
    public List<Object> toList() {
        List<Object> results = new ArrayList<>(this.myArrayList.size());
        for (Object element : this.myArrayList) {
            if (element == null || JmeNull.NULL.equals(element) || element instanceof JmeNull) {
                results.add(null);
            } else if (element instanceof JmePrimitive) {
                results.add(((JmePrimitive) element).getValue());
            } else if (element instanceof JmeArray) {
                results.add(((JmeArray) element).toList());
            } else if (element instanceof JmeProperties) {
                results.add(((JmeProperties) element).toMap());
            } else {
                results.add(element);
            }
        }
        return results;
    }
    
    /**
     * Compruebe si JmeArray está vacío.
     *
     * @return true si JmeArray está vacío; de lo contrario false.
     */
    public boolean isEmpty() {
        return this.myArrayList.isEmpty();
    }
    
    /**
     * Agregue los elementos de una colección al JmeArray.
     *
     * @param collection
     *            Un Collection.
     * @param wrap
     *            {@code true} para llamar a {@link JmePropertiest#wrap(Object)} para cada elemento,
     *            {@code false} para agregar los elementos directamente
     *            
     */
    private void addAll(Collection<?> collection, boolean wrap) {
        this.myArrayList.ensureCapacity(this.myArrayList.size() + collection.size());
        if (wrap) {
            for (Object o: collection){
                this.put(JmeProperties.wrap(o));
            }
        } else {
            for (Object o: collection){
                this.put(o);
            }
        }
    }

    /**
     * Agregue los elementos de un Iterable al JmeArray.
     *
     * @param iter
     *            Un Iterable.
     * @param wrap
     *            {@code true} para llamar a {@link JmePropertiest#wrap(Object)} para cada elemento,
     *            {@code false} para agregar los elementos directamente
     */
    private void addAll(Iterable<?> iter, boolean wrap) {
        if (wrap) {
            for (Object o: iter){
                this.put(JmeProperties.wrap(o));
            }
        } else {
            for (Object o: iter){
                this.put(o);
            }
        }
    }
    
    /**
     * Agregue los elementos de una matriz a JmeArray.
     *
     * @param array
     *            Array. Si el parámetro pasado es nulo o no es una matriz,
     *            JmeArray, Collection o Iterable, una excepción será
     *            arrojado.
     * @param wrap
     *            {@code true} para llamar a {@link JmePropertiest#wrap(Object)} para cada elemento,
     *            {@code false} para agregar los elementos directamente
     *
     * @throws JmeException
     *            Si no es una matriz o si un valor de matriz es un número no finito.
     * @throws NullPointerException
     *            Thrown si el parámetro de la matriz es nulo.
     */
    private void addAll(Object array, boolean wrap) throws JmeException {
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            this.myArrayList.ensureCapacity(this.myArrayList.size() + length);
            if (wrap) {
                for (int i = 0; i < length; i += 1) {
                    this.put(JmeProperties.wrap(Array.get(array, i)));
                }
            } else {
                for (int i = 0; i < length; i += 1) {
                    this.put(Array.get(array, i));
                }
            }
        } else if (array instanceof JmeArray) {
            // usa la lista de matriz integrada `addAll` como todos los objetos
            // el ajuste debería haberse completado en el JmeArray
            // original
            this.myArrayList.addAll(((JmeArray)array).myArrayList);
        } else if (array instanceof Collection) {
            this.addAll((Collection<?>)array, wrap);
        } else if (array instanceof Iterable) {
            this.addAll((Iterable<?>)array, wrap);
        } else {
            throw new JmeException(
                    "JmeArray initial value should be a string or collection or array.");
        }
    }
    
    /**
     * Cree una nueva JmeException en un formato común para conversiones incorrectas.
     * @param valueType el tipo de valor al que se obliga
     * @param cause causa opcional de la falla de coerción
     * @return JmeException que se puede lanzar.
     */
    private static JmeException wrongValueFormatException(
            String valueType,
            Throwable cause) {
        return new JmeException(
                "JmeArray[Object] is not a " + valueType + "."
                , cause);
    }
    
    /**
     * Cree una nueva JmeException en un formato común para conversiones incorrectas.
     * @param idx indice del articulo
     * @param valueType el tipo de valor al que se obliga
     * @param cause causa opcional de la falla de coercion
     * @return JmeException que se puede tirar.
     */
    private static JmeException wrongValueFormatException(
            int idx,
            String valueType,
            Object value,
            Throwable cause) {
        if(value == null) {
            return new JmeException(
                    "JmeArray[" + idx + "] is not a " + valueType + " (null)."
                    , cause);
        }
        // no intente hacer cadenas de colecciones o tipos de objetos conocidos que podrian ser grandes.
        if(value instanceof Map || value instanceof Iterable || value instanceof JmeProperties) {
            return new JmeException(
                    "JmeArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + ")."
                    , cause);
        }
        return new JmeException(
                "JmeArray[" + idx + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ")."
                , cause);
    }
    
    /**
     * Clase <code>Itr</code> interna encargado de implementar la interfaz
     * <code>JmeIterator</code> para recorrer los articulos de una lista persoanlizada.
     * @param <E> Tipo de datos.
     */
    public static final class Itr<E extends Object> implements JmeIterator<E> {

        /** Objeto de iteracion {@link java.util.Iterator} interno. */
        private final Iterator<E> it;
        
        /** Objeto de la iteracion. */
        private E itrVal;

        /**
         * Genera un constructor <code>Itr</code> donde recorreremos los
         * compontes de un iterador.
         * 
         * @param it 
         *          Iterador de un {@link JmeArray}.
         */
        public Itr(Iterator<E> it) {
            this.it = it;
        }
        
        // Metodo de la clase JmeArrray#Iter<?> encargado de
        // iterrar los valor de la lista.
        @Override
        public E next() throws JmeException {
            final E object = this.opt();
            //if (object == null) {
            //    throw new JmeException("Null.");
            //}
            return object;
        }
        @Override
        public <T extends Savable> T nextSavable() throws JmeException {
            final E val = this.next();
            if (val instanceof Savable) {
                return (T) val;
            }
            throw wrongValueFormatException("savable", null);
        }
        @Override
        public Map<String, Object> nextMap() throws JmeException {
            final E val = this.next();
            if (val instanceof Map) {
                return (Map<String, Object>) val;
            }
            throw wrongValueFormatException("Map", null);
        }
        @Override
        public List<Object> nextList() throws JmeException {
            final E val = this.next();
            if (val instanceof List) {
                return (List<Object>) val;
            }
            throw wrongValueFormatException("List", null);
        }
        @Override
        public int nextInt() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            try {
                return Integer.parseInt(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("int", e);
            }
        }
        @Override
        public long nextLong() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return ((Number) val).longValue();
            }
            try {
                return Long.parseLong(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("long", e);
            }
        }
        @Override
        public Number nextNumber() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return (Number) val;
            }
            try {
                return JmeProperties.stringToNumber(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("number", e);
            }
        }
        @Override
        public short nextShort() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return ((Number) val).shortValue();
            }
            try {
                return Short.parseShort(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("short", e);
            }
        }
        @Override
        public byte nextByte() throws JmeException {
            final E val = this.next();
            if (val instanceof Byte) {
                return (byte) val;
            } else {
                if (val instanceof Number) {
                    return ((Number) val).byteValue();
                }
            }
            try {
                return Byte.parseByte(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("byte", e);
            }
        }
        @Override
        public char nextChar() throws JmeException {
            final E val = this.next();
            if (val instanceof Character) {
                return (char) val;
            } else {
                if (val instanceof String) {
                    if (!((String) val).isEmpty() &&
                            ((String) val).length() == 1) {
                        return ((String) val).charAt(0);
                    }
                }
            }
            throw wrongValueFormatException("char", null);
        }
        @Override
        public float nextFloat() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return ((Number) val).floatValue();
            }
            try {
                return Float.parseFloat(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("float", e);
            }
        }
        @Override
        public double nextDouble() throws JmeException {
            final E val = this.next();
            if (val instanceof Number) {
                return ((Number) val).doubleValue();
            }
            try {
                return Double.parseDouble(String.valueOf(val));
            } catch (NumberFormatException e) {
                throw wrongValueFormatException("double", e);
            }
        }
        @Override
        public String nextString() throws JmeException {
            final E val = this.next();
            if (val instanceof String) {
                return (String) val;
            }
            throw wrongValueFormatException("string", null);
        }
        @Override
        public boolean nextBoolean() throws JmeException {
            final E object = this.next();
            if (object.equals(Boolean.FALSE)
                    || (object instanceof String && ((String) object)
                            .equalsIgnoreCase("false"))) {
                return false;
            } else if (object.equals(Boolean.TRUE)
                    || (object instanceof String && ((String) object)
                            .equalsIgnoreCase("true"))) {
                return true;
            }
            throw wrongValueFormatException("boolean", null);
        }
        @Override
        public <T extends Enum<T>> T nextEnum() throws JmeException {
            final T val = this.optEnum();
            if (val == null) {
                throw wrongValueFormatException("enum", null);
            }
            return val;
        }
        @Override
        public BigDecimal nextBigDecimal() throws JmeException {
            final E val = this.next();
            BigDecimal ret = JmeProperties.objectToBigDecimal(val, null);
            if (ret != null) {
                return ret;
            }
            throw wrongValueFormatException("BigDecimal", null);
        }
        @Override
        public BigInteger nextBigInteger() throws JmeException {
            final E val = this.next();
            BigInteger ret = JmeProperties.objectToBigInteger(val, null);
            if (ret != null) {
                return ret;
            }
            throw wrongValueFormatException("BigDecimal", null);
        }
        @Override
        public BitSet nextBitSet() throws JmeException {
            final Object object = this.next();
            if (object 
                    instanceof BitSet) {
                return (BitSet) object;
            } else if (object 
                            instanceof ByteBuffer) {
                return BitSet.valueOf((ByteBuffer)
                                        object);
            } else if (object 
                            instanceof LongBuffer) {
                return BitSet.valueOf((LongBuffer)
                                        object);
            }

            throw wrongValueFormatException("BitSet", null);
        }
        @Override
        public FloatBuffer nextFloatBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof FloatBuffer) {
                return (FloatBuffer) object;
            }
            throw wrongValueFormatException("FloatBuffer", null);
        }
        @Override
        public IntBuffer nextIntBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof IntBuffer) {
                return (IntBuffer) object;
            }
            throw wrongValueFormatException("IntBuffer", null);
        }
        @Override
        public ByteBuffer nextByteBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof ByteBuffer) {
                return (ByteBuffer) object;
            }
            throw wrongValueFormatException("ByteBuffer", null);
        }
        @Override
        public ShortBuffer nextShortBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof ShortBuffer) {
                return (ShortBuffer) object;
            }
            throw wrongValueFormatException("ShortBuffer", null);
        }
        @Override
        public CharBuffer nextCharBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof CharBuffer) {
                return (CharBuffer) object;
            }
            throw wrongValueFormatException("CharBuffer", null);
        }
        @Override
        public LongBuffer nextLongBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof LongBuffer) {
                return (LongBuffer) object;
            }
            throw wrongValueFormatException("LongBuffer", null);
        }
        @Override
        public DoubleBuffer nextDoubleBuffer() throws JmeException {
            final Object object = this.next();
            if (object instanceof DoubleBuffer) {
                return (DoubleBuffer) object;
            }
            throw wrongValueFormatException("DoubleBuffer", null);
        }
        
        @Override
        public E opt() {
            if (this.it == null)
                this.itrVal = null;
            else
                this.itrVal = this.it.next();
            return this.itrVal;
        }
        @Override public <T extends Savable> T optSavable() { return this.optSavable(null); }
        @Override public <T extends Savable> T optSavable(T defaultVal) {
            final E val = this.opt();
            if (JmeNull.NULL.equals(val)) {
                return defaultVal;
            }
            return val instanceof Savable ? (T) val : defaultVal;
        }
        @Override public Map<String, Object> optMap() { return this.optMap(null); }
        @Override public Map<String, Object> optMap(Map<String, Object> defaultValue) {
            final E val = this.opt();
            return val instanceof Map ? (Map<String, Object>) val : defaultValue;
        }
        @Override public List<Object> optList() { return this.optList(null); }
        @Override public List<Object> optList(List<Object> defaultValue) {
            final E val = this.opt();
            return val instanceof List ? (List<Object>) val : defaultValue;
        }
        @Override public int optInt() { return this.optInt(0); }
        @Override public int optInt(int defaultValue) {
            final Number obj = this.optNumber(null);
            if (obj == null)
                return defaultValue;
            else
                return ((Number) obj).intValue();
        }
        @Override public long optLong() { return this.optLong(0L); }
        @Override public long optLong(long defaultValue) {
            final Number obj = this.optNumber(null);
            if (obj == null)
                return defaultValue;
            else
                return ((Number) obj).longValue();
        }
        @Override public Number optNumber() { return this.optNumber(null); }
        @Override public Number optNumber(Number defaultValue) {
            final E val = this.opt();
            if (JmeNull.NULL.equals(val)) {
                return defaultValue;
            }
            if (val instanceof Number) {
                return (Number) val;
            }
            try {
                return JmeProperties.stringToNumber((String) val);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        @Override public short optShort() { return this.optShort((short) 0); }
        @Override public short optShort(short defaultValue) {
            final Number val = this.optNumber(null);
            if (val == null)
                return defaultValue;
            else
                return val.shortValue();
        }
        @Override public byte optByte() { return this.optByte((byte) 0); }
        @Override public byte optByte(byte defaultValue) {
            final E val = this.opt();
            if (val instanceof Byte) {
                return (byte) val;
            } else {
                if (val instanceof Number) {
                    return ((Number) val).byteValue();
                }
            }
            try {
                return Byte.parseByte(String.valueOf(val));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        @Override public char optChar() { return this.optChar('\u0000'); }
        @Override public char optChar(char defaultvalue) {
            final E val = this.opt();
            if (val instanceof Character) {
                return (char) val;
            } else if (val instanceof String) {
                if (!((String) val).isEmpty()
                        && ((String) val).length() == 1) {
                    return ((String) val).charAt(0);
                }
            }
            return defaultvalue;
        }
        @Override public float optFloat() { return this.optFloat(Float.NaN); }
        @Override public float optFloat(float defaultValue) {
            final Number val = this.optNumber(null);
            if (val == null)
                return defaultValue;
            else
                return val.floatValue();
        }
        @Override public double optDouble() { return this.optDouble(Double.NaN); }
        @Override public double optDouble(double defaultvalue) {
            final Number val = this.optNumber(null);
            if (val == null)
                return defaultvalue;
            else
                return val.doubleValue();
        }
        @Override public String optString() { return this.optString(""); }
        @Override public String optString(String defaultValue) {
            final E val = this.opt();
            return val instanceof String ? (String) val : defaultValue;
        }
        @Override public boolean optBoolean() { return this.optBoolean(false); }
        @Override public boolean optBoolean(boolean defaultValue) {
            final E object = this.opt();
            if (JmeNull.NULL.equals(object))
                return defaultValue;
            
            if (object.equals(Boolean.FALSE)
                    || (object instanceof String && ((String) object)
                            .equalsIgnoreCase("false"))) {
                return false;
            } else if (object.equals(Boolean.TRUE)
                    || (object instanceof String && ((String) object)
                            .equalsIgnoreCase("true"))) {
                return true;
            }
            return defaultValue;
        }
        @Override public <T extends Enum<T>> T optEnum() { return this.optEnum(null); }
        @Override public <T extends Enum<T>> T optEnum(T defaultValue) {
            final E val = this.opt();
            if (JmeNull.NULL.equals(val)) {
                return defaultValue;
            }
            if (val instanceof Enum) {
                // Lo acabamos de comprobar!
                @SuppressWarnings("unchecked")
                T myT = (T) val;
                return myT;
            }
            return defaultValue;
        }
        @Override public BigDecimal optBigDecimal() { return this.optBigDecimal(null); }
        @Override public BigDecimal optBigDecimal(BigDecimal defaultValue) {
            final E val = this.opt();
            return JmeProperties.objectToBigDecimal(val, defaultValue);
        }
        @Override public BigInteger optBigInteger() { return this.optBigInteger(null); }
        @Override public BigInteger optBigInteger(BigInteger defaultValue) {
            final E val = this.opt();
            return JmeProperties.objectToBigInteger(val, defaultValue);
        }
        @Override public BitSet optBitSet() { return this.optBitSet(null); }
        @Override public BitSet optBitSet(BitSet defaultValue) { 
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object))
                return defaultValue;
            
            if (object 
                    instanceof BitSet) {
                return (BitSet) object;
            } else if (object 
                            instanceof ByteBuffer) {
                return BitSet.valueOf((ByteBuffer)
                                        object);
            } else if (object 
                            instanceof LongBuffer) {
                return BitSet.valueOf((LongBuffer)
                                        object);
            }
            return defaultValue;
        }        
        @Override public FloatBuffer optFloatBuffer() { return this.optFloatBuffer(null); }
        @Override public FloatBuffer optFloatBuffer(FloatBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof FloatBuffer) {
                    return (FloatBuffer) object;
                } else {
                    return defaultValue;
                }            
            }            
        }
        @Override public IntBuffer optIntBuffer() { return this.optIntBuffer(null); }
        @Override public IntBuffer optIntBuffer(IntBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof IntBuffer) {
                    return (IntBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }
        @Override public ByteBuffer optByteBuffer() { return this.optByteBuffer(null); }
        @Override public ByteBuffer optByteBuffer(ByteBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof ByteBuffer) {
                    return (ByteBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }
        @Override public ShortBuffer optShortBuffer() { return this.optShortBuffer(null); }
        @Override public ShortBuffer optShortBuffer(ShortBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof ShortBuffer) {
                    return (ShortBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }
        @Override public CharBuffer optCharBuffer() { return this.optCharBuffer(null); }
        @Override public CharBuffer optCharBuffer(CharBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof CharBuffer) {
                    return (CharBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }
        @Override public LongBuffer optLongBuffer() { return this.optLongBuffer(null); }
        @Override public LongBuffer optLongBuffer(LongBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof LongBuffer) {
                    return (LongBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }
        @Override public DoubleBuffer optDoubleBuffer() { return this.optDoubleBuffer(null); }
        @Override public DoubleBuffer optDoubleBuffer(DoubleBuffer defaultValue) {
            final Object object = this.opt();
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            } else {
                if (object instanceof DoubleBuffer) {
                    return (DoubleBuffer) object;
                } else {
                    return defaultValue;
                }            
            }   
        }        

        @Override
        public JmeType getType() throws JmeException {
            if (this.itrVal == null)
                throw new JmeException("Object is Null.");
            return JmeType.jmeValueOf(this.itrVal);
        }
        @Override
        public boolean hasNext() {
            if (this.it == null)
                return false;
            else
                return this.it.hasNext();
        }
        @Override
        public void remove() {
            if (this.it == null)
                return;
            this.it.remove();
        }
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            if (this.it == null)
                return;
            this.it.forEachRemaining(action);
        }
    }
}
