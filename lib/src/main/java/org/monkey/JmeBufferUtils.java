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

import com.jme3.util.BufferAllocator;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <code>BufferUtils</code> es una clase auxiliar
 * para generar {@link java.nio.Buffer}.
 * 
 * @author jme3
 * @version 1.0.0
 */
public final class JmeBufferUtils {
    
    /**
     * El campo debe ser final para admitir subprocesos seguros.
     */
    private static final BufferAllocator allocator = JmeBufferAllocatorFactory.create();
    
    private static boolean trackDirectMemory = false;
    final private static ReferenceQueue<Buffer> removeCollected = new ReferenceQueue<>();
    final private static ConcurrentHashMap<BufferInfo, BufferInfo> trackedBuffers = new ConcurrentHashMap<>();
    static ClearReferences cleanupthread;
    
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private JmeBufferUtils() {
    }

    /**
     * Establézcalo en verdadero si desea habilitar el seguimiento directo de la memoria para la depuración
     * apropósito. El valor predeterminado es falso. Para imprimir el uso directo de la memoria
     * JmeBufferUtils.printCurrentDirectMemory (tienda StringBuilder);
     *
     * @param enabled verdadero para habilitar el seguimiento, falso para deshabilitarlo
     * (predeterminado = falso)
     */
    public static void setTrackDirectMemoryEnabled(boolean enabled) {
        trackDirectMemory = enabled;
    }
    
    private static void onBufferAllocated(Buffer buffer) {

        if (JmeBufferUtils.trackDirectMemory) {
            if (JmeBufferUtils.cleanupthread == null) {
                JmeBufferUtils.cleanupthread = new ClearReferences();
                JmeBufferUtils.cleanupthread.start();
            }
            if (buffer instanceof ByteBuffer) {             // byte
                BufferInfo info = new BufferInfo(ByteBuffer.class, buffer.capacity(), buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof FloatBuffer) {     // float
                BufferInfo info = new BufferInfo(FloatBuffer.class, buffer.capacity() * 4, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof IntBuffer) {       // int
                BufferInfo info = new BufferInfo(IntBuffer.class, buffer.capacity() * 4, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof ShortBuffer) {     // short
                BufferInfo info = new BufferInfo(ShortBuffer.class, buffer.capacity() * 2, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof DoubleBuffer) {    // double
                BufferInfo info = new BufferInfo(DoubleBuffer.class, buffer.capacity() * 8, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof LongBuffer) {    // long
                BufferInfo info = new BufferInfo(LongBuffer.class, buffer.capacity() * 10, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            } else if (buffer instanceof CharBuffer) {    // char
                BufferInfo info = new BufferInfo(CharBuffer.class, buffer.capacity() * 2, buffer,
                        JmeBufferUtils.removeCollected);
                JmeBufferUtils.trackedBuffers.put(info, info);
            }
        }
    }
    
    /**
     * Cree un nuevo CharBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de char para almacenar.
     * @return el nuevo CharBuffer
     */
    public static CharBuffer createCharBuffer(int size) {
        CharBuffer buf = allocator.allocate(2 * size).order(ByteOrder.nativeOrder()).asCharBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genere un nuevo CharBuffer usando la matriz dada de char. El búfer de caracteres
     * será data.length largo y contendrá los datos int como datos [0], datos [1] ...
     * etc
     *
     * @param data
     *            matriz de char para colocar en corto a un nuevo CharBuffer
     * @return un nuevo CharBuffer directo, invertido o nulo si los datos eran nulos
     */
    public static CharBuffer createCharBuffer(char... data) {
        if (data == null) {
            return null;
        }
        CharBuffer buff = createCharBuffer(data.length);        
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz char[] y complétela con el CharBuffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz char desde el buffer.
     */
    public static char[] toCharArray(CharBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        char[] inds = new char[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }
    
    /**
     * Crea un nuevo CharBuffer con el mismo contenido que el
     * Charbuffer dado. El nuevo CharBuffer está separado del antiguo y
     * los cambios no se reflejan a través. Si quieres reflejar los cambios,
     * considere usar Buffer.duplicate ().
     *
     * @param buf
     *            el CharBuffer para copiar
     * @return la copia
     */
    public static CharBuffer clone(CharBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        CharBuffer copy;
        if (isDirect(buf)) {
            copy = createCharBuffer(buf.limit());
        } else {
            copy = CharBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo LongBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de long para almacenar.
     * @return el nuevo LongBuffer
     */
    public static LongBuffer createLongBuffer(int size) {
        LongBuffer buf = allocator.allocate(10 * size).order(ByteOrder.nativeOrder()).asLongBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static LongBuffer createLongBuffer(long... data) {
        if (data == null) {
            return null;
        }
        LongBuffer buff = createLongBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz long[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz long desde el buffer.
     */
    public static long[] toLongArray(LongBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        long[] inds = new long[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }
    
    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static LongBuffer clone(LongBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        LongBuffer copy;
        if (isDirect(buf)) {
            copy = createLongBuffer(buf.limit());
        } else {
            copy = LongBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo DoubleBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de doble para almacenar.
     * @return el nuevo DoubleBuffer
     */
    public static DoubleBuffer createDoubleBuffer(int size) {
        DoubleBuffer buf = allocator.allocate(8 * size).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static DoubleBuffer createDoubleBuffer(double... data) {
        if (data == null) {
            return null;
        }
        DoubleBuffer buff = createDoubleBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz double[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz double desde el buffer.
     */
    public static double[] toDoubleArray(DoubleBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        double[] inds = new double[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }
    
    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static DoubleBuffer clone(DoubleBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        DoubleBuffer copy;
        if (isDirect(buf)) {
            copy = createDoubleBuffer(buf.limit());
        } else {
            copy = DoubleBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo ShortBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de short para almacenar.
     * @return el nuevo ShortBuffer
     */
    public static ShortBuffer createShortBuffer(int size) {
        ShortBuffer buf = allocator.allocate(2 * size).order(ByteOrder.nativeOrder()).asShortBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static ShortBuffer createShortBuffer(short... data) {
        if (data == null) {
            return null;
        }
        ShortBuffer buff = createShortBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz short[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz short desde el buffer.
     */
    public static short[] toShortArray(ShortBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        short[] inds = new short[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static ShortBuffer clone(ShortBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        ShortBuffer copy;
        if (isDirect(buf)) {
            copy = createShortBuffer(buf.limit());
        } else {
            copy = ShortBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo ByteBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de char para almacenar.
     * @return el nuevo ByteBuffer
     */
    public static ByteBuffer createByteBuffer(int size) {
        ByteBuffer buf = allocator.allocate(size).order(ByteOrder.nativeOrder());
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static ByteBuffer createByteBuffer(byte... data) {
        if (data == null) {
            return null;
        }
        ByteBuffer buff = createByteBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz byte[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz byte desde el buffer.
     */
    public static byte[] toByteArray(ByteBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        byte[] inds = new byte[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static ByteBuffer clone(ByteBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        ByteBuffer copy;
        if (isDirect(buf)) {
            copy = createByteBuffer(buf.limit());
        } else {
            copy = ByteBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo intBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de int para almacenar.
     * @return el nuevo intBuffer
     */
    public static IntBuffer createIntBuffer(int size) {
        IntBuffer buf = allocator.allocate(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static IntBuffer createIntBuffer(int... data) {
        if (data == null) {
            return null;
        }
        IntBuffer buff = createIntBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz int[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz int desde el buffer.
     */
    public static int[] toIntArray(IntBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        int[] inds = new int[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }
    
    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static IntBuffer clone(IntBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        IntBuffer copy;
        if (isDirect(buf)) {
            copy = createIntBuffer(buf.limit());
        } else {
            copy = IntBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Cree un nuevo FloatBuffer del tamaño especificado.
     *
     * @param size
     *            número requerido de float para almacenar.
     * @return el nuevo FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(int size) {
        FloatBuffer buf = allocator.allocate(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buf.clear();
        onBufferAllocated(buf);
        return buf;
    }
    
    /**
     * Genera un buffer con una matris o secuencia de datos.
     * 
     * @param data
     *          Contenido del buffer.
     * @return El buffer generado.
     */
    public static FloatBuffer createFloatBuffer(float... data) {
        if (data == null) {
            return null;
        }
        FloatBuffer buff = createFloatBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }
    
    /**
     * Crea una nueva matriz float[] y complétela con el buffer dado
     * contenidos.
     *
     * @param buff
     *            el buffer para leer
     * @return una nueva matriz float desde el buffer.
     */
    public static float[] toFloatArray(FloatBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        float[] inds = new float[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }
    
    /**
     * Crear una copia/clona un buffer, sieno los burres que almacenan datos primitivos
     * (int. double, short, char...). Y si desea replicarlo utilize el metodo
     * <code>Buffer.duplicate()</code>
     * 
     * @param buf
     *          Buffer de datos a clonar-
     * @return Copia/Clon del buffer.
     */
    public static FloatBuffer clone(FloatBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        FloatBuffer copy;
        if (isDirect(buf)) {
            copy = createFloatBuffer(buf.limit());
        } else {
            copy = FloatBuffer.allocate(buf.limit());
        }
        copy.put(buf);

        return copy;
    }
    
    /**
     * Prueba si el búfer especificado es directo.
     *
     * @param buf El búfer para probar (no nulo, no afectado)
     * @return true si directo, de lo contrario false
     */
    private static boolean isDirect(Buffer buf) {
        return buf.isDirect();
    }
    
    private static class BufferInfo extends PhantomReference<Buffer> {

        private Class type;
        private int size;

        public BufferInfo(Class type, int size, Buffer referent, ReferenceQueue<? super Buffer> q) {
            super(referent, q);
            this.type = type;
            this.size = size;
        }
    }

    private static class ClearReferences extends Thread {

        ClearReferences() {
            this.setDaemon(true);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Reference<? extends Buffer> toclean = JmeBufferUtils.removeCollected.remove();
                    JmeBufferUtils.trackedBuffers.remove(toclean);
                }

            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
