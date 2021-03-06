package nl.paulinternet.gtasaveedit.view.component;

import nl.paulinternet.gtasaveedit.model.Model;
import nl.paulinternet.gtasaveedit.model.Settings;
import nl.paulinternet.gtasaveedit.view.Main;
import nl.paulinternet.gtasaveedit.view.window.MainWindow;
import nl.paulinternet.gtasaveedit.view.menu.MenuBar;
import nl.paulinternet.gtasaveedit.view.pages.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabbedPane extends JTabbedPane {
    private static Boolean loaded = Boolean.FALSE;
    private final PageAbout pageAbout;
    private List<Page> pages;

    public TabbedPane() {
        // Create pages
        pages = new ArrayList<>();

        pages.addAll(Arrays.asList(
                new PageGeneral(),
                new PageSkills(),
                new PageLocation(),
                new PageSchools(),
                new PageWeapons(),
                new PageGangWeapons(),
                new PageZones(),
                new PagePeds(),
                new PageClothes(),
                new PageCollectables(),
                new PageFix(),
                new PageOptions()));

        pageAbout = new PageAbout();
        if (!Main.MAC) {
            pages.add(pageAbout);
        }

        // Add tabs
        pages.forEach(p -> addTab(p.getTitle(), p.getComponent()));

        // Set the border
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Observe
        Model.gameLoaded.addHandler(this, "onGameLoaded");
        Model.gameClosed.addHandler(this, "onGameClosed");
        onGameClosed();

        addChangeListener(e -> {
            if (pageAbout.getComponent().equals(TabbedPane.this.getSelectedComponent()) && Settings.getSoundOnAboutPage() == Settings.YES) {
                pageAbout.play();
            } else {
                pageAbout.stop();
            }
        });
    }


    @SuppressWarnings("unused") // used in event
    public void onGameLoaded() {
        if (!loaded) {
            removeAll();
            pages.forEach(p -> addTab(p.getTitle(), p.getComponent()));
            loaded = true;
            MenuBar menubar = (MenuBar) MainWindow.getInstance().getJMenuBar();
            if (menubar != null) {
                menubar.onSavegameStateChange(true);
            } else {
                System.err.println("Unable to get menuBar: " + MainWindow.getInstance().getJMenuBar());
            }
            MainWindow.getInstance().validate();
        }
    }

    @SuppressWarnings("WeakerAccess") // used in event
    public void onGameClosed() {
        removeAll();
        pages.forEach(p -> {
            if (p.isAlwaysVisible()) {
                addTab(p.getTitle(), p.getComponent());
            }
        });
        loaded = false;
        MenuBar menubar = (MenuBar) MainWindow.getInstance().getJMenuBar();
        if (menubar != null) {
            menubar.onSavegameStateChange(false);
        } else {
            System.err.println("Unable to get menuBar: " + MainWindow.getInstance().getJMenuBar());
        }
        MainWindow.getInstance().validate();
    }

    public void updateUI() {
        super.updateUI();
        if (!loaded && pages != null) {
            pages.forEach(p -> {
                if (!p.isAlwaysVisible()) {
                    SwingUtilities.updateComponentTreeUI(p.getComponent());
                }
            });
        }
    }

    public void onShowPreferences() {
        pages.forEach(p -> {
            if (p instanceof PageOptions) {
                setSelectedComponent(p.getComponent());
            }
        });
    }
}
