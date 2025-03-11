package net.geforcemods.securitycraft.misc;

import net.minecraft.item.crafting.Ingredient;

public enum PageGroup {
	NO_PAGE(false, "", ""), //ignored anyway
	SINGLE_ITEM(false, "", ""), //ignored anyway
	KEYCARDS(true, "gui.securitycraft:scManual.keycards", "help.securitycraft.keycards.info");

	private final boolean hasRecipeGrid;
	private final String title;
	private final String specialInfoKey;
	private Ingredient items = Ingredient.EMPTY;

	PageGroup(boolean hasRecipeGrid, String title, String specialInfoKey) {
		this.hasRecipeGrid = hasRecipeGrid;
		this.title = title;
		this.specialInfoKey = specialInfoKey;
	}

	public boolean hasRecipeGrid() {
		return hasRecipeGrid;
	}

	public String getTitle() {
		return title;
	}

	public String getSpecialInfoKey() {
		return specialInfoKey;
	}

	public Ingredient getItems() {
		return items;
	}

	public void setItems(Ingredient items) {
		this.items = items;
	}
}
