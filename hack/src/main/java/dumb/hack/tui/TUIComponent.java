package dumb.hack.tui;

import com.googlecode.lanterna.gui2.Panel;
import dumb.hack.App;

public interface TUIComponent {
    String getName();
    Panel createPanel(App app, BreadcrumbManager breadcrumbManager);
}
