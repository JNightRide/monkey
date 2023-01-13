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
package org.monkey.lemur.renderer;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;
import java.util.Objects;

/**
 * Un objeto <code>Constraints</code> es el encargado de almacenar los parametro
 * que se pasar al agregar un nodo hijo.
 */
public final 
class Constraints implements Cloneable, Savable {
    
    /**
     * Objeto boolean encargado de guardar un valor logico que se pasa
     * por parametro.
     */
    private Boolean bool;
    
    /**
     * Tipo enumerado del layout, es decir la posicion del objeto al
     * agregarse a su respectivo nodo padre.
     */
    private Layout layout;

    /**
     * Constructor predeterminado.
     */
    public Constraints() {
        this(Boolean.FALSE, Layout.Center);
    }
    
    /**
     * Genera un objeto de la clase <code>Constraints</code> que contendran
     * los datos requeridos para que su contro funcione.
     * 
     * @param bool Un valor logico.
     * @param layout Un objeto enumerado.
     */
    public Constraints(Boolean bool, Layout layout) {
        if ( bool == null )
            throw new NullPointerException("Boolean.");
        
        if ( layout == null )
            throw new NullPointerException("Layout.");
        
        this.bool   = bool;
        this.layout = layout;
    }

    // (non-JavaDoc)
    @Override
    public Constraints clone() {
        try {
            Constraints clon = (Constraints) 
                                super.clone();
            
            clon.layout = layout;
            clon.bool   = bool;
            
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    // (non-JavaDoc)
    @Override
    public String toString() {
        return "Constraints{" + "bool=" + bool + ", layout=" + layout + '}';
    }
    
    /*
        Getters
    */
    public Boolean getBool() { return bool; }
    public Layout getLayout() { return layout; }

    /*
        Setters.
    */
    public void setBool(boolean bool) { this.bool = bool; }
    public void setLayout(Layout layout) { this.layout = layout; }

    // (non-JavaDoc)
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(bool, "bool", Boolean.FALSE);
        out.write(layout, "layout", Layout.Center);
    }

    // (non-JavaDoc)
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        bool = in.readBoolean("bool", Boolean.FALSE);
        layout = in.readEnum("layout", Layout.class, Layout.Center);
    }

    // (non-JavaDoc)
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.bool);
        hash = 97 * hash + Objects.hashCode(this.layout);
        return hash;
    }

    // (non-JavaDoc)
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
        final Constraints other = (Constraints) obj;
        if (!Objects.equals(this.bool, other.bool)) {
            return false;
        }
        return this.layout == other.layout;
    }
}
