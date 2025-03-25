package net.felsstudio.clickgui.component.components;

import java.awt.*;
import java.util.ArrayList;

import net.felsstudio.clickgui.component.Component;
import net.felsstudio.fenix.ExampleMod;
import net.felsstudio.fenix.module.Module;
import net.felsstudio.clickgui.Setting;
import net.felsstudio.clickgui.component.components.sub.Checkbox;
import net.felsstudio.clickgui.component.components.sub.Keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.felsstudio.clickgui.component.components.sub.ModeButton;
import net.felsstudio.clickgui.component.components.sub.Slider;

public class Button extends net.felsstudio.clickgui.component.Component {

	public Module mod;
	public net.felsstudio.clickgui.component.Frame parent;
	public int offset;
	private boolean isHovered;
	private ArrayList<net.felsstudio.clickgui.component.Component> subcomponents;
	public boolean open;
	public int height;
	public FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	public Button(Module mod, net.felsstudio.clickgui.component.Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.height = 12;
		this.subcomponents = new ArrayList<net.felsstudio.clickgui.component.Component>();
		this.open = false;
		int opY = offset + 12;
		if(ExampleMod.instance.settingsManager.getSettingsByMod(mod) != null) {
			for(Setting s : ExampleMod.instance.settingsManager.getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(net.felsstudio.clickgui.component.Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? 0xFF222222 : 0xFF111111);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.mod.getName(), (parent.getX() + 5), (parent.getY() + offset + 2), this.mod.isEnabled() ? Color.ORANGE.hashCode() : 0xFFFFFF); //0x999999
		if(this.subcomponents.size() >= 2) {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "§7-" : "§7+", (parent.getX()+parent.getWidth()-10), (parent.getY() + offset + 2), -1);
		}
		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				for(net.felsstudio.clickgui.component.Component comp : this.subcomponents) {
					comp.renderComponent();
				}
			}
		}
	}

	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(net.felsstudio.clickgui.component.Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.toggle();
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(net.felsstudio.clickgui.component.Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(net.felsstudio.clickgui.component.Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		for(Component comp : this.subcomponents) {
			comp.keyTyped(typedChar, key);
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		if(x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset) {
			return true;
		}
		return false;
	}
}
