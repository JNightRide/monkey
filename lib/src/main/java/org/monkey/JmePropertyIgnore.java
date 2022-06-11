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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *  Use esta anotacion en un método getter para anular el nombre del Bean
 * analizador para Bean -&gt; Asignación de JmePropereties. Si esta anotación es
 * presente en cualquier nivel en la jerarquía de clases, entonces el método
 * no sera serializado desde el bean al JmeProperties.
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface JmePropertyIgnore {
    
}
