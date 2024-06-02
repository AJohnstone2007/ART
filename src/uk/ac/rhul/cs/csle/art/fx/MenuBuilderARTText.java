package uk.ac.rhul.cs.csle.art.fx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;

public class MenuBuilderARTText extends MenuBuilder {
  @Override
  public MenuBar buildMenuBar() {
    MenuBar menuBar = new MenuBar();
    Menu menu;

    menu = new Menu("_File");
    addMenuItem(menu, "_Run", "shortcut+R");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_New");
    addMenuItem(menu, "_Open");
    addMenuItem(menu, "_Close");
    addMenuItem(menu, "_Save", "shortcut+S");
    addMenuItem(menu, "Save _As");
    addMenuItem(menu, "Save A_ll");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Export");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "E_xit", "shortcut+Q");

    menuBar.getMenus().add(menu);

    menu = new Menu("_Edit");
    addMenuItem(menu, "_Find", "shortcut+F");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Start of text", "shortcut+H");
    addMenuItem(menu, "End of text", "shortcut+H");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Page Up", "shift+UP");
    addMenuItem(menu, "Page Down", "shift+DOWN");
    addMenuItem(menu, "Start of line", "shift+LEFT");
    addMenuItem(menu, "End of line", "shift+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Copy", "shortcut+C");
    addMenuItem(menu, "Cu_t", "shortcut+X");
    addMenuItem(menu, "_Paste", "shortcut+V");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Co_mment", "shortcut+/");
    addMenuItem(menu, "_Reformat", "shortcut+T");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Enlarge font", "shortcut+=");
    addMenuItem(menu, "Reduce font", "shortcut+-");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Preferences");
    menuBar.getMenus().add(menu);

    menu = new Menu("_Help");
    addMenuItem(menu, "_Contents");
    addMenuItem(menu, "_About");
    menuBar.getMenus().add(menu);

    return menuBar;
  }

}
