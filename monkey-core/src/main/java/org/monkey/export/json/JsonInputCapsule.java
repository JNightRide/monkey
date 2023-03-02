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

import com.jme3.export.InputCapsule;
import com.jme3.export.Savable;
import com.jme3.export.SavableClassUtil;
import com.jme3.util.BufferUtils;
import com.jme3.util.IntMap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * (non-JavaDoc).
 * @since 1.6.5
 */
public class JsonInputCapsule implements InputCapsule {
    
    private final JsonSerializer jsonSerializer;
    private final JsonImporter jsonImporter;
    private final JSONObject jsonSavable;
    
    private final Savable jmeSavable;
    private final int[] classHierarchyVersions;

    public JsonInputCapsule(JsonImporter jsonImporter, JSONObject jsonSavable, 
                                Savable savable, int[] classHierarchyVersions) throws IOException {
        this.classHierarchyVersions = classHierarchyVersions;
        this.jsonSerializer = jsonImporter.getJsonSerializer();
        this.jsonImporter = jsonImporter;
        this.jsonSavable = jsonSavable;
        this.jmeSavable = savable;
    }

    protected Savable getJmeSavable() {
        return jmeSavable;
    }

    protected JSONObject getJsonSavable() {
        return jsonSavable;
    }
    
    @Override
    public int getSavableVersion(Class<? extends Savable> clazz) {
        return SavableClassUtil.getSavedSavableVersion(jmeSavable, clazz, 
                classHierarchyVersions, jsonImporter.getFormatVersion());
    }

    @Override
    public byte readByte(String name, byte defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getNumber(name).byteValue();
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public byte[] readByteArray(String name, byte[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            byte[] value = new byte[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                final Number n = array.getNumber(i);
                value[i] = n.byteValue();
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public byte[][] readByteArray2D(String name, byte[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<byte[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    byte[] elements = new byte[cols.length()];

                    for (int c = 0; c < cols.length(); c++) {
                        final Number num = cols.getBigDecimal(c);
                        elements[c] = num.byteValue();
                    }

                    list.add(elements);
                }
            }
            return list.toArray(byte[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int readInt(String name, int defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getInt(name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int[] readIntArray(String name, int[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }        
        if (jsonSavable.isNull(name)) {
            return null;
        }        
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            int[] value = new int[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                value[i] = array.getInt(i);
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public int[][] readIntArray2D(String name, int[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<int[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    int[] elements = new int[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        elements[c] = cols.getInt(c);
                    }

                    list.add(elements);
                }
            }
            return list.toArray(int[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public float readFloat(String name, float defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getFloat(name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public float[] readFloatArray(String name, float[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }  
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            float[] value = new float[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                value[i] = array.getFloat(i);
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public float[][] readFloatArray2D(String name, float[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<float[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    float[] elements = new float[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        elements[c] = cols.getFloat(c);
                    }

                    list.add(elements);
                }
            }
            return list.toArray(float[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public double readDouble(String name, double defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getDouble(name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public double[] readDoubleArray(String name, double[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }  
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            double[] value = new double[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                value[i] = array.getDouble(i);
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public double[][] readDoubleArray2D(String name, double[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<double[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    double[] elements = new double[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        elements[c] = cols.getDouble(c);
                    }

                    list.add(elements);
                }
            }
            return list.toArray(double[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public long readLong(String name, long defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getLong(name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public long[] readLongArray(String name, long[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            long[] value = new long[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                value[i] = array.getLong(i);
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public long[][] readLongArray2D(String name, long[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<long[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    long[] elements = new long[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        elements[c] = cols.getLong(c);
                    }

                    list.add(elements);
                }
            }
            return list.toArray(long[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public short readShort(String name, short defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getNumber(name).shortValue();
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public short[] readShortArray(String name, short[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            short[] value = new short[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                final Number n = array.getNumber(i);
                value[i] = n.shortValue();
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public short[][] readShortArray2D(String name, short[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<short[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    short[] elements = new short[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        final Number n = cols.getNumber(c);
                        elements[c] = n.shortValue();
                    }

                    list.add(elements);
                }
            }
            return list.toArray(short[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean readBoolean(String name, boolean defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            return jsonSavable.getBoolean(name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean[] readBooleanArray(String name, boolean[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            boolean[] value = new boolean[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                value[i] = array.getBoolean(i);
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean[][] readBooleanArray2D(String name, boolean[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<boolean[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    boolean[] elements = new boolean[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        elements[c] = cols.getBoolean(c);
                    }

                    list.add(elements);
                }
            }
            return list.toArray(boolean[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String readString(String name, String defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        try {
            if (jsonSavable.isNull(name)) {
                return null;
            } else {
                return jsonSavable.getString(name);
            }
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String[] readStringArray(String name, String[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            String[] value = new String[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                if (array.isNull(i)) {
                    value[i] = null;
                } else {
                    value[i] = array.getString(i);
                }
            }
            return value;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String[][] readStringArray2D(String name, String[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<String[]> list = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    list.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    String[] elements = new String[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        if (cols.isNull(c)) {
                            elements[c] = null;
                        } else {
                            elements[c] = cols.getString(c);
                        }
                    }

                    list.add(elements);
                }
            }
            return list.toArray(String[][]::new);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public BitSet readBitSet(String name, BitSet defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        
        try {
            JSONArray bitsArray = jsonSavable.getJSONArray(name);
            BitSet bits = new BitSet();
            for (int i = 0; i < bitsArray.length(); i++) {
                int isSet = bitsArray.getInt(i);
                if (isSet == 1) {
                    bits.set(i);
                }
            }
            return bits;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Savable readSavable(String name, Savable defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        
        try {
            JSONObject obj = jsonSavable.getJSONObject(name);
            return jsonImporter.readObject(obj);
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Savable[] readSavableArray(String name, Savable[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            Savable[] savables = new Savable[array.length()];
            
            for (int i = 0; i < array.length(); i++) {
                if (array.isNull(i)) {
                    savables[i] = null;
                } else {
                    savables[i] = jsonImporter.readObject(array.getJSONObject(i));
                }
            }
            return savables;
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Savable[][] readSavableArray2D(String name, Savable[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray rows = jsonSavable.getJSONArray(name);
            List<Savable[]> savables = new ArrayList<>();
            
            for (int r = 0; r < rows.length(); r++) {
                if (rows.isNull(r)) {
                    savables.add(null);
                } else {
                    JSONArray cols = rows.getJSONArray(r);
                    Savable[] objs = new Savable[cols.length()];
                    
                    for (int c = 0; c < cols.length(); c++) {
                        if (cols.isNull(c)) {
                            objs[c] = null;
                        } else {
                            objs[c] = jsonImporter.readObject(cols.getJSONObject(c));
                        }
                    }
                    
                    savables.add(objs);
                }
            }
            
            return savables.toArray(Savable[][]::new);
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArrayList readSavableArrayList(String name, ArrayList defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            ArrayList<? super Savable> list = new ArrayList<>(array.length());
            
            for (int i = 0; i < array.length(); i++) {
                if (array.isNull(i)) {
                    list.add(null);
                } else {
                    Savable obj = jsonImporter.readObject(array.getJSONObject(i));
                    list.add(obj);
                }
            }
            return list;
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArrayList[] readSavableArrayListArray(String name, ArrayList[] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray root = jsonSavable.getJSONArray(name);
            ArrayList<?>[] value = new ArrayList<?>[root.length()];
            
            for (int i = 0; i < root.length(); i++) {
                if (root.isNull(i)) {
                    value[i] = null;
                } else {
                    JSONArray array = root.getJSONArray(i);
                    ArrayList<? super Savable> list = new ArrayList<>(array.length());
                    
                    for (int j = 0; j < array.length(); j++) {
                        if (array.isNull(j)) {
                            list.add(null);
                        } else {
                            Savable obj = jsonImporter.readObject(array.getJSONObject(j));
                            list.add(obj);
                        }
                    }
                    
                    value[i] = list;
                }
            }
            
            return value;
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArrayList[][] readSavableArrayListArray2D(String name, ArrayList[][] defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray arrayAA = jsonSavable.getJSONArray(name);
            ArrayList<ArrayList<?>[]> value = new ArrayList(arrayAA.length());
            
            for (int a = 0; a < arrayAA.length(); a++) {
                if (arrayAA.isNull(a)) {
                    value.add(null);
                } else {
                    JSONArray arrayBB = arrayAA.getJSONArray(a);
                    ArrayList<?>[] elements = new ArrayList<?>[arrayBB.length()];
                    
                    for (int b = 0; b < arrayBB.length(); b++) {
                        if (arrayBB.isNull(b)) {
                            elements[b] = null;
                        } else {
                            JSONArray arrayCC = arrayBB.getJSONArray(b);
                            ArrayList<? super Savable> items = new ArrayList<>(arrayCC.length());
                            
                            for (int c = 0; c < arrayCC.length(); c++) {
                                if (arrayCC.isNull(c)) {
                                    items.add(null);
                                } else {
                                    Savable obj = jsonImporter.readObject(arrayCC.getJSONObject(c));
                                    items.add(obj);
                                }
                            }
                            
                            elements[b] = items;
                        }
                    }
                    
                    value.add(elements);
                }
            }
            
            return value.toArray(ArrayList<?>[][]::new);
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArrayList<FloatBuffer> readFloatBufferArrayList(String name, ArrayList<FloatBuffer> defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray arrayBuff = jsonSavable.getJSONArray(name);
            ArrayList<FloatBuffer> listBuff = new ArrayList<>(arrayBuff.length());
            
            for (int i = 0; i < arrayBuff.length(); i++) {
                if (arrayBuff.isNull(i)) {
                    listBuff.add(null);
                } else {
                    JSONArray array = arrayBuff.getJSONArray(i);
                    float[] nbuff = new float[array.length()];
                    for (int j = 0; j < array.length(); j++) {
                        nbuff[j] = array.getFloat(j);
                    }

                    FloatBuffer tmp = BufferUtils.createFloatBuffer(nbuff.length);
                    for (final float vn : nbuff) {
                        tmp.put(vn);
                    }
                    tmp.flip();
                    listBuff.add(tmp);
                }
            }
            
            return listBuff;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ArrayList<ByteBuffer> readByteBufferArrayList(String name, ArrayList<ByteBuffer> defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray arrayBuff = jsonSavable.getJSONArray(name);
            ArrayList<ByteBuffer> listBuff = new ArrayList<>(arrayBuff.length());
            
            for (int i = 0; i < arrayBuff.length(); i++) {
                if (arrayBuff.isNull(i)) {
                    listBuff.add(null);
                } else {
                    JSONArray array = arrayBuff.getJSONArray(i);
                    byte[] nbuff = new byte[array.length()];
                    for (int j = 0; j < array.length(); j++) {
                        Number b = array.getNumber(i);
                        nbuff[j] = b.byteValue();
                    }

                    ByteBuffer tmp = BufferUtils.createByteBuffer(nbuff.length);
                    for (final byte vn : nbuff) {
                        tmp.put(vn);
                    }
                    tmp.flip();
                    listBuff.add(tmp);
                }
            }
            
            return listBuff;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Map<? extends Savable, ? extends Savable> readSavableMap(String name, Map<? extends Savable, ? extends Savable> defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONArray arrayMap = jsonSavable.getJSONArray(name);
            Map<Savable, Savable> map = new HashMap<>(arrayMap.length());
            
            for (int i = 0; i < arrayMap.length(); i++) {
                JSONObject item = arrayMap.getJSONObject(i);
                
                Savable key = jsonImporter.readObject(item.getJSONObject(JsonSerializer.SAVABLE_KEY));
                if (item.isNull(JsonSerializer.SAVABLE_VAL)) {
                    map.put(key, null);
                } else {
                    Savable obj = jsonImporter.readObject(item.getJSONObject(JsonSerializer.SAVABLE_VAL));
                    map.put(key, obj);
                }
            }
            
            return map;
        } catch (IOException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Map<String, ? extends Savable> readStringSavableMap(String name, Map<String, ? extends Savable> defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONObject mapObject = jsonSavable.getJSONObject(name);
            
            Map<String, Savable> map = new HashMap<>(mapObject.length());
            Iterator<String> keys = mapObject.keys();
            
            while (keys.hasNext()) {
                String next = keys.next();
                
                if (mapObject.isNull(next)) {
                    map.put(next, null);
                } else {
                    Savable obj = jsonImporter.readObject(mapObject.getJSONObject(next));
                    map.put(next, obj);
                }
            }
            
            return map;
        } catch (IOException | JSONException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public IntMap<? extends Savable> readIntSavableMap(String name, IntMap<? extends Savable> defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
            return defVal;
        }
        if (jsonSavable.isNull(name)) {
            return null;
        }
        try {
            JSONObject mapObject = jsonSavable.getJSONObject(name);
            
            IntMap<Savable> map = new IntMap<>(mapObject.length());
            Iterator<String> keys = mapObject.keys();
            
            while (keys.hasNext()) {
                String next = keys.next();
                
                if (mapObject.isNull(next)) {
                    map.put(Integer.parseInt(next), null);
                } else {
                    Savable obj = jsonImporter.readObject(mapObject.getJSONObject(next));
                    map.put(Integer.parseInt(next), obj);
                }
            }
            
            return map;
        } catch (IOException | NumberFormatException | JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public FloatBuffer readFloatBuffer(String name, FloatBuffer defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
           return defVal;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            float[] nbuff = new float[array.length()];
            for (int i = 0; i < array.length(); i++) {
                nbuff[i] = array.getFloat(i);
            }
            
            FloatBuffer tmp = BufferUtils.createFloatBuffer(nbuff.length);
            for (final float vn : nbuff) {
                tmp.put(vn);
            }
            tmp.flip();
            return tmp;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public IntBuffer readIntBuffer(String name, IntBuffer defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
           return defVal;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            int[] nbuff = new int[array.length()];
            for (int i = 0; i < array.length(); i++) {
                nbuff[i] = array.getInt(i);
            }
            
            IntBuffer tmp = BufferUtils.createIntBuffer(nbuff.length);
            for (final int vn : nbuff) {
                tmp.put(vn);
            }
            tmp.flip();
            return tmp;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ByteBuffer readByteBuffer(String name, ByteBuffer defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
           return defVal;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            byte[] nbuff = new byte[array.length()];
            for (int i = 0; i < array.length(); i++) {
                nbuff[i] = array.getNumber(i).byteValue();
            }
            
            ByteBuffer tmp = BufferUtils.createByteBuffer(nbuff.length);
            for (final byte vn : nbuff) {
                tmp.put(vn);
            }
            tmp.flip();
            return tmp;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public ShortBuffer readShortBuffer(String name, ShortBuffer defVal) throws IOException {
        jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
           return defVal;
        }
        try {
            JSONArray array = jsonSavable.getJSONArray(name);
            short[] nbuff = new short[array.length()];
            for (int i = 0; i < array.length(); i++) {
                nbuff[i] = array.getNumber(i).shortValue();
            }
            
            ShortBuffer tmp = BufferUtils.createShortBuffer(nbuff.length);
            for (final short vn : nbuff) {
                tmp.put(vn);
            }
            tmp.flip();
            return tmp;
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T extends Enum<T>> T readEnum(String name, Class<T> enumType, T defVal) throws IOException {
       jsonSerializer.validName(name);
        if (!jsonSavable.has(name)) {
           return defVal;
       }
       if (jsonSavable.isNull(name)) {
           return defVal;
       }
        try {
            return jsonSavable.getEnum(enumType, name);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }    
}
