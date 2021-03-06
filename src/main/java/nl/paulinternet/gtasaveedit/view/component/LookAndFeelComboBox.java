package nl.paulinternet.gtasaveedit.view.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import nl.paulinternet.gtasaveedit.model.Model;
import nl.paulinternet.gtasaveedit.view.swing.PComboBox;

public class LookAndFeelComboBox extends PComboBox
{
	private static class Item
	{
		private String name;
		private String className;
		
		public Item (String name, String className) {
			this.name = name;
			this.className = className;
		}
		
		public String toString () {
			return name;
		}
	}
	
	private List<Item> items;
	
	public LookAndFeelComboBox () {
		items = new ArrayList<>();

		LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();

		Arrays.asList(looks).forEach(look -> {
			Item item = new Item(look.getName(), look.getClassName());
			items.add(item);
			addItem(item);
		});
		
		// Event
		copyFromModel();
		onChange().addHandler(this, "copyToModel");
		Model.editSettings.lookAndFeelClassName.onChange().addHandler(this, "copyFromModel");
	}
	
	@SuppressWarnings("WeakerAccess") // used in handler
	public void copyFromModel() {
		String value = Model.editSettings.lookAndFeelClassName.getValue();

		for (Item item : items) {
			if (item.className.equals(value)) {
				setSelectedItem(item);
			}
		}
	}
	
	@SuppressWarnings("unused") // used in handler
	public void copyToModel () {
		Model.editSettings.lookAndFeelClassName.setValue(items.get(getSelectedIndex()).className);
	}
}
