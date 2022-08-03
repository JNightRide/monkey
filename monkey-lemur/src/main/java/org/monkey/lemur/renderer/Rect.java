package org.monkey.lemur.renderer;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

import java.io.IOException;
import java.util.Objects;

/**
 * La clase <code>Rect</code> es el ecargado de almacenar las dimensiones y
 * posiciones de un determinado componentre grafico.
 * <p>
 * Los valores almacenados en esta clase u objeto son utilizados como
 * referencia para escalar los componentes segun la resolucion de la pantalla
 * donse se muestran.
 *  
 * @author wil
 * @version 1.0.0
 * 
 * @since 1.0.0
 */
public final 
class Rect implements Cloneable, Savable {
    
    /**
     * Clave para el objeto {@code Savable} de las dimensiones.
     */
    protected static final String GL_SIZE = "GL::Size";
    
    /**
     * Clave para el objeto {@code Savable} de las posiciones.
     */
    protected static final String GL_POS  = "GL::Pos";
    
    /**
     * Encargado de almacenar la posicion {@code original} del
     * 'Gui', es utilizada para determinar la escala de de la posiciones
     * del componentes en la pantalla.
     */
    private Vector3f pos = new Vector3f(0.0F, 0.0F, 0.0F);
    
    /**
     * Se encarga de almacenar las dimensiones {@code original} del
     * 'Gui', estos datos se utilizan para escalar/redimensionar un
     * componente en pantalla.
     */
    private Vector3f size = new Vector3f(0.0F, 0.0F, 0.0F);
    
    /**
     * Genere un <code>Rect</code> utilizando su 
     * constructor predeterminado.
     */
    public Rect() {}
    
    /**
     * Metodo encargado de generar un clon de este objeto.
     * @return Clon generado a partir de los datos 
     * de este {@code Rect}.
     */
    @Override
    public Rect clone() {
        try {
            Rect clon = (Rect) 
                        super.clone();            
            clon.size = size.clone();
            clon.pos  = pos.clone();
            return clon;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    
    /**
     * Establece nuevas dimensiones para esta {@code Rect}. Solo
     * se puede redimensionar en 2 ejes.
     * 
     * @param with
     *          Largo de la recta.
     * @param height 
     *          Ancho de la recta.
     */
    public void setSize(float with, float height) {
        this.setSize(new Vector3f(with, height, this.size.getZ()));
    }
    
    /**
     * Metodo encargado de establecer una nueva dimension. Si el parametro
     * es <code>null</code>, las dimensiones seran <code>0</code>.
     * @param size Vector 3D para la nueva dimension.
     */
    public void setSize(Vector3f size) {
        if (size == null) {
            this.size.zero();
        } else {
            this.size = size;
        }
    }
    
    /**
     * Establece una nueva posicion para la recta en 2D.
     * @param x nueva posicion en {@code x}.
     * @param y nueva posicion en {@code y}.
     */
    public void setLocation(float x, float y) {
        this.setLocation(new Vector3f(x, y, this.pos.getZ()));
    }
    
    /**
     * Metodo encargado de establecer una nueva posiciones. Si la posiciones
     * es <code>null</code>, las tres posiciones {@code x, y, z} seran <code>0</code>.
     * @param pos Vector 3D para la nueva posicion.
     */
    public void setLocation(Vector3f pos) {
        if (pos == null) {
            this.pos.zero();
        } else {
            this.pos = pos;
        }
    }
    
    /**
     * Devuelve un clon de las dimensiones de  esta {@code Rect}.
     * @return Dimension de la recta.
     */
    public Vector3f getSize() {
        return this.size.clone();
    }
    
    /**
     * Devuelve un clon de las posiciones de esta {@code Rect}.
     * @return Posicicion de la recta.
     */
    public Vector3f getLocation() {
        return this.pos.clone();
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);        
        out.write(size, GL_SIZE, null);
        out.write(pos,  GL_POS,  null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);        
        size = (Vector3f) in.readSavable(GL_SIZE, new Vector3f(0.0F, 0.0F, 0.0F));
        pos  = (Vector3f) in.readSavable(GL_POS,  new Vector3f(0.0F, 0.0F, 0.0F));
    }

    /**
     * Metodo encargado de generar una cadena que represente este
     * objeto {@code Rect}.
     * @return Un cadena como valor.
     */
    @Override
    public String toString() {
        return "Rect[" + "pos="    + pos 
                       + ", size=" + size + ']';
    }

    /**
     * Metodo encargado de generar el codigo hash de la clase.
     * @return Codigo hash generado.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.pos);
        hash = 79 * hash + Objects.hashCode(this.size);
        return hash;
    }

    /**
     * Metodo encargado de comparar este objeto con otro y determinar si son
     * iguales a nivel de contenido o asi misma.
     * 
     * @param obj
     *          Otro objeto con quien compararse.
     * @return {@code true} si son iguales, es decir si tienen los mismos
     *           valores, de lo contrarios {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rect other = (Rect) obj;
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        return Objects.equals(this.size, other.size);
    }
}
