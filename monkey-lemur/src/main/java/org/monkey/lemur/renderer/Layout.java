package org.monkey.lemur.renderer;

/**
 * Un <code>Layout</code> que el que determina la posicion donde el
 * componetes se localiza.
 * 
 * @author wil
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Layout {
    // [ LAYOUT ] :Centrado en el contenedor padre.
    Center, CenterTop, CenterBottom,
    
    // v[ LAYOUT ] :Centrado en la derecha.
    RightCenter, RightTop, RightBottom,
    
    // [ LAYOUT ] :Centrado en la izquierda
    LeftCenter, LeftTop, LeftBottom;
}
