package uk.ac.rhul.cs.csle.art.fx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public abstract class MenuBuilder {
  public abstract MenuBar buildMenuBar();

  protected void addMenuItem(Menu menu, String label) {
    addMenuItem(menu, label, null);
  }

  public void addMenuItem(Menu menu, String label, String accelerator) {
    MenuItem ret = new MenuItem(label);
    menu.getItems().add(ret);
    ret.setOnAction(e -> menuAction(label));
    if (accelerator != null) ret.setAccelerator(KeyCombination.keyCombination(accelerator));
  }

  public void menuAction(String s) {
    System.err.println("Deafault menu builder: no actions defined");
  }

}
