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

import java.io.IOException;

/**
 * Clase encargado de gestionar las palabras clave o reservadas que itiliza
 * el codificado({@link JsonExporter}) y/o decodificado({@link JsonImporter}).
 * <p>
 * Tambien es el encaergado de establecer el formato del fichero {@code json}
 * al exportarlo.</p>
 * 
 * @author wil
 * @version 1.0.0
 * 
 * @since 1.6.5
 */
public final 
class JsonSerializer {
    
    /**
     * Palabra clave que se utiliza para guardad una {@code id} de un objeto
     * {@code Savable} que se a guardado anteriormente.
     * <p>
     * Es decir, si dicho objeto se a guardar en otros objeto y este a sido
     * codificado previamente solo se genera un {@code id} con la referencia
     * del objeto.</p>
     * <p>
     * Si no se haces una referencia, se estaria guardado el mismo objeto
     * varias veces y al importarlo dicho objeto serian independiadntes,
     * basicamente es un <b>error</b>.</p>
     */
    public static final String SAVABLE_ID         = "@jme3:id";
    
    /**
     * Palabra clave encargado de identificar un objeto {@code Savable} que 
     * se han referido a el varias veces en otros objetos guardables.
     * <p>
     * Con el fin de que cuando de importe, solo se mande a llamar dicho
     * objeto {@code Savable} en vez de crera uno nuevo que se ria un error.</p>
     */
    public static final String SAVABLE_REF        = "@jme3:reference_id";
    
    /**
     * Palabra clave que se utiliza como identificado o llave al guardar
     * objetos de la clase {@code Map<?,?>} en donde sus identificadores son
     * {@code Savable} como sus datos.
     */
    public static final String SAVABLE_KEY        = "@jme3:key";
    
    /**
     * Palabra clave que se utiliza como identificado o llave al guardar
     * objetos de la clase {@code Map<?,?>} en donde sus identificadores son
     * {@code Savable} como sus datos.
     */
    public static final String SAVABLE_VAL        = "@jme3:val";
    
    /**
     * Todo {@code Savable} detener una clase, dicho nombre de clase se gaudar
     * bajo {@code @jme3:clazz} .
     */
    public static final String SAVABLE_CLAZZ      = "@jme3:clazz";
    
    /**
     * Palabra clave en donde se se aloja las versiones del objeto 
     * {@code Savable} al convertilo en un objeto JSON.
     */
    public static final String SAVABLE_VERSIONS   = "@jme3:versions";
    
    /**  Palabra clave para el formato de version. */
    public static final String FORMAT_VERSION     = "@jme3:format_version";
    
    /** Palabra calve para la asignatura. */
    public static final String SIGNATURE          = "@jme3:signature";
    
    /**
     * Un {@code indentFactor} es el espacio que utiliza el formato
     * al dar un nuevo salto de lineas.
     * 
     * @see JsonSerializer#getIndentFactor() 
     * @see JsonSerializer#setIndentFactor(int) 
     */
    int indentFactor;
    
    /**
     * Un {@code indent} es el espacio qur se utiliza el formato al dar un
     * margen izquierdo.
     * 
     * @see JsonSerializer#getIndent() 
     * @see JsonSerializer#setIndent(int) 
     */
    int indent;

    /**
     * Constructor de la clase.
     */
    JsonSerializer() {
    }

    /*
        Getters
    */
    public int getIndentFactor() { return indentFactor; }
    public int getIndent() { return indent; }

    /*
        Setters.
    */
    public void setIndentFactor(int indentFactor) { this.indentFactor = indentFactor; }
    public void setIndent(int indent) { this.indent = indent; }    
    
    /**
     * Metodo encargado de validar los nombres o palabras claves de cada
     * atributo o propiedade que el objeto {@code Savable} intente guardar.
     * 
     * @param name Clave de la propiedade a validar.
     * @return Un valor boolean.
     * 
     * @throws IOException Excpecion si el nombre que se este intentado utilizar
     *                      es reservado por los codificadores y/o decodificadores.
     */
    public boolean validName(String name) throws IOException {
        if (name == null || name.isEmpty()) {
            throw new IOException("Name|Key is null");
        }
        switch (name) {
            case SAVABLE_ID :   case SAVABLE_KEY:      case SAVABLE_VAL:
            case SAVABLE_CLAZZ: case SAVABLE_VERSIONS: case FORMAT_VERSION:
            case SIGNATURE:
                throw new IOException("Name|Key reserved by the export and import tool.");
            default:
                return true;
        }
    }
}
