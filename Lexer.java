import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {

	private StringHandler handler;
	private int lineNum = 1;
	private int position = 0;
	private char currentChar = ' ';//Used to hold the current character we are on if needed
	private HashMap<String,Token.tokenType> keyWords;
	
	Lexer(String s){	
		handler = new StringHandler(s);
		keyWords = fillKeyWordHashMap();
	}
	
	LinkedList<Token> lex() throws Exception{
		LinkedList<Token> tokenList = new LinkedList<Token>();

		while (handler.isDone() == false) {
			//System.out.println(handler.peek(0));
			currentChar = handler.peek(0);//Checks what the current character is
			if (Character.isDigit(currentChar)) {//If the next character is a number
				tokenList.add(processDigits());//Add a number token
			} else if (Character.isLetter(currentChar)) {//If the next character is a letter
				tokenList.add(processWord());//Add a word Token
			} else if (currentChar == '\n') {//If there is a new line
				tokenList.add(new Token(Token.tokenType.SEPARATOR,lineNum,position));//Add a separator token
				lineNum += 1;
				position = 0;
				handler.swallow(1);
			} else if (currentChar == ' ' || currentChar == '\r') {//If the next character is a space or tab
				handler.swallow(1);//Increment the handler by 1
				position += 1;
			} else if (currentChar ==  '	' ) {//If a tab is found
				handler.swallow(4);
				position += 4;
			} else {//If a not-recognized character is present
				throw new Exception("Unrecognized character");
			}
		}	
		return tokenList;	
	}
	
	Token processDigits() throws Exception{
		String currentString = "";
		int startPos = position;
		while (handler.isDone() == false && Character.isDigit(handler.peek(0))) {
			currentChar = handler.getChar();//Gets the current character
			currentString = currentString + currentChar;//Increments the character to a string
			position += 1;
		}
		Token newToken = new Token(Token.tokenType.NUMBER, lineNum, startPos, currentString);
		position += 1;//Makes sure the handler "leaves the number when done
		return newToken;
	}
	
	Token processWord() throws Exception {
		String currentString = "";
		int startPos = position;
		if(handler.peek(0) == 'R' && Character.isDigit(handler.peek(1))) {//Checks if the word is a register
			return processRegister();
		}
		while (handler.isDone() == false && Character.isLetter(handler.peek(0))) {
			currentChar = handler.getChar();
			position += 1;
			currentString = currentString + currentChar;
			//System.out.println(currentString);
		}
		String currentStringKeyTester = currentString.toUpperCase();
		if (keyWords.containsKey(currentStringKeyTester)) {
			Token newToken = new Token(keyWords.get(currentStringKeyTester), lineNum, startPos);	
			position += 1;
			return newToken;
		}
		throw new Exception("Invalid Word Type");
	}
	
	Token processRegister() {
		String currentString = "";
		int startPos = position;
		handler.swallow(1);//Do not include the R
		position += 1;
		while (handler.isDone() == false && Character.isDigit(handler.peek(0))) {
			currentChar = handler.getChar();
			position += 1;
			currentString = currentString + currentChar;
			//System.out.println(currentString);
		}
		Token newToken = new Token(Token.tokenType.REGISTER, lineNum, startPos, currentString);
		position += 1;
		return newToken;
	}
	
	
	private HashMap<String,Token.tokenType> fillKeyWordHashMap() {
		HashMap<String,Token.tokenType> map = new HashMap<String,Token.tokenType>();
		map.put("MATH", Token.tokenType.MATH);
		map.put("ADD", Token.tokenType.ADD);
		map.put("SUBTRACT", Token.tokenType.SUBTRACT);
		map.put("MULTIPLY", Token.tokenType.MULTIPLY);
		map.put("AND", Token.tokenType.AND);
		map.put("OR", Token.tokenType.OR);
		map.put("NOT", Token.tokenType.NOT);
		map.put("XOR", Token.tokenType.XOR);
		map.put("COPY", Token.tokenType.COPY);
		map.put("HALT", Token.tokenType.HALT);
		map.put("BRANCH", Token.tokenType.BRANCH);
		map.put("JUMP", Token.tokenType.JUMP);
		map.put("CALL", Token.tokenType.CALL);
		map.put("PUSH", Token.tokenType.PUSH);
		map.put("LOAD", Token.tokenType.LOAD);
		map.put("RETURN", Token.tokenType.RETURN);
		map.put("STORE", Token.tokenType.STORE);
		map.put("PEEK", Token.tokenType.PEEK);
		map.put("POP", Token.tokenType.POP);
		map.put("INTERRUPT", Token.tokenType.INTERRUPT);
		map.put("EQUAL", Token.tokenType.EQUAL);
		map.put("UNEQUAL", Token.tokenType.UNEQUAL);
		map.put("GREATER", Token.tokenType.GREATER);
		map.put("LESS", Token.tokenType.LESS);
		map.put("GREATEROREQUAL", Token.tokenType.GREATEROREQUAL);
		map.put("LESSOREQUAL", Token.tokenType.LESSOREQUAL);
		map.put("SHIFT", Token.tokenType.SHIFT);
		map.put("LEFT", Token.tokenType.LEFT);
		map.put("RIGHT", Token.tokenType.RIGHT);
		return map;
	}
	
}
