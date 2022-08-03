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

import com.jme3.util.PrimitiveAllocator;
import com.jme3.util.ReflectionAllocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  La fabrica de asignadores de buffer. 
 * @author jme3
 * @version 1.0.0
 */
final class JmeBufferAllocatorFactory {
    
    public static final String PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION = "com.jme3.BufferAllocatorImplementation";
    
    private static final Logger LOGGER = Logger.getLogger(JmeBufferAllocatorFactory.class.getName());

    /**
     * Un constructor privado para inhibir la instanciación de esta clase.
     */
    protected JmeBufferAllocatorFactory() {
    }

    protected static BufferAllocator create() {
        final String className = System.getProperty(PROPERTY_BUFFER_ALLOCATOR_IMPLEMENTATION,
                                                        ReflectionAllocator.class.getName());
        try {
            Constructor<?> ctr = findNoArgConstructor(className);
            return (BufferAllocator) ctr.newInstance();
        } catch (final ClassNotFoundException | IllegalAccessException | IllegalArgumentException |
                       InstantiationException | InvocationTargetException e) {
            LOGGER.log(Level.WARNING, "Unable to access {0}", className);
            return new PrimitiveAllocator();
        }
    }
    
    /**
     * Use la reflexion para obtener acceso al constructor sin argumentos del nombre
     * clase.
     *
     * @return El constructor preexistente (no nulo)
     */
    private static Constructor findNoArgConstructor(String className)
            throws ClassNotFoundException, InstantiationException {
        Class clazz = Class.forName(className);
        Constructor result;
        try {
            result = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new InstantiationException(
                    "Loading requires a no-arg constructor, but class "
                    + className + " lacks one.");
        }

        return result;
    }
}
