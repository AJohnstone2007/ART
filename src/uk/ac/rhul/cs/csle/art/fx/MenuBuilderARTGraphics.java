package uk.ac.rhul.cs.csle.art.fx;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SeparatorMenuItem;

public class MenuBuilderARTGraphics extends MenuBuilder {
  @Override
  public MenuBar buildMenuBar() {
    MenuBar menuBar = new MenuBar();
    Menu menu;

    menu = new Menu("_Show");
    addMenuItem(menu, "T_WE set");
    addMenuItem(menu, "_Lexicalisations");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Full SPPF");
    addMenuItem(menu, "_Core SPPF");
    addMenuItem(menu, "_Residual SPPF");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Derivation term");
    addMenuItem(menu, "_Final term");
    addMenuItem(menu, "_Attributed final term");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "E_xit", "shortcut+Q");
    menuBar.getMenus().add(menu);

    menu = new Menu("_View");
    addMenuItem(menu, "_Home", "shortcut+H");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "_Above");
    addMenuItem(menu, "_Under");
    addMenuItem(menu, "_Left");
    addMenuItem(menu, "_Right");
    addMenuItem(menu, "_Front", "shortcut+O");
    addMenuItem(menu, "_Back");
    addMenuItem(menu, "_Turntable");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Zoom In", "shortcut+=");
    addMenuItem(menu, "Zoom Out", "shortcut+-");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Pan Up", "shift+UP");
    addMenuItem(menu, "Pan Down", "shift+DOWN");
    addMenuItem(menu, "Pan Left", "shift+LEFT");
    addMenuItem(menu, "Pan Right", "shift+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Rotate Up", "shortcut+UP");
    addMenuItem(menu, "Rotate Down", "shortcut+DOWN");
    addMenuItem(menu, "Rotate Left", "shortcut+LEFT");
    addMenuItem(menu, "Rotate Right", "shortcut+RIGHT");
    menu.getItems().add(new SeparatorMenuItem());
    addMenuItem(menu, "Parallel _view");
    addMenuItem(menu, "Perspective _view");
    menuBar.getMenus().add(menu);

    return menuBar;
  }

}
