package com.ruoyi.radius.tinyradius.dictionary;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

/**
 * The default dictionary is a singleton object containing
 * a dictionary in the memory that is filled on application
 * startup using the default dictionary file from the
 * classpath resource
 * <code>org.tinyradius.dictionary.default_dictionary</code>.
 */
@Component
public class DefaultDictionary
extends MemoryDictionary {

	/**
	 * Returns the singleton instance of this object.
	 * @return DefaultDictionary instance
	 */
	public static Dictionary getDefaultDictionary() {
		return instance;
	}
	
	/**
	 * Make constructor private so that a DefaultDictionary
	 * cannot be constructed by other classes. 
	 */
	private DefaultDictionary() {
	}
	// update by pwl 原来的属性文件加载失败 不知道哪个属性有问题 换下文件就可以了
	//private static final String DICTIONARY_RESOURCE = "radius_dictionary";
	private static final String DICTIONARY_RESOURCE = "default_dictionary";
	private static DefaultDictionary instance = null;
	
	/**
	 * Creates the singleton instance of this object
	 * and parses the classpath ressource.
	 */
	static {
		try {
			instance = new DefaultDictionary();
    		InputStream source = DefaultDictionary.class.getClassLoader().getResourceAsStream(DICTIONARY_RESOURCE);
			DictionaryParser.parseDictionary(source, instance);
		} catch (IOException e) {
			throw new RuntimeException("default dictionary unavailable", e);
		}
	}
	
}
