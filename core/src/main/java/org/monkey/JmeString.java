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

/**
 * La interfaz <code>JmeString</code> permite un <code>toJmeString()</code>
 * metodo para que una clase pueda cambiar el comportamiento de
 * <code>JmeProperties.toString()</code>, <code>JmeArray.toString()</code>.
 * <p>
 * Se utilizara el metodo <code>toJmeString</code> en lugar del comportamiento predeterminado
 * de usar el metodo <code>toString()</code> del objeto y citar el resultado.
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public interface JmeString {
    
    /**
     * El metodo <code>toJmeString</code> permite que una clase produzca su propio
     * cadena de caracteres representacion.
     *
     * @return Un texto que represente la clase.
     */
    public String toJmeString();
}
