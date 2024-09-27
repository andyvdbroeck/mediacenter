package sony;

import java.io.File;

class Utils {

	public static String getJarFolder() {
		String name = Main.class.getName().replace('.', '/');
		String s = Main.class.getResource("/" + name + ".class").toString();
		s = s.replace('/', File.separatorChar);
		if (s.indexOf(".jar") != -1) {
			s = s.substring(0, s.indexOf(".jar") + 4);
		} else {
			s = s.substring(0, s.indexOf("target"));
		}
		if (s.lastIndexOf(':') != -1) {
			s = s.substring(s.lastIndexOf(':') - 1);
		}
		return s.substring(0, s.lastIndexOf(File.separatorChar) + 1).replace("%20", " ").replace("\\", "/");
	}

}
