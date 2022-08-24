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

## Módulo  - monkey-lemur:
Ahora veremos el módulo **monkey-lemur**, con el podemos aprovechar la clase **FreeLayout** que proporciona
un diseño libre, es decir que podemos generar GUI escalables a cualquier resolución de pantalla..

**NOTA:** Para que los componentes sean escalables, es importante que implementemos la clase **CanvasLayer** para
que pueda calcular y adaptar los diferentes componentes de la interfaz del usuario.


```java
package org.monkey.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

import org.monkey.lemur.CanvasLayer;
import org.monkey.lemur.renderer.FreeControl;
import org.monkey.lemur.renderer.Layout;

/**
 * Clase que ejemplifica como generar una interfaz escalable
 * utilizando la API <code>Lemur</code>.
 * @version 1.0.0-test
 */
public class MonkeyLemur extends SimpleApplication {
    
    public static void main(String[] args) {
        MonkeyLemur app = new MonkeyLemur();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Inicializar el acceso global para que el predeterminado
        // los componentes pueden encontrar lo que necesitan.
        GuiGlobals.initialize(this);
            
        // Carga el estilo 'glass'
        BaseStyles.loadGlassStyle();
            
        // Establecer 'glass' como el estilo predeterminado cuando no se especifica
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        
        // Implementamos el contenedor raiz.
        CanvasLayer canvasLayer = new CanvasLayer(this);
        canvasLayer.setPreferredSize(new Vector3f(1024.0F, 576.0F, 0.0F));
        
        // GUI escalables.
        Container rootPane = canvasLayer.addChild(new Container(), true, Layout.Center);
        rootPane.setPreferredSize(new Vector3f(300.0F, 300.0F, 0.0F));
        
        Button button = rootPane.addChild(new Button("GButton"), true, Layout.CenterTop);
        button.setPreferredSize(new Vector3f(200.0F, 100.0F, 0.0F));
        button.getControl(FreeControl.class).setFontSize(14);
        button.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                System.out.println("onClick()");
            }});
    }
}
```

Y eso es todo. Tu juego ya puede guadar sus datos en disco y/o contar con una intrefaz escalable.
