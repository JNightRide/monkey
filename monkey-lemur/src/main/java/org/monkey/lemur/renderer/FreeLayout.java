package org.monkey.lemur.renderer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import com.simsilica.lemur.component.AbstractGuiComponent;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.GuiLayout;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.monkey.lemur.CanvasLayer;

/**
 * Un <code>FreeLayout</code> se encarga de gestionar e organizars los diferentes
 * componentes graficos en la intrefaz del usuario. Aciendolos adaptables a cualquier
 * resulución de pantalla.
 * 
 * @author wil
 * @version 1.5.1
 * 
 * @since 1.0.0
 */
public class FreeLayout extends AbstractGuiComponent implements GuiLayout, Cloneable {
/** Logger de la clase. */
private static final Logger LOG = Logger.getLogger(FreeLayout.class.getName());
    
    /**
     * Mapa o diccionario donde agregaremos los diferentes nodos
     * hijos del componente padre.
     */
    private final Map<Node, FreeControl> children 
                                            = new IdentityHashMap<>();
    /**
     * Ventana principal donde se vizualizan todos los componentes
     * de la intrefaz de usuario <code>GUI</code>.
     */
    private final CanvasLayer canvasLayer;

    /**
     * Constructor predeterminado.
     * 
     * @param canvasLayer Ventana principal.
     */
    public FreeLayout(CanvasLayer canvasLayer) {
        this.canvasLayer = canvasLayer;
    }
    
    @Override
    public void calculatePreferredSize(Vector3f size) {
        FreeControl control = getNode().getControl(FreeControl.class);
        if (control == null) {
            size.set(new Vector3f(1.0F, 1.0F, 0.0F));
        } else {
            Vector3f myPref = control.getRect().getSize();
            size.set(myPref);
        }
    }

    @Override
    public void reshape(Vector3f pos, Vector3f size) {
        if (canvasLayer == null) {
            FreeLayout.LOG.warning("Main window not found");
            return;
        }        
        for (final Map.Entry<?, ?> entry : this.children.entrySet()) {
            if (entry.getValue() == null)
                continue;
            
            FreeControl control = (FreeControl) entry.getValue();
            GuiControl guiControl  = ((Node) entry.getKey()).getControl(GuiControl.class);
            
            control.getRect().setSize(guiControl.getPreferredSize().clone());
            control.updateGui();
        }
    }

    @Override
    public <T extends Node> T addChild(T t, Object... constraints) {
        if(t != null && t.getControl(GuiControl.class) == null)
            throw new IllegalArgumentException( "Child is not GUI element." );
        
        if (t == null)
            return null;
        
        if (children.containsKey(t))
            removeChild(t);
        
        Constraints args = null;
        FreeControl newFreeControl;
        
        if (t.getControl(FreeControl.class) != null)  {
            newFreeControl = t.getControl(FreeControl.class);
            args = newFreeControl.getConstraints();
        } else {
            newFreeControl = new FreeControl(canvasLayer, getInstanceConstraints(constraints));
        }
        
        t.addControl(newFreeControl);
        children.put(t, newFreeControl);
        
        if (getGuiControl() != null) {
            getGuiControl().getNode().attachChild(t);
            newFreeControl.attach();
            
            if ( args != null ) {
                Constraints newConstraints = getInstanceConstraints(constraints);
                if ( !args.equals(newConstraints) ) {
                    newFreeControl.setConstraints(newConstraints);
                }
            }
        }
        
        invalidate();
        return t;
    }
    
    private <T extends Node> Constraints getInstanceConstraints(Object... constraints) {
        if (constraints == null || constraints.length < 1) {
            return new Constraints();
        }
        
        Boolean bool  = null;
        Layout layout = null;
        
        for (final Object element : constraints) {
            if (element == null)
                continue;
            
            if ((element instanceof Boolean) 
                    && (bool == null)) {
                bool = (Boolean) element;
            } else if ((element instanceof Layout) 
                            && layout == null) {
                layout = (Layout) element;
            }
            
            if (bool != null 
                    && layout != null)
                break;
        }
        
        if (bool == null)
            bool = Boolean.FALSE;
        
        if (layout == null)
            layout = Layout.Center;
        
        return new Constraints(bool, layout);
    }

    @Override
    public void detach(GuiControl parent) {
        super.detach(parent);
        for (final Node child : getChildren()) {
            if (child == null)
                continue;            
            child.removeFromParent();
        }
    }

    @Override
    public void attach(GuiControl parent) {
        super.attach(parent);
        for (final Node child : getChildren()) {
            if (child == null)
                continue;
            getNode().attachChild(child);
            child.getControl(FreeControl.class).attach();
        }
    }

    @Override
    public void removeChild(Node n) {
        if (children.remove(n) == null)
            return;
        
        /*n.removeControl(FreeControl.class);*/
        n.removeFromParent();
        invalidate();
    }

    @Override
    public Collection<Node> getChildren() {
        return Collections.unmodifiableSet(this.children.keySet());
    }

    @Override
    public void clearChildren() {
        if (this.children == null)
            return;
        
        for (final Map.Entry<?, ?> entry : this.children.entrySet()) {
            if (entry.getKey() == null)
                continue;
            
            ((Node) entry.getKey()).removeFromParent();
            /*((Node) entry.getKey()).removeControl(FreeControl.class);*/
        }
        this.children.clear();
    }

    @Override
    public GuiLayout clone() {
        return new FreeLayout(canvasLayer);
    }
}
