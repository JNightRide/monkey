/*
 * Copyright 2023 wil.
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
package org.monkey.export.json;

import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.IntMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * (non-JavaDoc).
 * @since 1.6.5
 */
public class JsonOutputCapsule implements OutputCapsule {    
    
    private final JsonSerializer jsonserializer;
    private final JsonExporter jsonExporter;
    private final JSONObject jsonSavable;

    public JsonOutputCapsule(JsonExporter jsonExporter, JSONObject jsonSavable) {
        this.jsonserializer = jsonExporter.getSerializer();
        this.jsonExporter = jsonExporter;
        this.jsonSavable = jsonSavable;        
    }

    protected JSONObject getJsonSavable() {
        return jsonSavable;
    }
    
    @Override
    public void write(byte value, String name, byte defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(byte[] value, String name, byte[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final byte e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(byte[][] value, String name, byte[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final byte[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final byte cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(int value, String name, int defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(int[] value, String name, int[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final int e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(int[][] value, String name, int[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final int[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final int cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(float value, String name, float defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(float[] value, String name, float[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final float e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(float[][] value, String name, float[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final float[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final float cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(double value, String name, double defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(double[] value, String name, double[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final double e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(double[][] value, String name, double[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final double[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final double cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(long value, String name, long defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(long[] value, String name, long[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final long e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(long[][] value, String name, long[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final long[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final long cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(short value, String name, short defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(short[] value, String name, short[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final short e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(short[][] value, String name, short[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final short[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final short cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(boolean value, String name, boolean defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == defVal)
            return;
        jsonSavable.put(name, value);
    }

    @Override
    public void write(boolean[] value, String name, boolean[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final boolean e : value) {
            array.put(e);
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(boolean[][] value, String name, boolean[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final boolean [] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final boolean cl : rs) {
                    element.put(cl);
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(String value, String name, String defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value != null && 
                value.equals(defVal))
            return;
        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
        } else {
            jsonSavable.put(name, value);
        }        
    }

    @Override
    public void write(String[] value, String name, String[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }        
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final String e : value) {
            if (e == null) {
                array.put(JSONObject.NULL);
            } else {
                array.put(e);
            }
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(String[][] value, String name, String[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray table = new JSONArray();
        for (final String[] rs : value) {
            if (rs == null) {
                table.put(JSONObject.NULL);
            } else {
                JSONArray element = new JSONArray();
                for (final String cl : rs) {
                    if (cl == null) {
                        element.put(JSONObject.NULL);
                    } else {
                        element.put(cl);
                    }
                }
                table.put(element);
            }
        }
        jsonSavable.put(name, table);
    }

    @Override
    public void write(BitSet value, String name, BitSet defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value != null 
                && value.equals(defVal)) {
            return;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
        } else {
            JSONArray bits = new JSONArray();
            for (int i = value.nextSetBit(0); 
                        i >= 0; i = value.nextSetBit(i + 1)) {
                bits.put(i);
            }
            jsonSavable.put(name, bits);
        }
    }

    @Override
    public void write(Savable object, String name, Savable defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (object != null 
                && object.equals(defVal)) {
            return;
        }        
        if (object == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONObject savable = jsonExporter.processJsonCapsule(object);
        jsonSavable.put(name, savable);
    }

    @Override
    public void write(Savable[] objects, String name, Savable[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (objects != null 
                && Arrays.equals(objects, defVal)) {
            return;
        }
        if (objects == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray array = new JSONArray();
        for (final Savable obj : objects) {
            if (obj == null) {
                array.put(JSONObject.NULL);
            } else {
                array.put(jsonExporter.processJsonCapsule(obj));
            }
        }
        jsonSavable.put(name, array);
    }

    @Override
    public void write(Savable[][] objects, String name, Savable[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (objects != null 
                && Arrays.deepEquals(objects, defVal)) {
            return;
        }
        if (objects == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray rows = new JSONArray();
        for (final Savable[] array : objects) {
            if (array == null) {
                rows.put(JSONObject.NULL);
            } else {
                JSONArray cols = new JSONArray();
                for (final Savable obj : array) {
                    if (obj == null) {
                        cols.put(JSONObject.NULL);
                    } else {
                        cols.put(jsonExporter.processJsonCapsule(obj));
                    }
                }
                rows.put(cols);
            }
        }
        jsonSavable.put(name, rows);
    }

    @Override
    public void writeSavableArrayList(ArrayList array, String name, ArrayList defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (array != null 
                && array.equals(defVal)) {
            return;
        }
        if (array == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray savableArray = new JSONArray();
        for (final Object obj : array) {
            if (obj == null) {
                savableArray.put(JSONObject.NULL);
            } else {
                if (!(obj instanceof Savable)) {
                    throw new IOException("Object[" + obj.getClass() + "] not supported");
                }
                
                Savable jmeObj = (Savable) obj;
                savableArray.put(jsonExporter.processJsonCapsule(jmeObj));
            }
        }
        
        jsonSavable.put(name, savableArray);
    }

    @Override
    public void writeSavableArrayListArray(ArrayList[] array, String name, ArrayList[] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (array != null 
                && Arrays.equals(array, defVal)) {
            return;
        }
        if (array == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray rootArray = new JSONArray();
        for (final ArrayList<?> element : array) {
            if (element == null) {
                rootArray.put(JSONObject.NULL);
            } else {
                JSONArray savableArray = new JSONArray();
                for (final Object obj : element) {
                    if (obj == null) {
                        savableArray.put(JSONObject.NULL);
                    } else {
                        if (!(obj instanceof Savable)) {
                            throw new IOException("Object[" + obj.getClass() + "] not supported");
                        }

                        Savable jmeObj = (Savable) obj;
                        savableArray.put(jsonExporter.processJsonCapsule(jmeObj));
                    }
                }
                
                rootArray.put(savableArray);
            }
        }
        
        jsonSavable.put(name, rootArray);
    }

    @Override
    public void writeSavableArrayListArray2D(ArrayList[][] array, String name, ArrayList[][] defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (array != null 
                && Arrays.deepEquals(array, defVal)) {
            return;
        }
        if (array == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray rooValue = new JSONArray();
        for (final ArrayList<?>[] list : array) {
            if (list == null) {
                rooValue.put(JSONObject.NULL);
            } else {
                JSONArray rootArray = new JSONArray();
                for (final ArrayList<?> element : list) {
                    if (element == null) {
                        rootArray.put(JSONObject.NULL);
                    } else {
                        JSONArray savableArray = new JSONArray();
                        for (final Object obj : element) {
                            if (obj == null) {
                                savableArray.put(JSONObject.NULL);
                            } else {
                                if (!(obj instanceof Savable)) {
                                    throw new IOException("Object[" + obj.getClass() + "] not supported");
                                }

                                Savable jmeObj = (Savable) obj;
                                savableArray.put(jsonExporter.processJsonCapsule(jmeObj));
                            }
                        }
                        rootArray.put(savableArray);
                    }
                }
                rooValue.put(rootArray);
            }
        }
        jsonSavable.put(name, rooValue);
    }

    @Override
    public void writeFloatBufferArrayList(ArrayList<FloatBuffer> array, String name, ArrayList<FloatBuffer> defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (array != null 
                && array.equals(defVal)) {
            return;
        }
        if (array == null) {
            jsonSavable.put(name, JSONObject.NULL);
        } else {
            JSONArray listBuff = new JSONArray();
            for (final FloatBuffer value : array) {
                if (value == null) {
                    listBuff.put(JSONObject.NULL);
                } else {
                    JSONArray buff = new JSONArray();
        
                    int pos = value.position();
                    value.rewind();
                    int ctr = 0;
                    while (value.hasRemaining()) {
                        ctr++;

                        buff.put(value.get());
                    }
                    if (ctr != value.limit()) {
                        throw new IOException("'" + name
                            + "' buffer contention resulted in write data consistency.  "
                            + ctr + " values written when should have written "
                            + value.limit());
                    }

                    value.position(pos);
                    listBuff.put(buff);
                }
            }
            
            jsonSavable.put(name, listBuff);
        }
    }

    @Override
    public void writeByteBufferArrayList(ArrayList<ByteBuffer> array, String name, ArrayList<ByteBuffer> defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (array != null 
                && array.equals(defVal)) {
            return;
        }
        if (array == null) {
            jsonSavable.put(name, JSONObject.NULL);
        } else {
            JSONArray listBuff = new JSONArray();
            for (final ByteBuffer value : array) {
                if (value == null) {
                    listBuff.put(JSONObject.NULL);
                } else {
                    JSONArray buff = new JSONArray();
        
                    int pos = value.position();
                    value.rewind();
                    int ctr = 0;
                    while (value.hasRemaining()) {
                        ctr++;

                        buff.put(value.get());
                    }
                    if (ctr != value.limit()) {
                        throw new IOException("'" + name
                            + "' buffer contention resulted in write data consistency.  "
                            + ctr + " values written when should have written "
                            + value.limit());
                    }

                    value.position(pos);
                    listBuff.put(buff);
                }
            }
            
            jsonSavable.put(name, listBuff);
        }
    }

    @Override
    public void writeSavableMap(Map<? extends Savable, ? extends Savable> map, String name, Map<? extends Savable, ? extends Savable> defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (map != null 
                && map.equals(defVal)) {
            return;
        }
        if (map == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONArray mapList = new JSONArray();
        for (final Map.Entry<? extends Savable, 
                                ? extends Savable> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new IOException("Null key.");
            }
            
            JSONObject jsonoMapEntry = new JSONObject();
            jsonoMapEntry.put(JsonSerializer.SAVABLE_KEY, jsonExporter.processJsonCapsule(entry.getKey()));
            
            Savable value = entry.getValue();
            if (value == null) {
                jsonoMapEntry.put(JsonSerializer.SAVABLE_VAL, JSONObject.NULL);
            } else {
                jsonoMapEntry.put(JsonSerializer.SAVABLE_VAL, jsonExporter.processJsonCapsule(value));
            }
            
            mapList.put(jsonoMapEntry);
        }
        
        jsonSavable.put(name, mapList);
    }

    @Override
    public void writeStringSavableMap(Map<String, ? extends Savable> map, String name, Map<String, ? extends Savable> defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (map != null 
                && map.equals(defVal)) {
            return;
        }
        if (map == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONObject jsonMap = new JSONObject();
        for (final Map.Entry<String, ? extends Savable> entry : map.entrySet()) {
            if (entry.getKey() == null) {
                throw new IOException("Null key.");
            }
            
            final Savable value = entry.getValue();
            if (value == null) {
                jsonMap.put(entry.getKey(), JSONObject.NULL);
            } else {
                jsonMap.put(entry.getKey(), jsonExporter.processJsonCapsule(entry.getValue()));
            }
        }
        
        jsonSavable.put(name, jsonMap);
    }

    @Override
    public void writeIntSavableMap(IntMap<? extends Savable> map, String name, IntMap<? extends Savable> defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (map != null 
                && map.equals(defVal)) {
            return;
        }
        if (map == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        
        JSONObject jsonMap = new JSONObject();
        for (final IntMap.Entry<? extends Savable> entry : map) {
            int key = entry.getKey();
            Savable val = entry.getValue();
            
            if (val == null) {
                jsonMap.put(String.valueOf(key), JSONObject.NULL);
            } else {
                jsonMap.put(String.valueOf(key), jsonExporter.processJsonCapsule(val));
            }
        }
        jsonSavable.put(name, jsonMap);
    }

    @Override
    public void write(FloatBuffer value, String name, FloatBuffer defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            return;
        }
        
        JSONArray buff = new JSONArray();
        
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            
            buff.put(value.get());
        }
        if (ctr != value.limit()) {
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        }
        
        value.position(pos);
        jsonSavable.put(name, buff);
    }

    @Override
    public void write(IntBuffer value, String name, IntBuffer defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            return;
        }
        
        JSONArray buff = new JSONArray();
        
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            
            buff.put(value.get());
        }
        if (ctr != value.limit()) {
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        }
        
        value.position(pos);
        jsonSavable.put(name, buff);
    }

    @Override
    public void write(ByteBuffer value, String name, ByteBuffer defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            return;
        }
        
        JSONArray buff = new JSONArray();
        
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            
            buff.put(value.get());
        }
        if (ctr != value.limit()) {
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        }
        
        value.position(pos);
        jsonSavable.put(name, buff);
    }

    @Override
    public void write(ShortBuffer value, String name, ShortBuffer defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            return;
        }
        
        JSONArray buff = new JSONArray();
        
        int pos = value.position();
        value.rewind();
        int ctr = 0;
        while (value.hasRemaining()) {
            ctr++;
            
            buff.put(value.get());
        }
        if (ctr != value.limit()) {
            throw new IOException("'" + name
                + "' buffer contention resulted in write data consistency.  "
                + ctr + " values written when should have written "
                + value.limit());
        }
        
        value.position(pos);
        jsonSavable.put(name, buff);
    }

    @Override
    public void write(Enum value, String name, Enum defVal) throws IOException {
        jsonserializer.validName(name);
        
        if (value == null) {
            value = defVal;
        }
        if (value == null) {
            jsonSavable.put(name, JSONObject.NULL);
            return;
        }
        jsonSavable.put(name, value);
    }
}
