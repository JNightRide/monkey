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

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.export.FormatVersion;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.export.SavableClassUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * (non-JavaDoc).
 * @since 1.6.5
 */
public class JsonImporter implements JmeImporter {

    private static final Logger LOG = Logger.getLogger(JsonImporter.class.getName());

    private final Map<String, Savable> savables
            = new IdentityHashMap<>();

    private final Map<Savable, JsonInputCapsule> classes
            = new IdentityHashMap<>();
    
    private final JsonSerializer jsonSerializer 
            = new JsonSerializer();
    
    private AssetManager assetManager;    
    private int formatVersion = 0;

    public JsonImporter() {
    }
    
    private JsonInputCapsule createCapsule(JSONObject obj, boolean isRoot) throws IOException, InstantiationException,
                                                                                    InvocationTargetException, NoSuchMethodException, 
                                                                                    IllegalAccessException, ClassNotFoundException {
        try {
            String clazzname = obj.getString(JsonSerializer.SAVABLE_CLAZZ);

            JSONArray arrayVer = obj.getJSONArray(JsonSerializer.SAVABLE_VERSIONS);
            int[] classHierarchyVersions = new int[arrayVer.length()];

            for (int i = 0; i < arrayVer.length(); i++) {
                classHierarchyVersions[i] = arrayVer.getInt(i);
            }

            if (isRoot) {
                int sig = obj.getInt(JsonSerializer.SIGNATURE);
                if (sig == FormatVersion.SIGNATURE) {
                    formatVersion = obj.getInt(JsonSerializer.FORMAT_VERSION);
                    
                    if (formatVersion > FormatVersion.VERSION){
                        throw new IOException("The binary file is of newer version than expected! " + 
                                              formatVersion + " > " + FormatVersion.VERSION);
                    }
                } else {
                    formatVersion = 0;
                }
            }
            
            Savable savable;
            if (assetManager != null) {
                savable = SavableClassUtil.fromName(clazzname, assetManager.getClassLoaders());
            } else {
                savable = SavableClassUtil.fromName(clazzname);
            }
            return new JsonInputCapsule(this, obj, savable, classHierarchyVersions);
        } catch (JSONException e) {
            throw new IOException(e.getMessage());
        }
    }

    protected Savable readObject(JSONObject obj) throws IOException {
        return this.readObject(obj, false);
    }
    
    private Savable readObject(JSONObject obj, boolean b) throws IOException {
        final String id = obj.optString(JsonSerializer.SAVABLE_ID, null);        
        Savable jmeObject = null;
        
        if (id != null) {
            Set<String> keys = this.savables.keySet();
            
            for (String keyNext : keys) {
                if (keyNext.equals(id)) {
                    jmeObject = this.savables.get(keyNext);
                    break;
                }
            }
        }
        if (jmeObject != null) {
            return jmeObject;
        }
        
        try {
            JsonInputCapsule jic = createCapsule(obj, b);
            JSONObject objSavable = jic.getJsonSavable();
            
            Savable mySavable = jic.getJmeSavable();
            if (objSavable.has(JsonSerializer.SAVABLE_REF)) {
                String reId = objSavable.getString(JsonSerializer.SAVABLE_REF);
                
                this.savables.put(reId, mySavable);
            }
            
            this.classes.put(mySavable, jic);
            
            mySavable.read(this);
            
            this.classes.remove(mySavable);
            return mySavable;
        } catch (IOException | ClassNotFoundException | IllegalAccessException | 
                InstantiationException | NoSuchMethodException | InvocationTargetException | JSONException e) {
            throw new IOException(e);
        }
    }

    protected JsonSerializer getJsonSerializer() {
        return jsonSerializer;
    }
    
    public static JsonImporter getInstance() {
        return new JsonImporter();
    }

    @Override
    public InputCapsule getCapsule(Savable id) {
        return this.classes.get(id);
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public int getFormatVersion() {
        return formatVersion;
    }

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        assetManager = assetInfo.getManager();

        InputStream is = null;
        try {
            is = assetInfo.openStream();
            Savable s = load(is);
            
            return s;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "An error occurred while loading jME binary object", ex);
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException ex) {}
            }
        }
        return null;
    }
    
    public Savable load(InputStream is) throws IOException {
        Savable rVal;
        try (InputStreamReader reader = new InputStreamReader(is)) {
            rVal = load(reader);
        }
        return rVal;
    }
    
    public Savable load(Reader reader) throws IOException {
        Savable rVal;
        try (reader) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObj = new JSONObject(tokener);
            
            rVal = readObject(jsonObj, true);
        }
        return rVal;
    }

    public Savable load(URL f) throws IOException {
        Savable rVal;
        try (InputStream is = f.openStream()) {
            rVal = load(is);
        }
        return rVal;
    }
    
    public Savable load(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f)) {
            return load(fis);
        }
    }

    public Savable load(byte[] data) throws IOException {
        Savable rVal;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            rVal = load(bais);
        }
        return rVal;
    }
}
