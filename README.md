# Monkey
 Monkey es un conjunto de clases que se pueden usar para guardar datos de un juego echo en jMonkeyEngine3.3.
Es una biblioteca basada en la API de json(https://github.com/stleary/JSON-java).

Permite al usuario guardar datos en forma de propiedades, también es posible guardar listas de datos,
siempre y cuando implementen la interfaz **Savable** u objetos primitivos (int, short, char, string, etc.).

**Algunas características que proporciona esta biblioteca:**
1. JmeProperties 
2. JmeArray
3. JmeBufferUtils
4. JmeNull
5. Tipo de datos dinamico (int, float, short, byte, etc.)

## Ejemplo 1:
En el Ejemplo 1, le mostraré cómo crear un objeto de propiedades y/o lista.
También verá como agregar los datos y/o obtenerlos.

```java
package org.monkey.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.util.BufferUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.monkey.JmeArray;
import org.monkey.JmeProperties;

/**
 * Este ejemplo le mostrará cómo crear un objeto de propiedades simple. Asi
 * cómo agregar los datos en ella y/o ebtenerlos.
 * 
 * @author wil
 */
public class Example1 extends SimpleApplication {
    
    private JmeProperties jp;   // Objeto de propiedades.
    private JmeArray ja;        // Objeto lista.
    
    /**
     * El método principal para esta aplicación Java cuando la ejecutamos.
     * 
     * @param args 
     *          Argumentos de la linea de comandos.
     */
    public static void main(String[] args) {
        Example1 app = new Example1();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        // creamos una nueva instancia.
        jp = new JmeProperties();
        ja = new JmeArray();
        
        // para agregar datos el objeto de propiedades,
        // basta con llavar el metodo 'put(key, value)'.
        jp.put("valInt",    10);    // un valor int
        jp.put("valFloat",  05.f);  // un valor float
        jp.put("valDouble", 0.1d);  // un valor double
        
        jp.put("valBigInt",     new BigInteger("10089638"));    // un entero grande.
        jp.put("valBigDecimal", new BigDecimal("1963.256"));    // un decimal grande.
        
        // para obtener los datos, hay 2 maneras.
        //
        // La primera es obtener de manera obligatoria
        // el dato deseadoo.
        int valInt = jp.getInt("valInt");
        
        System.out.println("[ JmeProperties ]: ");
        System.out.println("int: " + valInt);
        
        // La segunda manera, es tratar de convertir el
        // valor en el datos deseado. Si no se puede 
        // convertilo que devuelva un valor predeterminado 'NULL'
        Number number = jp.optNumber("valBigDecimal", null);
        
        System.out.println("number: " + number +"\n");
        
        // ---------------------------------------------
        // para agregar datos el objeto de propiedades,
        // basta con llavar el metodo 'put(value)'.
        ja.put(false);  // un valor boolean
        ja.put('Z');    // un valor char
        ja.put(BufferUtils.createIntBuffer(1, 2, 3));   // un buffer como valor.
        ja.put(new ColorRGBA(1.0F, 1.0F, 1.0F, 1.0F));  // un savable como valor.
        
        
        // para obtener los datos, hay 2 maneras.
        //
        // La primera es obtener de manera obligatoria
        // el dato deseadoo.
        boolean bool = ja.getBoolean(0);
        
        System.out.println("[ JmeArray ]: ");
        System.out.println("bool: " + bool);
        
        // La segunda manera, es tratar de convertir el
        // valor en el datos deseado. Si no se puede 
        // convertilo que devuelva un valor predeterminado 'ColorRGBA.Black'
        ColorRGBA col = ja.optSavable(3, ColorRGBA.Black);
        
        System.out.println("color: " + col);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: añadir código de actualización
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: agregar código de procesamiento
    }
}

```

## Ejemplo 2:
Ahora veremos cómo se pueden gurdar/exportar y cargar/importar nuestros datos
de forma binaria para que usuario no pueda acceder a ella.

**IMPORTANTE:** Ten encuenta que solo podras usar una de las formas de exportación e
importación, ya que guardan los datos de manera diferentes. Si utilizas la función 
de 'SaveGame' como exportación, tendras que utilizar el mismo metodo para la
importación. Lo mismo se aplica para BinaryExporter/BinaryImporter.

### Exporter > Forma 1: Utilizando BinaryExporte
Ya creado tu JmeProperties o JmeArray.
```java
        try {
            // Puedes exportar tu 'JmeProperties' y/o 'JmeArray' con
            // 'BinaryExporter'.
            BinaryExporter exporter = BinaryExporter.getInstance();            
            exporter.save(jp, new File("pathname"));            
        } catch (IOException e) {
            // Si sucede un error.
            throw new InternalError(e);
        }
```

### Exporter > Forma 2: Utilizando SaveGame(jme3tools.savegame.SaveGame)
Otra forma de guardar tus datos, es utilizando 'Savegame' que te
proporcionat jme3 en sus bibliotecas.
```java
        // Puedes guardar tus datos utilizando 'SaveGame'.
        //
        // Ten encuenta que se generara un directorio '.jme3' de
        // manera automatica, donde podras encontrar tus datos guardados.
        SaveGame.saveGame("pathdir", "pathname", ja, JmeSystem.StorageFolderType.External);      
```

### Importer > Forma 1: Utilizando BinaryImporter
Para cargar los datos del juego, puedes utilizar un 'BinaryImporter', dependiendo
del objeto que deseas cargar(JmeProperties, Jme Array).
```java
        try {
            // Puedes importar tu 'JmeProperties' y/o 'JmeArray' con
            // 'BinaryImporter'.
            BinaryImporter importer = BinaryImporter.getInstance();
            
            // [ JmeProperties ]
            JmeProperties jp = (JmeProperties) 
                                importer.load(new File("pathname"));
            
            // [ Jme Array ]
            JmeArray ja = (JmeArray)
                            importer.load(new File("pathname"));
        } catch (IOException e) {
            // Si sucede un error.
            throw new InternalError(e);
        }
```

### Importer > Forma 2: Utilizando SaveGame(jme3tools.savegame.SaveGame)
Si deseas utilizar 'SaveGame' para cargar tus datos.
```java
        // Puedes cargar tus datos utilizando 'SaveGame'.
        //
        // [ JmeProperties ]
        JmeProperties jp = (JmeProperties) 
                            SaveGame.loadGame("pathdir", "pathname", assetManager, JmeSystem.StorageFolderType.External);
        
        // [ Jme Array]
        JmeArray ja = (JmeArray)
                        SaveGame.loadGame("pathdir", "pathname", assetManager, JmeSystem.StorageFolderType.External);     
```

## Ejemplo 3:
Este ejemplo le mostrará cómo agregar diferentes tipos de datos en los objetos de propiedades como en listas.
En caso de que husted quiere agregar su propio objeto personalizado de propiedades, implemente la interfaz
**Savable** que jme3 le proporciona, tambien es posible anidar las listas u objetos de propiedades. 

```java
package org.monkey.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.renderer.RenderManager;

import java.io.IOException;

import org.monkey.JmeArray;
import org.monkey.JmeBufferUtils;
import org.monkey.JmeProperties;

/**
 * En este ejemplo, se mostrara como anidar componentes e incluso
 * crear nuestro propio objeto de configuracion.
 * 
 * @author wil
 */
public class Example3 extends SimpleApplication {
    
    /**
     * Suponga que tenemos una clase 'Settings', que es nuestro
     * objeto de configuración personalizado. Observe que implementamos
     * la interfaz {@code Savable} de la biblioteca <code>jme3</code>
     */
    static final class Settings implements Savable {
        
        private JmeProperties map;  // [ JmeProperties ]
        private boolean music;      // [ Boolean ]
        private boolean sound;      // [ Boolean ]

        public Settings() {
            this.map = new JmeProperties();
        }
        
        @Override
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule out = ex.getCapsule(this);
            
            // Exportamos nuestros datos:
            // [ JmeProperties ]
            out.write(map, "map", null);
            
            // [ Boolean ]
            out.write(music, "music", Boolean.FALSE);
            out.write(sound, "sound", Boolean.FALSE);
        }

        @Override
        public void read(JmeImporter im) throws IOException {
            InputCapsule in = im.getCapsule(this);
            
            // Reconstruimos nuestro objeto:
            // [ JmeProperties ]
            map = (JmeProperties) 
                   in.readSavable("map", map);
            
            // [ Boolean ]
            music = in.readBoolean("music", Boolean.FALSE);
            sound = in.readBoolean("sound", Boolean.FALSE);
        }
    }
    
    private JmeProperties jp;   // Objeto de propiedades.
    private JmeArray ja;        // Objeto lista.
    
    /**
     * El método principal para esta aplicación Java cuando la ejecutamos.
     * 
     * @param args 
     *          Argumentos de la linea de comandos.
     */
    public static void main(String[] args) {
        Example3 app = new Example3();
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        // genereamos nuestros objetos
        jp = new JmeProperties();
        ja = new JmeArray();
        
        // craemo nuestro objeto de configuración.
        Settings conf = new Settings();
        
        // establecemos los datos de cofiguración
        // [ Boobean ]
        conf.music = false;
        conf.sound = true;
        
        // [ JmeProperties ]
        conf.map.put("UserName", "Monkey");
        conf.map.put("Group", "org.monkey");
        
        
        // Ahora anidamos nuestros datos:
        jp.put("Settings", conf);                   // objeto configuracion.
        jp.put("UserData", new JmeProperties() {{   // objeto de propiedad.
            put("name",    "Monkey");
            put("version", "1.0.0");
        }});
        
        jp.put("Array", ja.put(new JmeProperties() {{   // [ JmeProperties ]
            put("item1", new JmeArray(new Object[] {    // [ JmeArray ]
                1, 2, 4, 'a', 'v', 'c', 0.5f, 0.5d
            }).put(false)                   // boolean
              .put(100L)                    // long
              .put(new JmeArray() {{        // [ JmeArray ]
                  put("Item - 1");      // String
                  put("item - 2 ");      // String
              }}));
        }}));
        
        jp.put("myaudio", AudioData.DataType.Buffer);                       // Un valor [ enum ]
        jp.put("buffer", JmeBufferUtils.createLongBuffer(1L, 2L, 4L));      // Un buffer
        
        // Salida >>
        System.out.println("[ OUT ] >> \n" + jp.toString(1));
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: añadir código de actualización
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: agregar código de procesamiento
    }
}

```
Piense en ello como un objeto json.


## Ejemplo 4: 
En el ejemplo 4, veremos como generar un JmeProperties utilizando anotaciones
en los metodos de nuestros objetos.

**Objeto** que implementa las anotaciones.

```java
package org.monkey.examples;

import org.monkey.JmePropertyIgnore;
import org.monkey.JmePropertyName;

/**
 * En esta clase se ejemplificara, como utilizar las notaciones
 * {@link org.monkey.JmePropertyIgnore} y {@link org.monkey.JmePropertyName} para
 * generar un bjeto de propiedad sin la necesidad de implementar la interfas Savable
 *
 * @author wil
 */
public class Example4 {
    
    // como decir los siguiente: 
    //
    // put('org.monkey.name', 'Monkey')
    @JmePropertyName(value = "org.monkey.name")
    public String getName() {
        return "Monkey";
    }
    
    // como decir los siguiente: 
    //
    // put('org.monkey.version', 1)
    @JmePropertyName(value = "org.monkey.version")
    public int getVersion() {
        return 1;
    }
    
    // no se toma encuenta, dado que tiene la
    // notacion 'JmePropertyIgnore'
    @Override
    @JmePropertyIgnore
    public String toString() {
        return "Monkey v1.5.0";
    }
    
    
    // como decir los siguiente:
    // put('music', 1)
    public boolean isMusic() {
        return false;
    }
    
    // no se toma encuenta, dado que tiene la
    // notacion 'JmePropertyIgnore'
    @JmePropertyIgnore
    public boolean isDeprecated() {
        return false;
    }
}

```

## Ejemplo 5: 
En este ejemplo 5, veremos como incorporar un objeto que no implemente la interfaz **Savable**
que jme3 no proporciona, sino que utilizamos las anotaciones 'JmePropertyName' y/o 'JmePropertyIgnore'.

Ten encuenta que al intanciar un objeto de propiedad(JmeProperties) dentro se su constructor, es donde
le pasaremos el objeto construido.

```java
        // genereamos nuestros objetos
        JmeProperties jp = new JmeProperties(new Example4());        
        
        // Salida por consola.
        System.out.println(jp.toString(1));  
```

Y eso es todo. Tu juego ya puede guadar sus datos en disco.
