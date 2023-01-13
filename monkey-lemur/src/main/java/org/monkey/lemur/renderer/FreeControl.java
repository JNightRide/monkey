package org.monkey.lemur.renderer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.core.AbstractNodeControl;
import com.simsilica.lemur.core.GuiControl;

import org.monkey.lemur.CanvasLayer;

/**
 * Un <code>FreeControl</code> se encarga de gestinar un componente de la interfaz
 * de usuario que utiliza como layout {@link FreeLayout}.
 * 
 * @author wil
 * @version 1.0.0
 * 
 * @since 1.0.0
 */
@SuppressWarnings(value = {"unchecked"})
public class FreeControl extends AbstractNodeControl<FreeControl> {
    
    /**
     * Lienzo principal que actua como una ventana, es el encargado
     * de proporcionar las escalas para los componentes.
     */
    private CanvasLayer canvasLayer;
    
    /**
     * Cualquier componentes que se agregue a un {@code FreeLayout}, debe
     * tener un padre a exepcion de contenedores principales.
     */
    private FreeControl parent;

    /**
     * Un {@code Layout} es el encargado de establecer un diseño de la
     * posiciones de componente grafico.
     */
    private Layout layout;
    
    /**
     * Objeto encargado de almacenar las dimensiones y posiciones 'originales'
     * del componente graficos.
     */
    private Rect rect;
    
    /**
     * {@code true} si la escala de los componentes es con el largo del lienzo
     * principal, de lo contrario {@code false} si es con el ancho.
     */
    private boolean lockscaling;
        
    /**
     * Constructor predeterminado. <b>Solo para usuo interno</b>.
     */
    protected FreeControl() {}

    /**
     * Genera un <code>FreeControl</code> que se encargara de gestionar el
     * control de las dimensiones y posiciones de su componente graficos que
     * esta de un <code>FreeLayout</code>.
     * 
     * @param canvasLayer
     *          Lienzo principal donde se visualizaran sus componentes.
     * @param constraints 
     *          Los valor con que se manejara este componentes de la
     *              interfaz de usuario.
     */
    public FreeControl(CanvasLayer canvasLayer, Constraints constraints) {
        this.lockscaling = constraints.getBool();
        this.layout      = constraints.getLayout();
        this.canvasLayer = canvasLayer;
        this.rect        = new Rect();
    }
    
    /**
     * Metodo encargado de establecer la visibilidad del componete
     * grafico que se ete gestionando.
     * 
     * @param visible {@code true} si se desea que el componente sea visible,
     *                  de lo contrario {@code false} para que no se visualize.
     */
    public void setVisible(boolean visible) {
        if (visible && 
                spatial.getCullHint().equals(Spatial.CullHint.Always)) {
            spatial.setCullHint(Spatial.CullHint.Never);
        } else if (!visible 
                    && spatial.getCullHint().equals(Spatial.CullHint.Never)) {
            spatial.setCullHint(Spatial.CullHint.Always);
        }
    }    
    
    /**
     * Determina la visibilidad del componente graficos.
     * @return Un valor booleando.
     */
    public boolean isVisible() {
        return spatial.getCullHint()
                      .equals(Spatial.CullHint.Never);
    }
    
    /**
     * Devuelve el componente grafico de la unterfaz del usuario.
     * @param <E> Tipo de dato a retornar.
     * @return Componnete de la interfaz grafica.
     */
    public <E extends Panel> E getGui() {
        return (E) getSpatial();
    }

    /**
     * Devuelve al linezo principal de la pantalla.
     * @return Contenedor principal de la interfaz grafica.
     */
    public CanvasLayer getCanvasLayer() {
        return canvasLayer;
    }

    /**
     * Devuelve el padre del componente grafico.
     * @return Padre del componente, de lo contrario
     * <code>null</code>.
     */
    public FreeControl getParent() {
        return parent;
    }

    /*
        Getters.
    */
    public Layout getLayout() {
        return layout;
    }
    public Rect getRect() {
        return rect;
    }
    public Constraints getConstraints() {
        return new Constraints(lockscaling, layout);
    }
    
    /**
     * Determina si el control tiene un padre.
     * @return {@code true} si el control tiene un padre, de lo
     *          contrario {@code false}.
     */
    public boolean hasParent() {
        return parent != null;
    }
    
    /**
     * Devuelve el largo del componente.
     * @return Dimension del GUI.
     */
    public float getWidth() {
        return spatial.getControl(GuiControl.class).getSize().x;
    }
    
    /**
     * Devuelve el ancho del componente.
     * @return Dimension del GUI.
     */
    public float getHeight() {
        return spatial.getControl(GuiControl.class).getSize().y;
    }
    
    protected float getParentWidth() {
        if (hasParent()) {
            return parent.getWidth();
        } else {
            return canvasLayer.getWidth() * canvasLayer.getScaleFactorWidth();
        }
    }
    protected float getParentHeight() {
        if (hasParent()) {
            return parent.getHeight();
        } else {
            return canvasLayer.getHeight() * canvasLayer.getScaleFactorHeight();
        }
    }
    
    /**
     * Metodo encargado de actuliza las dimensiones y posiciones del
     * componente de la interfaz de usuario.
     */
    public void updateGui() {
        Vector3f mySize = new Vector3f();        
        // Obtenemos el control del componente.
        GuiControl control = spatial.getControl(GuiControl.class);
        if (canvasLayer != null) {
            Vector3f prefSize = rect.getSize();            
            mySize.setX(isLockScaling() 
                                ? prefSize.getX() * canvasLayer.getScaleFactorHeight()
                                : prefSize.getX() * canvasLayer.getScaleFactorWidth());
                
            mySize.setY(prefSize.getY() * canvasLayer.getScaleFactorHeight());
            mySize.setZ(prefSize.getZ());
        }
        // establecemos las nuevas caracteristicas
        // sobre el control del componente.
        control.setSize(mySize);
        
        // centramos el nodo.
        if (hasParent()) {
            control.getNode().setLocalTranslation((getParentWidth()/ 2.0f),
                                                 -(getParentHeight() / 2.0f), rect.getLocation().z);
        } else {
            control.getNode().setLocalTranslation((canvasLayer.getWidth() / 2f) * canvasLayer.getScaleFactorWidth(),
                                                  (canvasLayer.getHeight() / 2f) * canvasLayer.getScaleFactorHeight(), rect.getLocation().z);
        }

        // Calculamos la nueva posicion segun el diseño.
        control.getNode().move(calculatePosition(control));
        
        // Centramos el componete en la nueva posicion.
         control.getNode().move(-mySize.x * 0.5F, mySize.y * 0.5F, 0.0F);
    }

    /**
     * Metodo encargado de escalar la posicion del componente segun el diseño
     * establecido de ello.
     * 
     * @param control
     *          Control de componente GUI.
     * @return Posicione 3D para el componente.
     */
    private Vector3f calculatePosition(GuiControl control) {
        if (canvasLayer == null)
            return new Vector3f(0.0F, 0.0F, 0.0F);
        
        if (layout == null)
            layout = Layout.Center;
        
        float width  = control.getSize().x,
              height = control.getSize().y;
        
        Vector3f myPos = rect.getLocation();
        float offsetX = myPos.getX();
        float offsetY = myPos.getY();
        
        float xPos, yPos, zPos = myPos.getZ();
        switch (layout) {
            case Center:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = (offsetX * canvasLayer.getScaleFactorHeight());
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = (offsetX * canvasLayer.getScaleFactorWidth());
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case CenterBottom:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = offsetX * canvasLayer.getScaleFactorHeight();
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = offsetX * canvasLayer.getScaleFactorWidth();
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case CenterTop:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = offsetX * canvasLayer.getScaleFactorHeight();
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = offsetX * canvasLayer.getScaleFactorWidth();
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case LeftBottom:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorHeight();
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorWidth();
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case LeftCenter:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorHeight();
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorWidth();
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case LeftTop:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorHeight();
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = -(getParentWidth() * 0.5f) + (width * 0.5f) + offsetX * canvasLayer.getScaleFactorWidth();
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case RightBottom:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorHeight());
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorWidth());
                    yPos = -(getParentHeight() * 0.5f) + (height * 0.5f) + (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case RightCenter:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorHeight());
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorWidth());
                    yPos = (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            case RightTop:
                if (hasParent() && getParent().isLockScaling()) {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorHeight());
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                } else {
                    xPos = (getParentWidth() * 0.5f) - (width * 0.5f) - (offsetX * canvasLayer.getScaleFactorWidth());
                    yPos = (getParentHeight() * 0.5f) - (height * 0.5f) - (offsetY * canvasLayer.getScaleFactorHeight());
                }
                return new Vector3f(xPos, yPos, zPos);
            default:
                throw new AssertionError();
        }
    }
    
    /**
     * Devuelve el estado de escalado.
     * @return Un valor bollean.
     */
    public boolean isLockScaling() {
        return this.lockscaling;
    }
    
    /**
     * Establece una nueva posicione.
     * @param x posicion en {@code x}.
     * @param y posicion en {@code y}.
     */
    public void setPosition(float x, float y) {
        this.rect.setLocation(x, y);
        this.updateGui();
    }
    
    /**
     * Establce la profundidad del objeto.
     * @param z profundidad.
     */
    public void setDepthPosition(float z) {
        Vector3f old = this.rect.getLocation();
        this.rect.setLocation(new Vector3f(old.getX(), old.getY(), z));
        this.updateGui();
    }

    /**
     * Establece un nuevo diseño de la posicion.
     * @param layout Nuevo diseño.
     */
    public void setLayout(Layout layout) {
        this.layout = layout == null 
                    ? Layout.Center : layout;
        this.updateGui();
    }
    
    /*
        Setters.
    */
    public void setLockscaling(boolean lockscaling) {
        this.lockscaling = lockscaling;
        this.updateGui();
    }
    
    public void setConstraints(Constraints constraints) {
        this.layout = constraints.getLayout();
        this.lockscaling = constraints.getBool();
        this.updateGui();
    }
    
    public Vector3f getPosition() {
        return spatial.getLocalTranslation();
    }
    public Vector3f getScreenPosition() {
        return new Vector3f(getPosition().x + (canvasLayer.getWidth() * 0.5f * canvasLayer.getScaleFactorWidth()),
                            getPosition().y + (canvasLayer.getHeight() * 0.5f * canvasLayer.getScaleFactorHeight()), getPosition().z);
    }
    
    /**
     * Si el componente administrado es una extencia de {@code Label}, de ser
     * asi puedes establecer untamaña escalable para la fuente.     * 
     * @param size tamaño de la funete.
     */
    public void setFontSize(float size) {
        Panel gui = getGui();
        if (gui instanceof Label) {
            ((Label) gui).setFontSize(size * canvasLayer.getScaleFactorHeight());
        }
    }
    
    @Override
    protected void attach() {
        if (spatial != null && spatial.getControl(GuiControl.class) == null)
            throw new IllegalArgumentException("Child is not GUI element.");
        
        Node parentNode = spatial.getParent();
        if (parentNode != null
                && parentNode.getControl(FreeControl.class) != null) {
            parent = parentNode.getControl(FreeControl.class);
        }
    }
    @Override
    protected void detach() { 
        parent = null;
    }
}
