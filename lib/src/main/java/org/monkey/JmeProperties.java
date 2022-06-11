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

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.lang.annotation.Annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Pattern;

/**
 * <p>Un <code>JmeProperties</code> es un objeto donde se pueden alojar datos
 * de tipo primitivo(int. short, byte, double, etc.), tambien soporta
 * objeto que implementes la interfaz {@code Savable}.</p>
 * 
 * <p>Se puede utilizar principalmente pata guardar los datos de nuestros juego 
 * creados von <code>jme</code> (<a href="https://jmonkeyengine.org/">jMonkeyEngine3</a>).
 * Solo necesita crear una instancia a esta clase.
 * 
 * <p>Funcion igual que un diccionario({@code Map<?,?>}).
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public class JmeProperties implements Cloneable, JmeCloneable, Savable {

    /** Logger de la clase. */
    static final Logger LOG = Logger.getLogger(JmeProperties.class.getName());

    /** Version que tiene la clase {@code JmeProperties} actualemente. */
    public static final int SAVABLE_VERSION = 1;
    
    /**
     * Expresión regular que coincide con números. Esto se utiliza principalmente para
     * garantizar las salida, que siempre estamos escribiendo un numero válido.
     */
    static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");
    
    /** El mapa donde se guardan las propiedades de JmeProperties. */
    private HashMap<String, Savable> map;
    
    /**
     * Construye un JmeProperties vacio.
     */
    public JmeProperties() {
        // HashMap se usa a propósito para garantizar que los elementos no estén ordenados por
        // la especificación.
        this.map = new HashMap<>();
    }
    
    /**
     * Construya un objeto JmeProperties a partir de un mapa.
     *
     * @param m
     *            Un objeto de mapa que se puede utilizar para inicializar el contenido del
     *              objeto de propiedades.
     * @throws JmeException
     *            Si un valor en el mapa es un número no finito.
     * @throws NullPointerException
     *            Si una clave en el mapa es <code>null</code>
     */
    public JmeProperties(Map<?, ?> m) {
        if (m == null) {
            this.map = new HashMap<>();
        } else {
            this.map = new HashMap<>(m.size());
        	for (final Entry<?, ?> e : m.entrySet()) {
        	    if(e.getKey() == null) {
        	        throw new NullPointerException("Null key.");
        	    }
                final Object value = e.getValue();
                if (value != null) {
                    this.map.put(String.valueOf(e.getKey()), wrap(value));
                }
            }
        }
    }
    
    /**
     * Construya un objeto JmePropertiest a partir de un objeto mediante captadores de beans. se refleja en
     * todos los métodos públicos del objeto. Para cada uno de los métodos sin
     * parámetros y un nombre que comience con <code>"get"</code> o
     * <code>"is"</code> seguido de una letra mayúscula, se invoca el método,
     * y una clave y el valor devuelto por el método getter se colocan en el
     * nuevo objeto de propiedades.
     * <p>
     * La clave se forma eliminando <code>"get"</code> o <code>"is"</code>
     * prefijo. Si el segundo carácter restante no está en mayúsculas, entonces el
     * el primer carácter se convierte a minúsculas.</p>
     * <p>
     * Los métodos que son <code>static</code>, devuelven <code>void</code>,
     * tienen parámetros, o son métodos "bridge", se ignoran.</p>
     * <p>
     * Por ejemplo, si un objeto tiene un método llamado <code>"getName"</code>, y
     * si el resultado de llamar a <code>object.getName()</code> es
     * <code>"Larry Fine"</code>, entonces el JmeProperties contendrá
     * <code>"name": "Larry Fine"</code>.</p>
     * <p>
     * La anotación {@link JmePropertyName} se puede usar en un bean getter para
     * invalidar el nombre de clave utilizado en JmeProperties. Por ejemplo, usando el objeto
     * arriba con el método <code>getName</code>, si lo anotamos con:
     * <pre>
     * &#64;JmePropertyName("Full Name")
     * public String getName() { return this.name; }
     * </pre>
     * El objeto de propiedades resultante contendría <code>"FullName": "Larry Fine"</code></p>
     * <p>
     * De manera similar, la anotación {@link JmePropertyName} se puede usar en
     * Métodos <code>get</code> y <code>is</code>. También podemos anular la clave
     * nombre utilizado en JmePropertiest como se ve a continuación aunque el campo normalmente
     * ser ignorado:
     * <pre>
     * &#64;JmePropertyName("Full Name")
     * public String fullName() { return this.name; }
     * </pre>
     * El objeto de propiedades resultante contendría <code>"FullName": "Larry Fine"</code></p>
     * <p>
     * La anotación {@link JmePropertyIgnore} se puede usar para forzar la propiedad del bean
     * para no ser serializado en el JmePropertyies. Si {@link JmePropertyIgnore} y
     * {@link JmePropertyName} están definidos en el mismo método, una comparación de profundidad es
     * realizado y se utiliza el más cercano a la clase concreta que se serializa.
     * Si ambas anotaciones están en el mismo nivel, {@link JmePropertyIgnore}
     * la anotación tiene prioridad y el campo no está serializado.
     * Por ejemplo, la siguiente declaración evitaría que <code>getName</code>
     * método de ser serializado:
     * <pre>
     * &#64;JmePropertyName("Full name")
     * &#64;JmePropertyIgnorar
     * public String getName() { retun this.name; }
     * </pre></p>
     *
     * @param bean
     *            Un objeto que tiene métodos getter que deben usarse para hacer
     *          un JmeProperties.
     */
    public JmeProperties(Object bean) {
        this();
        this.populateMap(bean);
    }

    private JmeProperties(Object bean, Set<Object> objectsRecord) {
        this();
        this.populateMap(bean, objectsRecord);
    }

    /**
     * Constructor para especificar una capacidad inicial del mapa interno. Útil para la biblioteca
     * llamadas internas donde sabemos, o al menos podemos adivinar mejor, qué tan grande es este JmeProperties
     * estarán.
     *
     * @param initialCapacity capacidad inicial del mapa interno.
     */
    public JmeProperties(int initialCapacity){
        this.map = new HashMap<>(initialCapacity);
    }
    
    /**
     * Obtiene el objeto de valor asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El objeto asociado con la clave.
     * @throws JmeException
     *             si no se encuentra la clave.
     */
    public Object get(String key) throws JmeException {
        if (key == null) {
            throw new JmeException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new JmeException("JmeProperties[" + quote(key) + "] not found.");
        }
        return object;
    }
    
    /**
     * Obtenga el valor de enum asociado con una clave.
     *
     * @param <E>
     *            Tipoe enumerado.
     * @param key
     *           Clave string.
     * @return El valor de enumeración asociado con la clave.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *             a una enumeración.
     */
    public <E extends Enum<E>> E getEnum(String key) throws JmeException {
        E val = optEnum(key);
        if(val==null) {
            // JmeException realmente debería tomar un argumento arrojable.
            // Si lo hiciera, lo volvería a implementar con Enum.valueOf
            // método y coloque cualquier excepción lanzada en JmeException
            throw wrongValueFormatException(key, "enum of type " + quote("null"), opt(key), null);
        }
        return val;
    }

    /**
     * Obtiene el valor boolean asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor verdadero o real.
     * @throws JmeException
     *             si el valor no es un booleano o la cadena "true" o
     *             "false".
     */
    public boolean getBoolean(String key) throws JmeException {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw wrongValueFormatException(key, "Boolean", object, null);
    }
    
    /**
     * Obtenga el valor BigInteger asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numérico.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no puede
     *             ser convertido a BigInteger.
     */
    public BigInteger getBigInteger(String key) throws JmeException {
        Object object = this.get(key);
        BigInteger ret = objectToBigInteger(object, null);
        if (ret != null) {
            return ret;
        }        
        throw wrongValueFormatException(key, "BigInteger", object, null);
    }
    
    /**
     * Obtenga el valor BigDecimal asociado con una clave. Si el valor es flotante o
     * double, el constructor {@link BigDecimal#BigDecimal(double)}
     * sera usado. Consulte las notas sobre el constructor para problemas de conversión que pueden
     * aumentar.
     *
     * @param key
     *            Clave string.
     * @return El valor numérico.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor
     *             no se puede convertir a BigDecimal.
     */
    public BigDecimal getBigDecimal(String key) throws JmeException {
        Object object = this.get(key);
        BigDecimal ret = objectToBigDecimal(object, null);
        if (ret != null) {
            return ret;
        }
        throw wrongValueFormatException(key, "BigDecimal", object, null);
    }
    
    /**
     * Obtenga el valor BitSet asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Un objeto como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no puede
     *             ser convertido a BitSet.
     */
    public BitSet getBitSet(String key) throws JmeException {
        final Object object = this.get(key);
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
        
        throw wrongValueFormatException(key, "BitSet", object, null);
    }
    
    /**
     * Obtiene un FloatBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public FloatBuffer getFloatBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof FloatBuffer) {
            return (FloatBuffer) object;
        }
        throw wrongValueFormatException(key, "FloatBuffer", object, null);
    }
    
    /**
     * Obtiene un IntBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public IntBuffer getIntBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof IntBuffer) {
            return (IntBuffer) object;
        }
        throw wrongValueFormatException(key, "IntBuffer", object, null);
    }
    
    /**
     * Obtiene un ByteBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public ByteBuffer getByteBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof ByteBuffer) {
            return (ByteBuffer) object;
        }
        throw wrongValueFormatException(key, "ByteBuffer", object, null);
    }
    
    /**
     * Obtiene un ShortBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public ShortBuffer getShortBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof ShortBuffer) {
            return (ShortBuffer) object;
        }
        throw wrongValueFormatException(key, "ShortBuffer", object, null);
    }
    
    /**
     * Obtiene un CharBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public CharBuffer getCharBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof CharBuffer) {
            return (CharBuffer) object;
        }
        throw wrongValueFormatException(key, "CharBuffer", object, null);
    }
    
    /**
     * Obtiene un LongBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public LongBuffer getLongBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof LongBuffer) {
            return (LongBuffer) object;
        }
        throw wrongValueFormatException(key, "LongBuffer", object, null);
    }
    
    /**
     * Obtiene un DoubleBuffer asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una buffer que es el valor.
     * @throws JmeException
     *             si no hay un valor buffer para la clave.
     */
    public DoubleBuffer getDoubleBuffer(String key) throws JmeException {
        final Object object = this.get(key);
        if (object instanceof DoubleBuffer) {
            return (DoubleBuffer) object;
        }
        throw wrongValueFormatException(key, "DoubleBuffer", object, null);
    }
    
    /**
     * Obtiene el valor double asociado a una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor numerico.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no es un Número
     *              objeto y no se puede convertir en un número.
     */
    public double getDouble(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "double", object, e);
        }
    }
    
    /**
     * Obtiene el valor float asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numerico.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no es un Número
     *             objeto y no se puede convertir en un número.
     */
    public float getFloat(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).floatValue();
        }
        try {
            return Float.parseFloat(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "float", object, e);
        }
    }
    
    /**
     * Obtenga el valor Numbers asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return El valor numerico.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no es un Número
     *             objeto y no se puede convertir en un número.
     */
    public Number getNumber(String key) throws JmeException {
        Object object = this.get(key);
        try {
            if (object instanceof Number) {
                return (Number)object;
            }
            return stringToNumber(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "number", object, e);
        }
    }
    
    /**
     * Obtiene el valor int asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Valor Integer.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *             a un número entero.
     */
    public int getInt(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "int", object, e);
        }
    }
    
    /**
     * Obtenga el valor de JmeArray asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Un JmeArray como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no es un JmeArray.
     */
    public JmeArray getJmeArray(String key) throws JmeException {
        Object object = this.get(key);
        if (object instanceof JmeArray) {
            return (JmeArray) object;
        }
        throw wrongValueFormatException(key, "JmeArray", object, null);
    }
    
    /**
     * Obtenga el valor de JmeProperties asociado con una clave.
     *
     * @param key
     *            Clave string.
     * @return Un JmeProperties como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no es un JmeProperties.
     */
    public JmeProperties getJmeProperties(String key) throws JmeException {
        Object object = this.get(key);
        if (object instanceof JmeProperties) {
            return (JmeProperties) object;
        }
        throw wrongValueFormatException(key, "JmeProperties", object, null);
    }
    
    /**
     * Obtiene el valor long asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un long como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un long.
     */
    public long getLong(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "long", object, e);
        }
    }
    
    /**
     * Obtenga una matriz de nombres de campo de un JmeProperties.
     *
     * @param jo
     *            Objeto de propiedades
     * @return Una matriz de nombres de campo, o nulo si no hay nombres.
     */
    public static String[] getNames(JmeProperties jo) {
        if (jo.isEmpty()) {
            return null;
        }
        return jo.keySet().toArray(new String[jo.length()]);
    }

    /**
     * Obtenga una matriz de nombres de campos públicos de un objeto.
     *
     * @param object
     *            objeto para leer
     * @return Una matriz de nombres de campo, o nulo si no hay nombres.
     */
    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }
    
    /**
     * Obtiene el valor short asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un short como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un short.
     */
    public short getShort(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Number) {
            return ((Number)object).shortValue();
        }
        try {
            return Short.parseShort(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "short", object, e);
        }
    }
    
    /**
     * Obtiene el valor byte asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un byte como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un byte.
     */
    public byte getByte(String key) throws JmeException {
        final Object object = this.get(key);
        if(object instanceof Byte) {
            return (byte) object;
        } else {
            if (object instanceof Number) {
                return ((Number) object).byteValue();
            }
        }
        try {
            return Byte.parseByte(String.valueOf(object));
        } catch (NumberFormatException e) {
            throw wrongValueFormatException(key, "byte", object, e);
        }
    }
    
    /**
     * Obtiene el valor char asociado con una clave.
     *
     * @param key
     *            Un string como clave.
     * @return Un char como valor.
     * @throws JmeException
     *             si no se encuentra la clave o si el valor no se puede convertir
     *              a un char.
     */
    public char getChar(String key) throws JmeException {
        final Object object = this.get(key);
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
        
        throw wrongValueFormatException(key, "char", object, null);
    }
    
    /**
     * Obtiene un String asociada con una clave.
     *
     * @param key
     *            Clave string.
     * @return Una cadena que es el valor.
     * @throws JmeException
     *             si no hay un valor de cadena para la clave.
     */
    public String getString(String key) throws JmeException {
        Object object = this.get(key);
        if (object 
                instanceof String) {
            return (String) object;
        } else {
            if (object instanceof CharBuffer) {
                final CharBuffer buffer 
                                    = (CharBuffer) object;
                return new String(JmeBufferUtils.toCharArray(buffer));
            }
        }
        throw wrongValueFormatException(key, "string", object, null);
    }
    
    /**
     * Obtiene un Savable asociada con una clave.
     *
     * @param <E>
     *          Tipo savable.
     * @param key
     *            Clave string.
     * @return Un objeto Savable como valor.
     * @throws JmeException
     *             si no hay un valor savable para la clave.
     */
    public <E extends Savable> E getSavable(String key) throws JmeException {
        Object object = this.get(key);
        if (object instanceof Savable) {
            return (E) object;
        }
        throw wrongValueFormatException(key, "savable", object, null);
    }
    
    /**
     * Determine si JmeProperties contiene una clave específica.
     *
     * @param key
     *            Una cadena clave.
     * @return true si la clave existe en JmeProperties.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    /**
     * Determine si el valor asociado con la clave es <code>null</code> o si no hay
     * valor.
     *
     * @param key
     *            Una cadena clave.
     * @return true si no hay ningún valor asociado con la clave o si el valor
     *          es el objeto JmeProperties.NULL o JmeNull.
     */
    public boolean isNull(String key) {
        final Object object = this.opt(key);
        if (object instanceof JmeNull) {
            return true;
        }        
        return JmeNull.NULL.equals(object);
    }

    @Override
    public JmeProperties jmeClone() {
        try {
            return (JmeProperties) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public void cloneFields(Cloner cloner, Object original) {
        map = cloner.clone(map);        
        for (final Entry<?,?> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException("Null key.");
            }
                
            final Object oldvalue = entry.getValue();
            if (oldvalue == null)
                continue;
            
            Object newvalue = cloner.clone(oldvalue);                
            map.put(String.valueOf(entry.getKey()), wrap(newvalue));
        }
    }
    
    /**
     * Clonara el objeto {@link JmeProperties} junto con sus datos que se almacenan
     * en el mapa. Los datos que no soportan la clonacion simplemente se agregaragn 
     * al diccionario clonado.
     * 
     * <p><b>
     *  ADVERTENCIA: Si tiene objeto no son clonables ten cudidado al modificarlos ya
     * que afectara el objeto original, utilize este metodo con precaucion.</b></p>
     * 
     * @see Cloner#clone(java.lang.Object) 
     * @see JmePrimitive#clone() 
     * 
     * @return Clon del objeto {@link JmeProperties}.
     * @throws NullPointerException
     *                  Si la clave es <code>null</code>.
     */
    @Override
    public JmeProperties clone() {
        try {
            // Clonamos el objeto JmeProperties
            JmeProperties clon = (JmeProperties) 
                                 super.clone();
            
            // Objeto clonaro.
            final Cloner cloner = new Cloner();
            
            // Ya que la clonacion solo afecta de manera superficial
            // los objeto, tambine clonaremos el Map junto con sus
            // datos que soportan la clonacion.
            clon.map = (HashMap<String, Savable>) map.clone();
                        
            for (final Entry<?, ?> entry : clon.map.entrySet()) {
                if (entry.getKey() == null) {
                    throw new NullPointerException("Null key.");
                }
                
                final Object oldvalue = entry.getValue();
                if (oldvalue == null)
                    continue;
                
                Object newvalue;
                if (oldvalue instanceof JmePrimitive) {
                    newvalue = ((JmePrimitive) oldvalue).clone();
                } else if (oldvalue instanceof Savable) {
                    if (oldvalue instanceof JmeCloneable ||
                        oldvalue instanceof Cloneable ||
                        cloner.isCloned(oldvalue)) {
                        
                        // ¡Clonamos el objeto!.
                        newvalue = cloner.clone(oldvalue);
                    } else {
                        newvalue = oldvalue;                        
                        log("The object[key={0}, object={1}] does not support cloning.", new Object[] {
                                                                                        quote(String.valueOf(entry.getKey())),
                                                                                        quote(oldvalue.getClass().getName())});
                    }
                } else {
                    throw new UnsupportedOperationException("Object [" + oldvalue.getClass().getName() + "] not supported.");
                }
                
                clon.map.put(String.valueOf(entry.getKey()), wrap(newvalue));
            }
            
            // Devolvemos el objeto clonado junto con
            // su propiedades.
            return clon;
        } catch (CloneNotSupportedException e) {
            // Si da un erro o por alguna razon JmeProperties
            // no soporto la clonacion.
            throw new InternalError(e);
        }
    }

    /**
     * Obtenga una enumeración de las claves del JmeProperties. La modificación de este conjunto de claves también
     * modificar el JmeProperties. Utilizar con precaución.
     *
     * @see Set#iterator()
     *
     * @return Un iterador de las claves.
     */
    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    /**
     * Obtenga una enumeración de las claves del JmeProperties. La modificación de este conjunto de claves también
     * modificar el JmeProperties. Utilizar con precaución.
     *
     * @see Map#keySet()
     *
     * @return Un juego de llaves.
     */
    public Set<String> keySet() {
        return this.map.keySet();
    }

    /**
     * Obtenga un conjunto de entradas de JmeProperties. Estos son valores brutos y es posible que no
     * coincide con lo que devuelven las funciones get* y opt* de JmeProperties. modificando
     * el EntrySet devuelto o los objetos Entry contenidos en él modificarán el
     * respaldando JmeProperties. Esto no devuelve un clon o una vista de solo lectura.
     *
     * Utilizar con precaución.
     *
     * @see Map#entrySet()
     *
     * @return Un conjunto de entrada
     */
    protected Set<Entry<String, Savable>> entrySet() {
        return this.map.entrySet();
    }

    /**
     * Obtenga el número de claves almacenadas en JmeProperties.
     *
     * @return El número de claves en el JmeProperties.
     */
    public int length() {
        return this.map.size();
    }

    /**
     * Elimina todos los elementos de este JmeProperties.
     * JmeProperties estará vacío después de que regrese esta llamada.
     */
    public void clear() {
        this.map.clear();
    }

    /**
     * Compruebe si JmeProperties está vacío.
     *
     * @return true si el JmeProperties está vacío, de lo contrario false.
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Producir un JmeArray que contenga los nombres de los elementos de este
     * JmeProperties.
     *
     * @return Un JmeArray que contiene las cadenas de clave, o nulo si JmeProperties
     *        esta vacio.
     */
    public JmeArray names() {
    	if(this.map.isEmpty()) {
    		return null;
    	}
        return new JmeArray(this.map.keySet());
    }
    
    /**
     * Produce una cadena a partir de un Número.
     *
     * @param number
     *            Un numero.
     * @return Uan cadena.
     * @throws JmeException
     *             Si n es un número no finito.
     */
    protected static String numberToString(Number number) throws JmeException {
        if (number == null) {
            throw new JmeException("Null pointer");
        }
        testValidity(number);

        // Elimina los ceros finales y el punto decimal, si es posible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Obtenga un valor opcional asociado con una clave.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor, o nulo si no hay valor.
     */
    public Object opt(String key) {
        if (key == null)
            return null;
        
        final Object object = this.map.get(key);
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
     * Obtenga el valor de enumeración asociado con una clave.
     *
     * @param <E>
     *            Tipo enum.
     * @param key
     *            Una cadena clave.
     * @return El valor enumerado asociado con la clave o nulo si no se encuentra
     */
    public <E extends Enum<E>> E optEnum(String key) {
        return this.optEnum(key, null);
    }
    
    /**
     * Obtenga el valor de enumeración asociado con una clave.
     *
     * @param <E>
     *            Tipo enum.
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            El valor predeterminado en caso de que no se encuentre el valor
     * @return El valor de enumeración asociado con la clave o defaultValue
     *          si el valor no se encuentra o no se puede asignar a <code>clazz</code>
     */
    public <E extends Enum<E>> E optEnum(String key, E defaultValue) {
        final Object val = this.opt(key);
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
     * Obtenga un booleano opcional asociado con una clave. Devuelve false si
     * no existe tal clave, o si el valor no es Boolean.TRUE o la cadena "true".
     *
     * @param key
     *            Una cadena clave.
     * @return Valo real o verdadero.
     */
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }
    
    /**
     * Obtenga un boolean opcional asociado con una clave. devuelve el
     * defaultValue si no existe tal clave, o si no es un boolean o la
     * Cadena "ture" o "falsoe" (no distingue entre mayúsculas y minúsculas).
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Valo real o verdadero.
     */
    public boolean optBoolean(String key, boolean defaultValue) {
        Object val = this.opt(key);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean){
            return ((boolean) val);
        }
        try {
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getBoolean(key);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un BigDecimal opcional asociado con una clave, o null si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número. si el valor
     * es flotante o doble, entonces {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión
     * de problemas que puedan surgir.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public BigDecimal optBigDecimal(String key) {
        return this.optBigDecimal(key, null);
    }
    
    /**
     * Obtenga un BigDecimal opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número. si el valor
     * es flotante o doble, entonces {@link BigDecimal#BigDecimal(double)}
     * Se utilizará el constructor. Ver notas sobre el constructor para la conversión
     * de problemas que puedan surgir.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminadoo.
     * @return Un objeto que es el valor.
     */
    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        return objectToBigDecimal(val, defaultValue);
    }
    
    /**
     * @param val 
     *          valor para convertir
     * @param defaultValue 
     *                  el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @return Conversión BigDecimal del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue) {
        return objectToBigDecimal(val, defaultValue, true);
    }
    
    /**
     * @param val valor para convertir
     * @param defaultValue el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @param exact Cuando <code>true</code>, los valores {@link Double} y {@link Float} se convertirán exactamente.
     *              Cuando <code>false</code>, se convertirán a valores {@link String} antes de convertirse a {@link BigDecimal}.
     * @return Conversión BigDecimal del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue, boolean exact) {
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigDecimal){
            return (BigDecimal) val;
        }
        if (val instanceof BigInteger){
            return new BigDecimal((BigInteger) val);
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            if (exact) {
                return new BigDecimal(((Number)val).doubleValue());
            }else {
                // usar el constructor de cadenas para mantener valores "agradables" para double y float,
                // el constructor double traducirá los douuble a valores "exactos" en lugar de los probables
                // representación prevista
                return new BigDecimal(val.toString());
            }
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        // no verifique si es una cadena en caso de subclases de números no verificadas
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un BigInteger opcional asociado con una clave, o null si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public BigInteger optBigInteger(String key) {
        return this.optBigInteger(key, null);
    }
    
    /**
     * Obtenga un BigInteger opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        return objectToBigInteger(val, defaultValue);
    }

    
    /**
     * @param val valor para convertir
     * @param defaultValue el valor predeterminado que se devuelve es que la conversión no funciona o es nula.
     * @return Conversión BigInteger del valor original, o el valor predeterminado si no se puede
     *          para convertir.
     */
    static BigInteger objectToBigInteger(Object val, BigInteger defaultValue) {
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigInteger){
            return (BigInteger) val;
        }
        if (val instanceof BigDecimal){
            return ((BigDecimal) val).toBigInteger();
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            return new BigDecimal(((Number) val).doubleValue()).toBigInteger();
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return BigInteger.valueOf(((Number) val).longValue());
        }
        // no verifique si es una cadena en caso de subclases de números no verificadas
        try {
            // las otras funciones opt manejan conversiones implícitas, es decir
            // jo.put("doble",1.1d);
            // jo.optInt("doble"); -- devolverá 1, no un error
            // esta conversión a BigDecimal y luego a BigInteger es para mantener
            // ese tipo de soporte de conversión que puede truncar el decimal.
            final String valStr = String.valueOf(val);
            if(isDecimalNotation(valStr)) {
                return new BigDecimal(valStr).toBigInteger();
            }
            return new BigInteger(valStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un doble opcional asociado con una clave, o NaN si no existe tal
     * clave o si su valor no es un número. Si el valor es una cadena, un intento
     * se hará para evaluarlo como un número.
     *
     * @param key
     *            Una cadena que es la clave..
     * @return Un objeto que es el valor.
     */
    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    /**
     * Obtenga un double opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si su valor no es un número. Si el valor es un
     * string, se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predetermiando.
     * @return Un objeto que es el valor.
     */
    public double optDouble(String key, double defaultValue) {
        Number val = this.optNumber(key);
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
     * Obtenga el valor float opcional asociado con una clave. Sse devuelve 
     * Nan si la clave no existe, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param key
     *           Una cande clave.
     * @return El valor.
     */
    public float optFloat(String key) {
        return this.optFloat(key, Float.NaN);
    }

    /**
     * Obtenga el valor float opcional asociado con una clave. El valor predeterminado
     * se devuelve si la clave no existe, o si el valor no es un
     * número y no se puede convertir a un número.
     *
     * @param key
     *            Una cande clave
     * @param defaultValue
     *            El valor predetreminado.
     * @return El valor.
     */
    public float optFloat(String key, float defaultValue) {
        Number val = this.optNumber(key);
        if (val == null) {
            return defaultValue;
        }
        final float floatValue = val.floatValue();
        // if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
        // return defaultValue;
        // }
        return floatValue;
    }
    
    /**
     * Obtenga un valor int opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena, un
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    /**
     * Obtenga un valor int opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public int optInt(String key, int defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }
        return val.intValue();
    }
    
    /**
     * Obtenga un JmeArray opcional asociado con una clave. Devuelve nulo si
     * no existe tal clave, o si su valor no es un JmeArray.
     *
     * @param key
     *            Clave string.
     * @return Un JmeArray que es el valor.
     */
    public JmeArray optJmeArray(String key) {
        return this.optJmeArray(key, null);
    }
    
    /**
     * Obtenga un JmeArray opcional asociado con una clave.Devuelve el valor 
     * predeterminado si no existe tal clave, o si su valor no es un JmeArray.
     *
     * @param key
     *            Clave string.
     * @param defaultValue
     *              Valor predeterminado.
     * @return Un JmeArray que es el valor.
     */
    public JmeArray optJmeArray(String key, JmeArray defaultValue) {
        Object o = this.opt(key);
        return o instanceof JmeArray ? (JmeArray) o : defaultValue;
    }
    
    /**
     * Obtenga un JmeProperties opcional asociado con una clave. Devuelve nulo si
     * no existe tal clave, o si su valor no es un JmeProperties.
     *
     * @param key
     *            Clave string.
     * @return Un JmeProperties que es el valor.
     */
    public JmeProperties optJmeProperties(String key) {
        return this.optJmeProperties(key, null);
    }
    
    /**
     * Obtenga un JmeProperties opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es JmeProperties.
     *
     * @param key
     *            Una cadena clave
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un JmeProperties que es el valor.
     */
    public JmeProperties optJmeProperties(String key, JmeProperties defaultValue) {
        Object object = this.opt(key);
        return object instanceof JmeProperties ? (JmeProperties) object : defaultValue;
    }
    
    /**
     * Obtenga un valor long opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    /**
     * Obtenga un valor long opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public long optLong(String key, long defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.longValue();
    }
    
    /**
     * Obtenga un valor short opcional asociado con una clave, o cero si no hay
     * tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public short optShort(String key) {
        return this.optShort(key, (short)0);
    }

    /**
     * Obtenga un valor short opcional asociado con una clave, o el valor predeterminado si existe
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public short optShort(String key, short defaultValue) {
        final Number val = this.optNumber(key, null);
        if (val == null) {
            return defaultValue;
        }

        return val.shortValue();
    }
    
    /**
     * Obtenga un valor byte opcional asociado con una clave, o cero bytes si no hay
     * tal clave o si el valor no es un byte o número. Si el valor es una cadena,
     * se intentará evaluarlo como un Byte.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public byte optByte(String key) {
        return this.optByte(key, (byte)0);
    }

    /**
     * Obtenga un valor byte opcional asociado con una clave, o el valor predeterminado si no hay
     * tal clave o si el valor no es un byte o número. Si el valor es una cadena,
     * se intentará evaluarlo como un Byte.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public byte optByte(String key, byte defaultValue) {
        final Object val = this.opt(key);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Byte) {
            return (byte) val;
        }
        try {
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getByte(key);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un valor char opcional asociado con una clave, o un valor nulo si no hay
     * tal clave o si el valor no es un char. Si el valor es una cadena,
     * se intentará evaluarlo como un char.
     *
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public char optChar(String key) {
        return this.optChar(key, '\u0000');
    }

    /**
     * Obtenga un valor char opcional asociado con una clave, o el valor predeterminado si no hay
     * tal clave o si el valor no es un char. Si el valor es una cadena,
     * se intentará evaluarlo como un char
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public char optChar(String key, char defaultValue) {
        final Object val = this.opt(key);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Character) {
            return (char) val;
        }
        try {
            // usaremos get de todos modos porque hace conversión de cadenas.
            return this.getChar(key);
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
     * @param key
     *            Una cadena clave.
     * @return Un objeto que es el valor.
     */
    public Number optNumber(String key) {
        return this.optNumber(key, null);
    }

    /**
     * Obtenga un valor {@link Number} opcional asociado con una clave, o el valor predeterminado si
     * no existe tal clave o si el valor no es un número. Si el valor es una cadena,
     * se intentará evaluarlo como un número. Este método
     * se usaría en los casos en que no se desee la coerción de tipo del valor numérico.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un objeto que es el valor.
     */
    public Number optNumber(String key, Number defaultValue) {
        Object val = this.opt(key);
        if (JmeNull.NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number){
            return (Number) val;
        }

        try {
            return stringToNumber(String.valueOf(val));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga una cadena opcional asociada con una clave. Devuelve una cadena vacía.
     * si no existe tal clave. Si el valor no es una cadena y no es nulo,
     * luego se convierte en una cadena.
     *
     * @param key
     *            Una cadena clave.
     * @return Una cadena que es el valor.
     */
    public String optString(String key) {
        return this.optString(key, "");
    }

    /**
     * Obtenga una cadena opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una cadena que es el valor.
     */
    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        } else if (object 
                    instanceof String) {
            return (String) object;
        } else  if (object 
                        instanceof CharBuffer) {
            final CharBuffer buffer 
                                = (CharBuffer) object;
            return String.valueOf(JmeBufferUtils.toCharArray(buffer));
        }
        
        return String.valueOf(object);
    }
    
    /**
     * Obtenga un BitSet opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link BitSet}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una BitSet que es el valor.
     */
    public BitSet optBitSet(String key) {
        return this.optBitSet(key, null);
    }
    
    /**
     * Obtenga un BitSet opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una BitSet que es el valor.
     */
    public BitSet optBitSet(String key, BitSet defaultValue) {
        try {
            final Object object = this.opt(key);
            if (JmeNull.NULL.equals(object)) {
                return defaultValue;
            }            
            if (object instanceof BitSet) {
                return (BitSet) object;
            }

            // usaremos get ya que hace una
            // conversion.
            return this.getBitSet(key);
        } catch (JmeException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtenga un FloatBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link FloatBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public FloatBuffer optFloatBuffer(String key) {
        return this.optFloatBuffer(key, null);
    }
    
    /**
     * Obtenga un FloatBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public FloatBuffer optFloatBuffer(String key, FloatBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un IntBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link IntBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public IntBuffer optIntBuffer(String key) {
        return this.optIntBuffer(key, null);
    }
    
    /**
     * Obtenga un IntBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public IntBuffer optIntBuffer(String key, IntBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un ByteBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link ByteBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public ByteBuffer optByteBuffer(String key) {
        return this.optByteBuffer(key, null);
    }
    
    /**
     * Obtenga un ByteBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public ByteBuffer optByteBuffer(String key, ByteBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un ShortBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link ShortBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public ShortBuffer optShortBuffer(String key) {
        return this.optShortBuffer(key, null);
    }
    
    /**
     * Obtenga un ShortBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public ShortBuffer optShortBuffer(String key, ShortBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un CharBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link CharBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public CharBuffer optCharBuffer(String key) {
        return this.optCharBuffer(key, null);
    }
    
    /**
     * Obtenga un CharBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public CharBuffer optCharBuffer(String key, CharBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un LongBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link LongBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public LongBuffer optLongBuffer(String key) {
        return this.optLongBuffer(key, null);
    }
    
    /**
     * Obtenga un LongBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public LongBuffer optLongBuffer(String key, LongBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga un DoubleBuffer opcional asociada con una clave. Devuelve <code>null</code>.
     * Si no existe tal clave. O si el valor no es un objeto {@link DoubleBuffer}.
     *
     * @param key
     *            Una cadena clave.
     * @return Una buffer que es el valor.
     */
    public DoubleBuffer optDoubleBuffer(String key) {
        return this.optDoubleBuffer(key, null);
    }
    
    /**
     * Obtenga unDoubletBuffer opcional asociada con una clave. Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Una buffer que es el valor.
     */
    public DoubleBuffer optDoubleBuffer(String key, DoubleBuffer defaultValue) {
        final Object object = this.opt(key);
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
     * Obtenga una savable opcional asociada con una clave.Devuelve el nulo <code>null</code>
     *  si no existe tal clave.
     * 
     * @param <E>
     *          Tipo savable.
     * @param key
     *            Una cadena clave.
     * @return Una cadena que es el valor.
     */
    public <E extends Savable> E optSavable(String key) {
        return this.optSavable(key, null);
    }
    
    /**
     * Obtenga una savable opcional asociada con una clave.Devuelve el valor predeterminado
     * si no existe tal clave.
     *
     * @param <E>
     *          Tipo savable
     * @param key
     *            Una cadena clave.
     * @param defaultValue
     *            Valor predeterminado.
     * @return Un savable que es el valor.
     */
    public <E extends Savable> E optSavable(String key, E defaultValue) {
        final Object object = this.opt(key);
        if (JmeNull.NULL.equals(object)) {
            return defaultValue;
        }
        if (object instanceof Savable) {
            return (E) object;
        }
        return defaultValue;
    }
    
    /**
     * Rellena el mapa interno de JmeProperties con las propiedades del bean. El
     * bean no puede ser recursivo.
     *
     * @see JSONObject#JSONObject(Object)
     *
     * @param bean
     *            el bean
     */
    private void populateMap(Object bean) {
        populateMap(bean, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private void populateMap(Object bean, Set<Object> objectsRecord) {
        Class<?> klass = bean.getClass();

        // Si klass es una clase del sistema, establezca includeSuperClass en falso.

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null) {
                            // comprueba la dependencia cíclica y arroja un error si es necesario
                            // el método de combinación wrap y populateMap es
                            // en sí mismo DFS recursivo
                            if (objectsRecord.contains(result)) {
                                throw recursivelyDefinedObjectException(key);
                            }
                            
                            objectsRecord.add(result);

                            this.map.put(key, wrap(result, objectsRecord));

                            objectsRecord.remove(result);

                            // no usamos el resultado en ninguna parte fuera de la envoltura
                            // si es un recurso debemos asegurarnos de cerrarlo
                            // después de llamar a String
                            if (result instanceof Closeable) {
                                try {
                                    ((Closeable) result).close();
                                } catch (IOException ignore) {
                                }
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignore) {
                    }
                }
            }
        }
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    private static String getKeyNameFromMethod(Method method) {
        final int ignoreDepth = getAnnotationDepth(method, JmePropertyIgnore.class);
        if (ignoreDepth > 0) {
            final int forcedNameDepth = getAnnotationDepth(method, JmePropertyName.class);
            if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) {
                // la jerarquía pidió ignorar, y el nombre más cercano anula
                // era mayor o inexistente
                return null;
            }
        }
        JmePropertyName annotation = getAnnotation(method, JmePropertyName.class);
        if (annotation != null && annotation.value() != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) {
            key = name.substring(2);
        } else {
            return null;
        }
        // si la primera letra de la clave no está en mayúscula, salte.
        // Esto es para mantener la compatibilidad con versiones anteriores antes de PR406
        // (https://github.com/stleary/JSON-java/pull/406/)
        if (key.length() == 0 || Character.isLowerCase(key.charAt(0))) {
            return null;
        }
        if (key.length() == 1) {
            key = key.toLowerCase(Locale.ROOT);
        } else if (!Character.isUpperCase(key.charAt(1))) {
            key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
        }
        return key;
    }

    /**
     * Busca en la jerarquía de clases para ver si el método es super e
     * implementa las interfaze o tiene la anotación.
     *
     * @param <A>
     *            tipo de anotación
     *
     * @param m
     *            método para comprobar
     * @param annotationClass
     *            anotación a buscar
     * @return si {@link Annotation} la anotación existe en el método actual
     *          o una de sus definiciones de superclase
     */
    private static <A extends Annotation> A getAnnotation(final Method m, final Class<A> annotationClass) {
        // if we have invalid data the result is null
        if (m == null || annotationClass == null) {
            return null;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return m.getAnnotation(annotationClass);
        }

        // si ya llegamos a la clase Object, devuelve nulo;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return null;
        }

        // comprueba las interfaces implementadas directamente para el método que se está comprobando
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                return getAnnotation(im, annotationClass);
            } catch (final SecurityException | 
                        NoSuchMethodException ex) {
            }
        }

        try {
            return getAnnotation(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
        } catch (final SecurityException | 
                        NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * Busca en la jerarquía de clases para ver si el método es super
     * implementaciones e interfaces tiene la anotación. Devuelve la profundidad de la
     * anotación en la jerarquía.
     *
     * @param m
     *            método para comprobar
     * @param annotationClass
     *            anotación a buscar
     * @return Profundidad de la anotación o -1 si la anotación no está en el método.
     */
    private static int getAnnotationDepth(final Method m, final Class<? extends Annotation> annotationClass) {
        // si tenemos datos inválidos el resultado es -1
        if (m == null || annotationClass == null) {
            return -1;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return 1;
        }

        // si ya llegamos a la clase Object, devuelve -1;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return -1;
        }

        // comprueba las interfaces implementadas directamente para el método que se está comprobando
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                int d = getAnnotationDepth(im, annotationClass);
                if (d > 0) {
                    // dado que la anotación estaba en la interfaz, agregue 1
                    return d + 1;
                }
            } catch (final SecurityException |
                            NoSuchMethodException ex) {
            }
        }

        try {
            int d = getAnnotationDepth(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
            if (d > 0) {
                // dado que la anotación estaba en la superclase, agregue 1
                return d + 1;
            }
            return -1;
        } catch (final SecurityException | 
                        NoSuchMethodException ex) {
            return -1;
        }
    }
    
    /**
     * Coloque un par clave/boolean en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un boolean que es el valor.
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, boolean value) throws JmeException {
        return this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    /**
     * Coloque un par clave/valor en el JmeProperties, donde el valor será un
     * JmeArray que se produce a partir de una Colección.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Una coleccion como valor.
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, Collection<?> value) throws JmeException {
        return this.put(key, new JmeArray(value));
    }
    
    /**
     * Coloque un par clave/double en el JmeProperties.
     *
     * @param key
     *            Una cadena como clave.
     * @param value
     *            Un double como valor-
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la lcave es <code>null</code>.
     */
    public JmeProperties put(String key, double value) throws JmeException {
        return this.put(key, Double.valueOf(value));
    }
    
    /**
     * Coloque un par clave/float en el JmeProperties.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un float como valor
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, float value) throws JmeException {
        return this.put(key, Float.valueOf(value));
    }
    
    /**
     * Coloque un par clave/int en el JmeProperties.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un int como valor.
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, int value) throws JmeException {
        return this.put(key, Integer.valueOf(value));
    }
    
    /**
     * Coloque un par clave/long en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un long como valor..
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, long value) throws JmeException {
        return this.put(key, Long.valueOf(value));
    }
    
    /**
     * Coloque un par clave/byte en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un byte como valor..
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, byte value) throws JmeException {
        return this.put(key, Byte.valueOf(value));
    }
    
    /**
     * Coloque un par clave/char en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un char como valor..
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, char value) throws JmeException {
        return this.put(key, Character.valueOf(value));
    }
    
    /**
     * Coloque un par clave/short en el JmeProperties
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un short como valor..
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, short value) throws JmeException {
        return this.put(key, Short.valueOf(value));
    }
    
    /**
     * Coloque un par clave/valor en el JmeProperties, donde el valor será un
     * JmeProperties que se produce a partir de un Mapa.
     *
     * @param key
     *            Una caden clave.
     * @param value
     *            Un Map como valor.
     * @return this.
     * @throws JmeException
     *            Si el valor es un número no finito.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, Map<?, ?> value) throws JmeException {
        return this.put(key, new JmeProperties(value));
    }
    
    /**
     * Coloque un par clave/valor en el JmeProperties. Si el valor es <code>null</code>, entonces el
     * La clave se eliminará del JmeProperties si está presente.
     *
     * @param key
     *            Un cadena como clave..
     * @param value
     *            Un objeto que es el valor. debe ser de uno de estos
     *            tipos: boolean, double, int, JmeArray, JmeProperties, long,
     *            String, o el objeto JmeNull.
     * @return this.
     * @throws JmeException
     *             Si el valor es un número no finito o el valor no est
     *              soportado.
     * @throws NullPointerException
     *            Si la clave es <code>null</code>.
     */
    public JmeProperties put(String key, Object value) throws JmeException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            if (value instanceof Savable) {
                this.map.put(key, (Savable) value);
            } else {
                testValidity(value);
                this.map.put(key, new JmePrimitive(value));
            }
        } else {
            this.remove(key);
        }
        return this;
    }
    
    /**
     * Coloque un par clave/valor en el JmeProperties, pero solo si la clave y el valor
     * ambos no son nulos, y solo si aún no hay un miembro con eso
     * nombre.
     *
     * @param key
     *            clave para insertar en
     * @param value
     *            valor a insertar
     * @return this.
     * @throws JmeException
     *             si la llave es un duplicado
     */
    public JmeProperties putOnce(String key, Object value) throws JmeException {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new JmeException("Duplicate key \"" + key + "\"");
            }
            return this.put(key, value);
        }
        return this;
    }

    /**
     * Coloque un par clave/valor en JmeProperties pero solo si la clave y el valor
     * ambos no son nulos.
     *
     * @param key
     *            Una cadena clave.
     * @param value
     *            Un objeto que es el valor. debe ser de uno de estos
     *            tipos: booleano, double, entero, JmeArray, JmeProperties, long,
     *            String, o el objeto JmeNullL.
     * @return this.
     * @throws JmeException
     *             Si el valor es un número no finito.
     */
    public JmeProperties putOpt(String key, Object value) throws JmeException {
        if (key != null && value != null) {
            return this.put(key, value);
        }
        return this;
    }
    
    /**
     * Encargado de exportar el JmeProperties a binarios. Se utiza el objeto
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
        
        // Exportamos el mapa o el diccionario del
        // JmeProperties a binario.
        out.writeStringSavableMap(map, "JmeMap", null);
    }

    /**
     * Importara los datos del JmeProperties, Se utilizara el obejto
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
        
        // Importamos los datos del mapa o diccionario que
        // utiliza JmeProperties como almacenamiento de datos.
        this.map = (HashMap<String, Savable>) in.readStringSavableMap("JmeMap", map);
    }
    
    /**
     * Eliminar un nombre y su valor, si está presente.
     *
     * @param key
     *            El nombre que se va a eliminar.
     * @return El valor que se asoció con el nombre, o nulo si no
     *         hubo valor.
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }
    
    /**
     * Producir un JmeArray que contenga los valores de los miembros de este
     * Objeto de Propiedades.
     *
     * @param names
     *            Un JmeArray que contiene una lista de cadenas de claves. Esto determina
     *              la secuencia de los valores en el resultado.
     * @return Un JmeArray de valores.
     * @throws JmeException
     *             Si alguno de los valores son números no finitos.
     */
    public JmeArray toJmeArray(JmeArray names) throws JmeException {
        if (names == null || names.isEmpty()) {
            return null;
        }
        JmeArray ja = new JmeArray();
        for (int i = 0; i < names.length(); i += 1) {
            Object object = this.opt(names.getString(i));
            if (object == null) {
                object = JmeNull.NULL;
            }
            ja.put(object);
        }
        return ja;
    }

    /**
     * Devuelve un java.util.Map que contiene todas las entradas de este objeto.
     * Si una entrada en el objeto es JmeArray o JmeProperties, también
     * seran convertido.
     *
     * <p>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </p>
     * 
     * @return un java.util.Map que contiene las entradas de este objeto
     */
    public Map<String, Object> toMap() {
        Map<String, Object> results = new HashMap<>();
        for (Entry<String, Savable> entry : this.entrySet()) {
            Object value;
            if (entry.getValue() == null || JmeNull.NULL.equals(entry.getValue()) || entry.getValue() instanceof JmeNull) {
                value = null;
            } else if (entry.getValue() instanceof JmePrimitive) {
                value = ((JmePrimitive) entry.getValue()).getValue();
            } else if (entry.getValue() instanceof JmeProperties) {
                value = ((JmeProperties) entry.getValue()).toMap();
            } else if (entry.getValue() instanceof JmeArray) {
                value = ((JmeArray) entry.getValue()).toList();
            } else {
                value = entry.getValue();
            }
            results.put(entry.getKey(), value);
        }
        return results;
    }
    
    /**
     * Genera el codigo hahs de la clase u objeto {@link JmeProperties}.
     * 
     * @return codigo hahs del objeto.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.map);
        return hash;
    }

    /**
     * Un objeto {@link JmeProperties} es igual a sí mismo.
     *
     * @param obj
     *            Un objeto para probar la nulidad.
     * @return true si el parámetro del objeto es el objeto JmeProperties o
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
        final JmeProperties other = (JmeProperties) obj;
        return Objects.equals(this.map, other.map);
    }
    
    private static void log(String smg, Object[] args) {
        JmeProperties.LOG.log(Level.WARNING, new String(new StringBuilder().append("JmeProperties")
                                                                           .append('\n')
                                                                           .append('\t')
                                                                           .append(smg)), args);
    }
    
    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return false;
        } else if (n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN())) {
            return false;
        }
        return true;
    }
    
     /**
     * Comprueba si el valor debe probarse como un decimal. No prueba si hay dígitos reales.
     *
     * @param val 
     *          valor a probar
     * @return verdadero si la cadena es "-0" o si contiene '.', 'e' o 'E', falso en caso contrario.
     */
    protected static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }
    
   /**
     * Convierte una cadena en un número usando el tipo más estrecho posible. Posible
     * Los resultados de esta función son BigDecimal, Double, BigInteger, Long e Integer.
     * Cuando se devuelve un Doble, siempre debe ser un Doble válido y no NaN o +-infinito.
     *
     * @param val 
     *          valor para convertir
     * @return Representación numérica del valor.
     * @throws NumberFormatException 
     *                  lanzado si el valor no es un número válido. La persona
     *                  que llama debe captar esto y envolverlo en una {@link JmeException} si corresponde.
     */
    protected static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            // representación decimal
            if (isDecimalNotation(val)) {
                // Usar un BigDecimal todo el tiempo para mantener el original
                // representación. BigDecimal no admite -0.0, asegúrese de que
                // mantener eso forzando un decimal.
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if(initial == '-' && BigDecimal.ZERO.compareTo(bd)==0) {
                        return -0.0d;
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    // esto es para soportar "Hex Floats" como este: 0x1.0P-1074
                    try {
                        Double d = Double.valueOf(val);
                        if(d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val ["+val+"] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val ["+val+"] is not a valid number.");
                    }
                }
            }
            // bloquea elementos como 00 01, etc. Los analizadores de números de Java los tratan como octales.
            if(initial == '0' && val.length() > 1) {
                char at1 = val.charAt(1);
                if(at1 >= '0' && at1 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            } else if (initial == '-' && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if(at1 == '0' && at2 >= '0' && at2 <= '9') {
                    throw new NumberFormatException("val ["+val+"] is not a valid number.");
                }
            }
            // representación de enteros.
            // Esto reducirá cualquier valor a la representación de objeto razonable más pequeña
            // (Integer, Long o BigInteger)

            // Conversión descendente de BigInteger: Usamos una comparación de longitud de bits similar a
            // BigInteger#intValueExact usa. Aumenta GC, pero los objetos se mantienen
            // solo lo que necesitan. es decir, menos sobrecarga de tiempo de ejecución si el valor es
            // larga vida.
            BigInteger bi = new BigInteger(val);
            if(bi.bitLength() <= 31){
                return bi.intValue();
            }
            if(bi.bitLength() <= 63){
                return bi.longValue();
            }
            return bi;
        }
        throw new NumberFormatException("val ["+val+"] is not a valid number.");
    }
    
    /**
     * Lanza una excepción si el objeto es un NaN o un número infinito.
     *
     * @param o
     *            El objeto a probar.
     * @throws JmeException
     *             Si o es un número no finito.
     */
    protected static void testValidity(Object o) throws JmeException {
        if (o instanceof Number && !numberIsFinite((Number) o)) {
            throw new JmeException("JmeProperties does not allow non-finite numbers.");
        }
    }
    
    /**
     * Haz un texto JSON de este JmePropertiesa. Por compacidad, no hay espacios en blanco
     * adicional. Si esto no da como resultado un texto JSON sintácticamente correcto,
     * entonces se devolverá nulo en su lugar.
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b></p>
     *
     * @return una representación imprimible, visualizable, portátil y transmisible
     *         del objeto, comenzando con <code>{</code>&nbsp;<small>(llav
     *         izquierdo)</small> y terminando en <code>}</code>&nbsp;<small>(llave
     *         derecho)</small>.
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
     * Haga un texto JSON bastante impreso de este JmeProperties.
     *
     * <p>Si <pre><code> indentFactor > 0</code></pre> y el {@link JmeProperties}
     * tiene solo una clave, entonces el objeto se generará en una sola línea:
     * <pre><code>{"clave": 1}</code></pre>
     *
     * <p>Si un objeto tiene 2 o más claves, se generará a través de
     * líneas múltiples: <pre><code> {
     * "clave1": 1,
     * "clave2": "valor 2",
     * "clave3": 3
     * }</code></pre>
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b></p>
     *
     * @param indentFactor
     *            El número de espacios para agregar a cada nivel de sangría.
     * @return una representación imprimible, visualizable, portátil y transmisible
     *         del objeto, comenzando con <code>{</code>&nbsp;<small>(llav
     *         izquierdo)</small> y terminando en <code>}</code>&nbsp;<small>(llave
     *         derecho)</small>.
     * @throws JmeException
     *             Si el objeto contiene un número no válido.
     */
    @SuppressWarnings("resource")
    public String toString(int indentFactor) throws JmeException {
        StringWriter w = new StringWriter();
        synchronized (w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString();
        }
    }
    
    /**
     * Envuelva un objeto, si es necesario. Si el objeto es <code>null</code>, devuelve NULL
     * objeto. Si es una matriz o una colección, envuélvala en un JmeArray. Si esto es
     * un mapa, envuélvalo en un JmeProperties. Si es una propiedad estándar (Double,
     * String, etc.) entonces ya está envuelto. En caso contrario, si procede
     * uno de los paquetes java, convertirlo en una cadena. Y si no es así, prueba
     * para envolverlo en un JmeProperties. Si el ajuste falla, se devuelve nulo.
     *
     * @param object
     *            El objeto a envolver
     * @return El valor envuelto
     */
    protected static Savable wrap(Object object) {
        return wrap(object, null);
    }
    
    private static Savable wrap(Object object, Set<Object> objectsRecord) {
        try {
            if (JmeNull.NULL.equals(object) || object == null) {
                return (Savable) JmeNull.NULL;
            }
            
            if (object instanceof Savable) {
                return (Savable) object;
            } else {
                if (object instanceof Collection) {
                    Collection<?> coll = (Collection<?>) object;
                    return new JmeArray(coll);
                }
                if (object.getClass().isArray()) {
                    return new JmeArray(object);
                }
                if (object instanceof Map) {
                    Map<?, ?> map = (Map<?, ?>) object;
                    return new JmeProperties(map);
                }
                
                try {
                    JmeType jmeType = JmeType.jmeValueOf(object);
                    
                    if (jmeType != null)
                        return new JmePrimitive(object);
                } catch (JmeException e) {
                    // Continuamos para verificar mas copiones.
                }
                
                Package objectPackage = object.getClass().getPackage();
                String objectPackageName = objectPackage != null ? objectPackage
                        .getName() : "";
                if (objectPackageName.startsWith("java.")
                        || objectPackageName.startsWith("javax.")
                        || object.getClass().getClassLoader() == null) {
                    return new JmePrimitive(String.valueOf(object));
                }
                if (objectsRecord != null) {
                    return new JmeProperties(object, objectsRecord);
                }
                else {
                    return new JmeProperties(object);
                }
            }
        } catch (JmeException e) {
            throw e;
        }
    }
    
    /**
     * Escriba el contenido de JmeProperties como texto JSON para un escritor. Para
     * compacidad, no se agregan espacios en blanco.
     * <p><b>
     * Advertencia: este método asume que la estructura de datos es acíclica.
     * </b>
     * 
     * @param writer 
     *              El objeto escritor
     * @return El escritor
     * @throws JmeException 
     *                  si una función llamada tiene un error
     */
    public Writer write(Writer writer) throws JmeException {
        return this.write(writer, 0, 0);
    }

    @SuppressWarnings("resource")
    static final Writer writeValue(Writer writer, Object value,
            int indentFactor, int indent) throws JmeException, IOException {
        if (value == null || value.equals(null)) {
            writer.write("null");
        } else if (value instanceof JmeString) {
            Object o;
            try {
                o = ((JmeString) value).toJmeString();
            } catch (Exception e) {
                throw new JmeException(e);
            }
            writer.write(o != null ? o.toString() : quote(value.toString()));
        } else if (value instanceof Number) {
            // no todos los números pueden coincidir con los números JSON reales. es decir, fracciones o imaginario
            final String numberAsString = numberToString((Number) value);
            if(NUMBER_PATTERN.matcher(numberAsString).matches()) {
                writer.write(numberAsString);
            } else {
                // El valor del número no es un número válido.
                // En cambio, lo citaremos como una cadena
                quote(numberAsString, writer);
            }
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof Enum<?>) {
            writer.write(quote(((Enum<?>)value).name()));
        } else if (value instanceof JmeProperties) {
            ((JmeProperties) value).write(writer, indentFactor, indent);
        } else if (value instanceof JmeArray) {
            ((JmeArray) value).write(writer, indentFactor, indent);
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            new JmeProperties(map).write(writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            new JmeArray(coll).write(writer, indentFactor, indent);
        } else if (value.getClass().isArray()) {
            new JmeArray(value).write(writer, indentFactor, indent);
        } else {
            quote(value.toString(), writer);
        }
        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i += 1) {
            writer.write(' ');
        }
    }

    /**
     * Escriba el contenido de JmeProperties como texto JSON para un escritor.
     *
     * <p>Si <pre><code> indentFactor > 0</code></pre> y el {@link JmeProperties}
     * tiene solo una clave, entonces el objeto se generará en una sola línea:
     * <pre><code> {"clave": 1}</code></pre>
     *
     * <p>Si un objeto tiene 2 o más claves, se generará a través de
     * líneas múltiples: <pre><code> {
     * "clave1": 1,
     * "clave2": "valor 2",
     * "clave3": 3
     * }</code></pre>
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
     * @return Lo escrito.
     * @throws JmeException si una función llamada tiene un error o un error de escritura
     * ocurre
     */
    @SuppressWarnings("resource")
    public Writer write(Writer writer, int indentFactor, int indent)
            throws JmeException {
        try {
            boolean needsComma = false;
            final int length = this.length();
            writer.write('{');

            if (length == 1) {
            	final Entry<String,?> entry = this.entrySet().iterator().next();
                final String key = entry.getKey();
                writer.write(quote(key));
                writer.write(':');
                if (indentFactor > 0) {
                    writer.write(' ');
                }
                try{
                    writeValue(writer, entry.getValue(), indentFactor, indent);
                } catch (JmeException | IOException e) {
                    throw new JmeException("Unable to write JmeProperties value for key: " + key, e);
                }
            } else if (length != 0) {
                final int newIndent = indent + indentFactor;
                for (final Entry<String,?> entry : this.entrySet()) {
                    if (needsComma) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    indent(writer, newIndent);
                    final String key = entry.getKey();
                    writer.write(quote(key));
                    writer.write(':');
                    if (indentFactor > 0) {
                        writer.write(' ');
                    }
                    try {
                        writeValue(writer, entry.getValue(), indentFactor, newIndent);
                    } catch (JmeException | IOException e) {
                        throw new JmeException("Unable to write JmeProperties value for key: " + key, e);
                    }
                    needsComma = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, indent);
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new JmeException(exception);
        }
    }

    
    /**
     * Produce una cadena entre comillas dobles con secuencias de barra invertida en todos los
     * lugares correctos. Se insertará una barra invertida dentro de &lt;/, produciendo
     * &lt;\/, lo que permite que el texto de propieades se entregue en HTML. En texto de propiedades, una
     * cadena no puede contener un carácter de control o una comilla sin escape o
     * barra invertida.
     *
     * @param string
     *            Una cadena
     * @return Una cadena con el formato correcto para la inserción en un texto JmeProperties.
     */
    protected static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // nunca sucedera - estamos escribiendo a un escritor de cadenas
                return "";
            }
        }
    }

    protected static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }
    
    /**
     * Cree una nueva JmeException en un formato comun para conversiones incorrectas.
     * @param key nombre de la llave
     * @param valueType el tipo de valor al que se obliga
     * @param cause causa opcional de la falla de coerción
     * @return JmeException que se puede tirar.
     */
    private static JmeException wrongValueFormatException(
            String key,
            String valueType,
            Object value,
            Throwable cause) {
        if(value == null) {

            return new JmeException(
                    "JmeProperties[" + quote(key) + "] is not a " + valueType + " (null)."
                    , cause);
        }
        // no intente hacer cadenas de colecciones o tipos de objetos conocidos que podrian ser grandes.
        if(value instanceof Map || value instanceof Iterable || value instanceof JmeProperties) {
            return new JmeException(
                    "JmeProperties[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + ")."
                    , cause);
        }
        return new JmeException(
                "JmeProperties[" + quote(key) + "] is not a " + valueType + " (" + value.getClass() + " : " + value + ")."
                , cause);
    }
    
    /**
     * Cree una nueva JmeException en un formato común para la definición de objetos recursivos.
     * @param key nombre de la llave
     * @return JmeException causa que se puede lanzar.
     */
    private static JmeException recursivelyDefinedObjectException(String key) {
        return new JmeException(
            "JavaBean object contains recursively defined member variable of key " + quote(key)
        );
    }
}
