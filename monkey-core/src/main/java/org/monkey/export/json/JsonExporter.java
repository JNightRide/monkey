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

import com.jme3.export.FormatVersion;
import com.jme3.export.JmeExporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.export.SavableClassUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.IdentityHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * (non-JavaDoc).
 * @since 1.6.5
 */
public class JsonExporter implements JmeExporter {

    private final Map<Savable, JsonOutputCapsule> classes 
                            = new IdentityHashMap<>();
    
    private final JsonSerializer serializer 
                    = new JsonSerializer();

    public JsonExporter() {
    }
    
    private JsonOutputCapsule createCapsule(Class<? extends Savable> clazz, boolean isRoot) throws IOException {
        JSONObject objectClazz = new JSONObject();
        
        // jME3 NEW: Append version number(s)
        final int[] versions = SavableClassUtil.getSavableVersions(clazz);
        JSONArray verArrray  = new JSONArray();
        
        for (final int verInt : versions) {
            verArrray.put(verInt);
        }
        
        objectClazz.put(JsonSerializer.SAVABLE_VERSIONS, verArrray);
        objectClazz.put(JsonSerializer.SAVABLE_CLAZZ, clazz.getName());
        
        if (isRoot) {
            objectClazz.put(JsonSerializer.FORMAT_VERSION, FormatVersion.VERSION);
            objectClazz.put(JsonSerializer.SIGNATURE, FormatVersion.SIGNATURE);
        }
        
        return new JsonOutputCapsule(this, objectClazz);
    }

    protected JsonSerializer getSerializer() {
        return serializer;
    }
   
    protected JSONObject processJsonCapsule(Savable obj) throws IOException {
        return this.processJsonCapsule(obj, false);
    }
    
    private JSONObject processJsonCapsule(Savable obj, boolean b) throws IOException {
        Class<? extends Savable> clazz = obj.getClass();
        JsonOutputCapsule joc = classes.get(obj);
        
        if (joc != null) {            
            JSONObject savable = new JSONObject();
            
            String clazzId = joc.getJsonSavable().optString(JsonSerializer.SAVABLE_ID, null);
            if (clazzId == null) {
                
                clazzId = clazz.getName() + '@' + obj.hashCode();
                joc.getJsonSavable().put(JsonSerializer.SAVABLE_REF, clazzId);
            }
            
            savable.put(JsonSerializer.SAVABLE_ID, clazzId);
            return savable;
        }
        
        joc = createCapsule(clazz, b);
        classes.put(obj, joc);
        
        obj.write(this);
        return joc.getJsonSavable();
    }
    
    @Override
    public void save(Savable object, OutputStream f) throws IOException {
        save(object, new OutputStreamWriter(f));
    }
    
    private void save(Savable object, Writer writer) throws IOException {
        try (writer) {
            if (object == null)
                throw new IOException("Savable is Null.");
            
            JSONObject rootObject = processJsonCapsule(object, true);
            rootObject.write(writer, serializer.getIndentFactor(), serializer.getIndent());
        }
    }

    @Override
     public void save(Savable object, File f) throws IOException {
        File parentDirectory = f.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(f);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            save(object, bos);
        }
    }

    @Override
    public OutputCapsule getCapsule(Savable object) {
        return classes.get(object);
    }

    public void setIndentFactor(int indentFactor) {
        serializer.setIndentFactor(indentFactor);
    }

    public void setIndent(int indent) {
        serializer.setIndent(indent);
    }
    
    public static JsonExporter getInstance() {
        return new JsonExporter();
    }
}
