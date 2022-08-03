package org.monkey.lemur;

import com.jme3.app.Application;
import com.jme3.system.AppSettings;

import com.simsilica.lemur.Container;
import com.simsilica.lemur.core.GuiComponent;
import com.simsilica.lemur.core.GuiLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.monkey.lemur.renderer.FreeLayout;

/**
 * Una <code>CanvasLayer</code> es el contenedor principal donde se agregaran los
 * componentes de la interfaz de usuario.
 * 
 * @author wil
 * @version 1.0.0
 * 
 * @since 1.0.0
 */
public class CanvasLayer extends Container {
    /** Logger de la clase. */
    private static final Logger LOG = Logger.getLogger(CanvasLayer.class.getName());
    
    /**
     * Aplicacion principal del juego <code>jme3</code>.
     */
    private final Application application;
    
    /**
     * Constructor predeterminados.
     * 
     * @param application Aplicacion jme3.
     */
    public CanvasLayer(Application application) {
        this.application = application;
        
        // Configuramos nuestro lienzo.
        CanvasLayer.this.setBackground(null);
        CanvasLayer.this.setLayout(new FreeLayout(CanvasLayer.this));
    }
    
    // [ Setters ] :Establece un nuevo diseño para los componetes
    //              hijos de esta ventana.
    @Override
    public void setLayout(GuiLayout layout) {
        if (layout != null 
                && !(layout instanceof FreeLayout)) {
            CanvasLayer.LOG.log(Level.WARNING, " [ Layout ] :It is recommended to use FreeLayout as the layout instead of {0}.", layout.getClass());
        }        
        super.setLayout(layout);
    }

    @Override
    public void setBackground(GuiComponent bg) {
        if (bg != null)
            throw new UnsupportedOperationException();
        super.setBackground(bg);
    }
    
    // [ Getters ] :Dimensiones escalado con la resolcion
    //              de pantalla de juego.
    public float getScaleFactorWidth()  { return getAppSettings().getWidth() / getWidth();   }
    public float getScaleFactorHeight() { return getAppSettings().getHeight() / getHeight(); }

    // [ Gettets ] :Dimensiones de la pantalla.
    public float getWidthScaled()  { return getAppSettings().getWidth();  }
    public float getHeightScaled() { return getAppSettings().getHeight(); }

    // [ Getters ] :Dimension de esta ventana.
    public float getWidth()  { return getPreferredSize().x;  }
    public float getHeight() {  return getPreferredSize().y; }
    
    /**
     * Devuleve las configuraciones del juego {@code jme3}.
     * @return Configuraciones del juego.
     */
    protected AppSettings getAppSettings() {
        return application.getContext().getSettings();
    }
}
