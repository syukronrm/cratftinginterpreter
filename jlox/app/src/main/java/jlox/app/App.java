/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jlox.app;

import jlox.list.LinkedList;

import static jlox.utilities.StringUtils.join;
import static jlox.utilities.StringUtils.split;
import static jlox.app.MessageUtils.getMessage;

import org.apache.commons.text.WordUtils;

public class App {
    public static void main(String[] args) {
        LinkedList tokens;
        tokens = split(getMessage());
        String result = join(tokens);
        System.out.println(WordUtils.capitalize(result));
    }
}
