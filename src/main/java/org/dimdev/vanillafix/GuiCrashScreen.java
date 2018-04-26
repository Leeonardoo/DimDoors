package org.dimdev.vanillafix;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.ddutils.HasteUpload;
import org.dimdev.vanillafix.mixins.client.IFixedMinecraft;

import java.io.File;
import java.net.URI;

@SideOnly(Side.CLIENT)
public class GuiCrashScreen extends GuiScreen {
    private static final String HASTE_BASE_URL = "https://paste.dimdev.org";
    private static final Logger log = LogManager.getLogger();

    private File reportFile;
    private final CrashReport report;
    private String hasteLink = null;

    public GuiCrashScreen(File reportFile, CrashReport report) {
        this.reportFile = reportFile;
        this.report = report;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiOptionButton(0, width / 2 - 155, height / 4 + 120 + 12, I18n.format("gui.toTitle")));
        buttonList.add(new GuiOptionButton(1, width / 2 - 155 + 160, height / 4 + 120 + 12, "Get Link")); // TODO: localize
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            if (button.id == 0) {
                mc.displayGuiScreen(new GuiMainMenu());
                ((IFixedMinecraft) mc).clearCurrentReport();
            } else if (button.id == 1) {
                if (hasteLink == null) {
                    hasteLink = HasteUpload.uploadToHaste(HASTE_BASE_URL, "txt", report.getCompleteReport());
                }
                ReflectionHelper.findField(GuiScreen.class, "clickedLinkURI", "field_175286_t").set(this, new URI(hasteLink));
                mc.displayGuiScreen(new GuiConfirmOpenLink(this, hasteLink, 31102009, false));
            }
        } catch (Throwable e) {
            log.error("Exception when crash menu button clicked:", e);
            button.displayString = "[Failed]";
            button.enabled = false;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, "Minecraft crashed!", width / 2, height / 4 - 60 + 20, 0xFFFFFF);
        drawString(fontRenderer, "Minecraft ran into a problem and crashed.", width / 2 - 160, height / 4 - 60 + 60, 0xA0A0A0);
        drawString(fontRenderer, "This is probably caused by a bug in one of your mods or vanilla", width / 2 - 160, height / 4 - 60 + 60 + 18, 0xA0A0A0);
        drawString(fontRenderer, "Minecraft. Since you have VanillaFix installed, you can return to", width / 2 - 160, height / 4 - 60 + 60 + 27, 0xA0A0A0);
        drawString(fontRenderer, "the main menu and keep playing despite the crash.", width / 2 - 160, height / 4 - 60 + 60 + 36, 0xA0A0A0);
        drawString(fontRenderer, "A crash report has been generated, and can be found here (click):", width / 2 - 160, height / 4 - 60 + 60 + 54, 0xA0A0A0);
        drawCenteredString(fontRenderer, reportFile != null ? "\u00A7n" + reportFile.getName() : "(report failed to save, see the log instead)", width / 2, height / 4 - 60 + 60 + 65, 0x00FF00);
        drawString(fontRenderer, "You are encouraged to send it to the mod's author to fix this issue", width / 2 - 160, height / 4 - 60 + 60 + 78, 0xA0A0A0);
        drawString(fontRenderer, "Click the \"Get Link\" button to upload the crash report.", width / 2 - 160, height / 4 - 60 + 60 + 87, 0xA0A0A0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}