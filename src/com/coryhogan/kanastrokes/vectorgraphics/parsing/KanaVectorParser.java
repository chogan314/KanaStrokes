package com.coryhogan.kanastrokes.vectorgraphics.parsing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.coryhogan.kanastrokes.math.Vec2;
import com.coryhogan.kanastrokes.vectorgraphics.parsing.KanaPathComponentParseNode.ComponentType;

public class KanaVectorParser {
	private interface CharTester {
		public boolean test(char c);
	}
	
	private int pos;
	private String input;
	
	private KanaVectorParser(String input) {
		pos = 0;
		this.input = input;
	}
	
	private char nextCharacter() {
		return input.charAt(pos);
	}
	
	private boolean startsWith(String prefix) {
		return input.substring(pos).startsWith(prefix);
	}
	
	private boolean eof() {
		return pos >= input.length();
	}
	
	private char consumeCharacter() {
		return input.charAt(pos++);
	}
	
	private String consumeCharacters(int num) {
		char[] chars = new char[num];
		for (int i = 0; i < num; i++) {
			chars[i] = consumeCharacter();
		}
		return String.valueOf(chars);
	}
	
	private String consumeWhile(CharTester tester) {
		StringBuilder builder = new StringBuilder();
		
		while(!eof() && tester.test(nextCharacter())) {
			builder.append(consumeCharacter());
		}
		
		return builder.toString();
	}
	
	private void consumeWhitespace() {
		consumeWhile(new CharTester() {
			@Override
			public boolean test(char c) {
				Pattern pattern = Pattern.compile("\\s");
				Matcher matcher = pattern.matcher(String.valueOf(c));
				return matcher.matches();
			}
		});
	}
	
	private KanaVectorParseNode parseKanaVector() {
		KanaVectorParseNode vectorNode = new KanaVectorParseNode();
		
		consumeWhitespace();
		
		if (!consumeCharacters(6).equalsIgnoreCase("<kana>")) {
			throw new IllegalStateException("Document must have format <kana>...</kana>");
		}
		
		while(true) {
			consumeWhitespace();
			if (eof() || startsWith("</kana>")) {
				break;
			}
			vectorNode.appendPath(parseKanaPath());
		}
		
		return vectorNode;
	}
	
	private KanaPathParseNode parseKanaPath() {
		KanaPathParseNode pathNode = new KanaPathParseNode();
		Vec2 cursor = new Vec2();
		char lastPathCommand = ' ';
		
		if (!consumeCharacters(5).equalsIgnoreCase("<path")) {
			throw new IllegalStateException("Path must have format <path.../>");
		}
		
		consumeWhitespace();
		
		if (consumeCharacter() != 'd') {
			throw new IllegalStateException("Path data must have format d=\"...\"");
		}
		
		consumeWhitespace();
		
		if (consumeCharacter() != '=') {
			throw new IllegalStateException("Path data must have format d=\"...\"");
		}
		
		consumeWhitespace();
		
		if (consumeCharacter() != '"') {
			throw new IllegalStateException("Path data must have format d=\"...\"");
		}
		
		while(true) {
			consumeWhitespace();
			
			if (nextCharacter() == '"') {
				consumeCharacter();
				break;
			}
			
			if (!Character.isDigit(nextCharacter()) && nextCharacter() != '.') {
				lastPathCommand = parsePathCommand();
				consumeWhitespace();
			}
			
			KanaPathComponentParseNode kanaParseComponent = parseKanaPathComponent(lastPathCommand, cursor);
			
			if (kanaParseComponent != null) {
				pathNode.appendComponent(kanaParseComponent);
			}
		}
		
		consumeWhitespace();
		
		if (!consumeCharacters(2).equals("/>")) {
			throw new IllegalStateException("Path must have format <path.../>");
		}
		
		return pathNode;
	}
	
	private char parsePathCommand() {
		char command = consumeCharacter();
		String cmdString = String.valueOf(command);
		Pattern pattern = Pattern.compile("[MmZzLlHhVvCcQq]");
		Matcher matcher = pattern.matcher(cmdString);
		if (!matcher.matches()) {
			throw new IllegalStateException("Invalid command. Valid commands: M m L l C c Q q");
		}
		return command;
	}
	
	private KanaPathComponentParseNode parseKanaPathComponent(char lastPathCommand, Vec2 cursor) {
		switch (lastPathCommand) {
		case ('M'):
			return parseKanaPathMove(cursor, false);
		case ('m'):
			return parseKanaPathMove(cursor, true);
		case ('L'):
			return parseKanaPathLine(cursor, false);
		case ('l'):
			return parseKanaPathLine(cursor, true);
		case ('Q'):
			return parseKanaPathQuadraticCurve(cursor, false);
		case ('q'):
			return parseKanaPathQuadraticCurve(cursor, true);
		case ('C'):
			return parseKanaPathCubicCurve(cursor, false);
		case ('c'):
			return parseKanaPathCubicCurve(cursor, true);
		}
		
		return null;
	}
	
	private KanaPathComponentParseNode parseKanaPathMove(Vec2 cursor, boolean relative) {
		consumeWhitespace();
		Vec2 dest = parseVertex();
		
		if (relative) {
			cursor.add(dest);
		} else {
			cursor.set(dest);
		}
		
		return null;
	}
	
	private KanaPathComponentParseNode parseKanaPathLine(Vec2 cursor, boolean relative) {
		KanaPathComponentParseNode node = new KanaPathComponentParseNode(ComponentType.LINE);
		
		consumeWhitespace();		
		Vec2 stop = parseVertex();
		
		if (relative) {
			stop.add(cursor);
		}
		
		node.appendVertex(cursor);
		node.appendVertex(stop);
		
		cursor.set(stop);
		
		return node;
	}
	
	private KanaPathComponentParseNode parseKanaPathQuadraticCurve(Vec2 cursor, boolean relative) {
		KanaPathComponentParseNode node = new KanaPathComponentParseNode(ComponentType.QUADRATIC_CURVE);
		
		consumeWhitespace();
		Vec2 control = parseVertex();
		consumeWhitespace();
		Vec2 stop = parseVertex();
		
		if (relative) {
			control.add(cursor);
			stop.add(cursor);
		}
		
		node.appendVertex(cursor);
		node.appendVertex(control);
		node.appendVertex(stop);
		
		cursor.set(stop);
		
		return node;
	}
	
	private KanaPathComponentParseNode parseKanaPathCubicCurve(Vec2 cursor, boolean relative) {
		KanaPathComponentParseNode node = new KanaPathComponentParseNode(ComponentType.CUBIC_CURVE);
		
		consumeWhitespace();
		Vec2 control0 = parseVertex();
		consumeWhitespace();
		Vec2 control1 = parseVertex();
		consumeWhitespace();
		Vec2 stop = parseVertex();
		
		if (relative) {
			control0.add(cursor);
			control1.add(cursor);
			stop.add(cursor);
		}
		
		node.appendVertex(cursor);
		node.appendVertex(control0);
		node.appendVertex(control1);
		node.appendVertex(stop);
		
		cursor.set(stop);
		
		return node;
	}
	
	private Vec2 parseVertex() {
		CharTester tester = new CharTester() {
			@Override
			public boolean test(char c) {
				return Character.isDigit(c) || c == '.' || c == '-';
			}
		};
		
		String num = consumeWhile(tester);
		float	x = Float.valueOf(num);
		
		consumeWhitespace();
		if (nextCharacter() == ',') {
			consumeCharacter();
			consumeWhitespace();
		}
		
		num = consumeWhile(tester);
		float y = Float.valueOf(num);
		
		return new Vec2(x, y);
	}
	
	public static KanaVectorParseNode ParseKanaVector(String source) {
		return new KanaVectorParser(source).parseKanaVector();
	}
}






















