package org.lamisplus.modules.report.utility;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Scrambler {

    private static final Map<Character, Character> charToSymbolMap = new HashMap<>();
	private static final Map<Character, Character> symbolToCharMap = new HashMap<>();
	private static final Map<Character, Character> numberToSymbolMap = new HashMap<>();
	private static final Map<Character, Character> symbolToNumberMap = new HashMap<>();


    static {
        charToSymbolMap.put('A', '$');
        charToSymbolMap.put('E', '@');
        charToSymbolMap.put('I', '#');
        charToSymbolMap.put('O', '%');
        charToSymbolMap.put('U', '^');
        charToSymbolMap.put('a', '&');
        charToSymbolMap.put('e', '*');
        charToSymbolMap.put('i', '=');
        charToSymbolMap.put('o', '[');
        charToSymbolMap.put('u', ']');
		charToSymbolMap.put('C', '{');
        charToSymbolMap.put('c', '}');
        charToSymbolMap.put('M', '|');
        charToSymbolMap.put('m', ';');
        charToSymbolMap.put('G', ':');
        charToSymbolMap.put('g', '<');
        charToSymbolMap.put('N', '>');
        charToSymbolMap.put('n', '?');
        charToSymbolMap.put('S', '~');
		charToSymbolMap.put('s', '\\');
        
		numberToSymbolMap.put('0', '{');
        numberToSymbolMap.put('1', '}');
        numberToSymbolMap.put('2', '|');
        numberToSymbolMap.put('3', ';');
        numberToSymbolMap.put('4', ':');
        numberToSymbolMap.put('5', '<');
        numberToSymbolMap.put('6', '>');
        numberToSymbolMap.put('7', '?');
        numberToSymbolMap.put('8', '~');
		numberToSymbolMap.put('9', '\\');
		
		// Create the reverse mapping
        for (Map.Entry<Character, Character> entry : charToSymbolMap.entrySet()) {
            symbolToCharMap.put(entry.getValue(), entry.getKey());
        }
		
		for (Map.Entry<Character, Character> entry : numberToSymbolMap.entrySet()) {
            symbolToNumberMap.put(entry.getValue(), entry.getKey());
        }
    }
	
    public String scrambleCharacters(String input) {
        StringBuilder scrambled = new StringBuilder();
        for (char ch : input.toCharArray()) {
            scrambled.append(charToSymbolMap.getOrDefault(ch, ch));
        }
        return scrambled.toString();
    }
	
    public String unscrambleCharacters(String scrambled) {
        StringBuilder unscrambled = new StringBuilder();
        for (char ch : scrambled.toCharArray()) {
           unscrambled.append(symbolToCharMap.getOrDefault(ch, ch));
        }
        return unscrambled.toString();
    }
	
    public String scrambleNumbers(String input) {
        StringBuilder scrambled = new StringBuilder();
        for (char ch : input.toCharArray()) {
            scrambled.append(numberToSymbolMap.getOrDefault(ch, ch));
        }
        return scrambled.toString();
    }
	
    public String unscrambleNumbers(String scrambled) {
        StringBuilder unscrambled = new StringBuilder();
        for (char ch : scrambled.toCharArray()) {
           unscrambled.append(symbolToNumberMap.getOrDefault(ch, ch));
        }
        return unscrambled.toString();
    }

}
