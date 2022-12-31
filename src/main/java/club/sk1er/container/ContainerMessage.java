package club.sk1er.container;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class ContainerMessage {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null,
            "This is a Forge mod, not an application. Please put this in your mods folder, located inside your Minecraft folder."
                + "\nIf you don't know how to install a Forge mod, search 'Forge Mod Installation Tutorials' online."
                + "\nIf you're still lost, contact the support Discord at https://polyfrost.cc/discord.",
            "This is not the proper installation method.", JOptionPane.INFORMATION_MESSAGE);

        System.exit(0);
    }
}
