package org.monkey.lemur;

import com.jme3.util.SafeArrayList;

import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.CheckboxModel;
import com.simsilica.lemur.Command;

import java.util.Iterator;

/**
 * Un <code>ButtonGroup</code> se encarga de generar un objeto grupo, de botones
 * que en este grupo solo puede ser seleccionaod uno ellos.
 * <p>
 * Es decir que en un grupo solo se puede seleccionar una opcion({@code Button})
 * en la lista.
 * 
 * @author wil
 * @version 1.0.0
 * 
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class ButtonGroup {
    
    /**
     * La lista de botones que participan en este grupo.
     */
    protected SafeArrayList<Checkbox> buttons = new SafeArrayList<>(Checkbox.class);
    
    /**
     * Evento encargado se la seleccion de botones de este grupo.
     */
    protected Command<Button> toggleCommand = new ToggleGroupCommand(this);
    
    /**
     * La seleccion actual.
     */
    CheckboxModel selection = null;
    
    /**
     * Crea un nuevo <code>ButtonGroup</code>.
     */
    public ButtonGroup() {}
    
    /**
     * Agrega el botón al grupo.
     * @param b el boton que se agregara
     */
    public void add(Checkbox b) {
        if(b == null) {
            return;
        }
        buttons.add(b);
        
        if (b.isChecked()) {
            if (selection == null) {
                selection = b.getModel();
            } else {
                b.setChecked(false);
            }
        }
        
        if (toggleCommand != null) {
            if (b.getCommands(Button.ButtonAction.Click)
                .contains(toggleCommand)) {
                
                b.getCommands(Button.ButtonAction.Click)
                 .remove(toggleCommand);
           }
           b.getCommands(Button.ButtonAction.Click).remove(Checkbox.TOGGLE_COMMAND);
           b.addCommands(Button.ButtonAction.Click, toggleCommand);
        }
    }
    
    /**
     * Elimina el boton del grupo.
     * @param b el boton para ser eliminado
     */
    public void remove(Checkbox b) {
        if(b == null) {
            return;
        }
        buttons.remove(b);
        if(b.getModel() == selection) {
            selection = null;
        }
        
        b.getCommands(Button.ButtonAction.Click).remove(toggleCommand);
        b.addCommands(Button.ButtonAction.Click, Checkbox.TOGGLE_COMMAND);
    }
    
    /**
     * Borra la seleccion de modo que ninguno de los botones
     * en el <code>ButtonGroup</code> estan seleccionados.
     *
     * @since 1.6
     */
    public void clearSelection() {
        if (selection != null) {
            CheckboxModel oldSelection = selection;
            selection = null;
            oldSelection.setChecked(false);
        }
    }
    
    /**
     * Devuelve todos los botones que estan participando en
     * este grupo.
     * @return un <code>Iterator</code> de los botones de este grupo
     */
    public Iterator<Checkbox> getElements() {
        return buttons.iterator();
    }
    
    /**
     * Devuelve el modelo del boton seleccionado.
     * @return el modelo de boton seleccionado
     */
    public CheckboxModel getSelection() {
        return selection;
    }
    
    /**
     * Establece el valor seleccionado para el <code>CheckboxModel</code>.
     * Solo se puede seleccionar un botón del grupo a la vez.
     * @param m el <code>CheckboxModel</code>
     * @param b <code>true</code> si este botón debe ser
     * seleccionado, de lo contrario <code>falso</code>
     */
    public void setSelected(CheckboxModel m, boolean b) {
        if (b && m != null && m != selection) {
            CheckboxModel oldSelection = selection;
            selection = m;
            if (oldSelection != null) {
                oldSelection.setChecked(false);
            }
            m.setChecked(true);
        }
    }
    
    /**
     * Devuelve si se ha seleccionado un {@code CheckboxModel}.
     *
     * @param m una instancia de {@code CheckboxModel}
     * @return {@code true} si el botón está seleccionado,
     * de lo contrario, devuelve {@code false}
     */
    public boolean isSelected(CheckboxModel m) {
        return (m == selection);
    }

    /**
     * Devuelve el número de botones del grupo.
     * @return el conteo de botones
     * @since 1.3
     */
    public int getButtonCount() {
        if (buttons == null) {
            return 0;
        } else {
            return buttons.size();
        }
    }
    
    /**
     * Clase <code>ToggleGroupCommand</code> es el encargado de gestionar la
     * seleccion de los botones en el grupo.
     * <p>
     * Ya que en un grupo solo un boton puede estar eleccionado.
     */
    protected static class ToggleGroupCommand implements Command<Button> {
        
        /**
         * Grupo de los botones.
         */
        protected ButtonGroup group = null;

        /**
         * Genere un nuevo <code>ToggleGroupCommand</code>.
         * 
         * @param group Grupo de los botones.
         */
        public ToggleGroupCommand(ButtonGroup group) {
            this.group = group;
        }
        
        /**
         * JavaDoc no definido.         * 
         * @param source Parametro de metodo.
         */
        @Override
        public void execute(Button source) {
            if ((source instanceof Checkbox) 
                    && group != null) {
                Checkbox button = (Checkbox) source;
                group.setSelected(button.getModel(), true);
            }
        }
    }
}
