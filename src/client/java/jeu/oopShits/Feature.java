package jeu.oopShits;

import jeu.screens.ModConfig;

/*



 */


public abstract class Feature {

    public boolean enabled = false;

    public String name;

    public ModConfig.Config config;

    public void on() {
        enabled = true;
    }

    public void off() {
        enabled = false;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void init() {
        System.out.println("no init override: " + this.getClass().getName());
    }
}
