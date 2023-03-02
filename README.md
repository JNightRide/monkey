# Monkey
 Monkey es un conjunto de clases que se pueden usar para guardar datos de un juego echo en jMonkeyEngine3.3.
Es una biblioteca basada en la API de json(https://github.com/stleary/JSON-java).

Permite al usuario guardar datos en forma de propiedades, también es posible guardar listas de datos,
siempre y cuando implementen la interfaz **Savable** u objetos primitivos (int, short, char, string, etc.), con la
nueva version 1.6.0 se a incluido un nuevo modulo **monkey-lemur** que se encarga de proprocionar clase se puedes
utilizan junto con lemur(https://github.com/jMonkeyEngine-Contributions/Lemur).

**Algunas características que proporciona esta biblioteca:**
1. JmeProperties 
2. JmeArray
3. JmeBufferUtils
4. JmeNull
5. ButtonGroup
6. FreeLayout
7. Layout
8. Tipo de datos dinamico (int, float, short, byte, etc.)

## Módulo - monkey-core:
Este módulo se encarga de proporcionar objetos/clases para guardar datos
de nuestros juego echos con jMonkeyEngine3.

```java
package org.monkey.examples;

import org.monkey.JmeProperties;

/**
 * Como utilizar el módulo <code>monkey-core</code>.
 * @version 1.0.0-test
 */
public class MonkeyCore {
    public static void main(String[] args) {
        JmeProperties jp = new JmeProperties();
        jp.put("name", "Monkey-Core");
        
        System.out.println(jp.toString(1));
    }
}
```

## Version v1.6.5
Con esta nueva version se puede exportar e importar nuestros objeto **Savable**
en formato JSON(https://github.com/stleary/JSON-java) con los siguientes objetos.

1. JsonImporter
2. JsonExporter

**NOTA:** El módulo **monkey-lemur** se a traslado a un nuevo repositorio
si dese sequir utilizando vea (https://github.com/JNightRide/monkey-lemur/tree/master)


Y eso es todo. Tu juego ya puede guadar sus datos en disco y/o contar con una intrefaz escalable.
