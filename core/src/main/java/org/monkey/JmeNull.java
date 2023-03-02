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

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;

import java.io.IOException;

/**
 * Un <code>JmeNull</code> es una implementacion de Savable sin datos.
 * <p>
 * Es equivalente al valor que Java llamada null o indefinido, se puede
 * utilizar para rellenar espacios nulos en los objetos <code>JmeProperties</code> y
 * <code>JmeArray</code>
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public final class JmeNull implements Savable {
    
    /**
     * A veces es mas conveniente y menos ambiguo tener un
     * Objeto <code>NULL</code> que usar el valor <code>null</code> de Java.
     * <code>JmeNull.NULL.equals(null)</code> devuelve <code>true</code>.
     * <code>JmeNull.NULL.toString()</code> devuelve <code>"null"</code>.
     */
    public static final Object NULL = new JmeNull();

    /**
     * Constructor predeterminado de la clase {@code JmeNull}.
     * <p><b>NOTA: No se puede crear una instancia, utilize el objeto</b>
     * <code>JmeNull.NULL</code>
     */
    private JmeNull() {
    }
    
    /**
     * Solo se pretende que haya una sola instancia del objeto {@code JmeNULL},
     * por lo que el método de clonacion se devuelve a si mismo.
     * 
     * @return JmeNULL.
     */
    @Override
    protected final Object clone() {
        return this;
    }

    /** 
     * Un objeto {@code NULL} es igual al valor null y a si mismo. 
     * 
     * @param object
     *          Un objeto para probar la nulidad. 
     * @return {@code true} si el parametro object es el objeto 
     *          {@code JSONObject.NULL} o {@code null}. 
     */
    @Override
    @SuppressWarnings("lgtm[java/unchecked-cast-in-equals]")
    public boolean equals(Object object) {
        return object == null || object == this;
    }
    
    @Override public void write(JmeExporter ex) throws IOException { }
    @Override public void read(JmeImporter im) throws IOException { }
    
    /**
     * Un objeto Null es igual al valor null y a si mismo.
     * @return siempre devuelve 0.
     */
    @Override
    public int hashCode() {
        return 0;
    }
    
    /**
     * Obtenga el valor string "null".
     * @return La cadena "null".
     */
    @Override
    public String toString() {
        return "null";
    }
}
