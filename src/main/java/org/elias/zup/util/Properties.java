package org.elias.zup.util;

import java.io.IOException;
import java.io.InputStream;

public class Properties {

	private static java.util.Properties properties;

	static {
		//usei essa estrategia apenas para simplificar, pois o ideal é
		//o arquivo de propriedades estar fora da aplicação
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("db.properties");
		properties = new java.util.Properties();
		try {
			properties.load(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static String getStringProperty(String name) {
		return properties.getProperty(name);
	}
	
	public static Integer getIntegerProperty(String name) {
		return Integer.parseInt(properties.getProperty(name));
	}
}
