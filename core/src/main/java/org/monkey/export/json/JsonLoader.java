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
import com.jme3.asset.AssetLoader;

import java.io.IOException;

/**
 * Clase encargado de cargar los ficherto {@code JSON} con los datos
 * de un {@code Savable}.
 * 
 * @author wil
 * @since 1.6.5
 */
public class JsonLoader implements AssetLoader {
    
    /**
     * Extenciones del ficheros json.
     */
    public static final String[] EXTENCION = {
        "JSON", "json"
    };
    
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        JsonImporter importer = JsonImporter.getInstance();
        return importer.load(assetInfo);
    }
}
