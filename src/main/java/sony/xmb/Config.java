package sony.xmb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Config extends DefaultHandler {
	public class Category extends Item {

		public Item getItem(int index) {
			return (Item) items.get(index);
		}

		public List<Item> getItems() {
			return items;
		}

		public void addItem(Item item) {
			items.add(item);
		}

		public int getSelected() {
			return selected;
		}

		public void setSelected(int selected) {
			this.selected = selected;
		}

		private int selected;
		private List<Item> items;

		public Category() {
			selected = 0;
			items = new ArrayList<Item>();
		}
	}

	public class Item {

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			if (image != null)
				this.image = image;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			if (title != null)
				this.title = title;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			if (value != null)
				this.value = value;
		}

		private String title;
		private String image;
		private String value;

		public Item() {
			title = "";
			image = "";
			value = "";
		}
	}

	public static List<Category> getMenu() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			Config ch = new Config();
			saxParser.parse(sony.xmb.Config.class.getResourceAsStream("/sony/cfg/menu.xml"), ch);
			return ch.getCategories();
		} catch (Exception ex) {
			return new ArrayList<Category>();
		}
	}

	public static Category getCategory(String title) {
		for (Iterator<Category> i$ = getMenu().iterator(); i$.hasNext();) {
			Category category = i$.next();
			if (category.getTitle().equalsIgnoreCase(title))
				return category;
		}

		return null;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("menu".equalsIgnoreCase(qName))
			categories = new ArrayList<Category>();
		if ("category".equalsIgnoreCase(qName)) {
			category = new Category();
			category.setTitle(attributes.getValue("title"));
			category.setImage(attributes.getValue("image"));
		}
		if ("item".equalsIgnoreCase(qName)) {
			item = new Item();
			item.setTitle(attributes.getValue("title"));
			item.setImage(attributes.getValue("image"));
			item.setValue(attributes.getValue("value"));
			category.addItem(item);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("category".equalsIgnoreCase(qName))
			categories.add(category);
	}

	public void characters(char ac[], int i, int j) throws SAXException {
	}

	private List<Category> getCategories() {
		return categories;
	}

	private Category category;
	private Item item;
	private List<Category> categories;
}
