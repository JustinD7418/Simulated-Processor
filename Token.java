
public class Token {

	public enum tokenType {NUMBER, SEPARATOR, REGISTER, MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, 
		HALT, BRANCH, JUMP, CALL, PUSH, LOAD, RETURN, STORE, PEEK, POP, INTERRUPT, EQUAL, UNEQUAL, GREATER, LESS, 
		GREATEROREQUAL, LESSOREQUAL, SHIFT, LEFT, RIGHT};
	private tokenType token;
	private String value;
	private int lineNum;
	private int position;
	
	Token(tokenType type, int ln, int pos, String val){
		token = type;
		lineNum = ln;
		position = pos;
		value = val;
	}
	
	Token(tokenType type, int ln, int pos){
		token = type;
		lineNum = ln;
		position = pos;
	}
	
	tokenType getTokenType() {
		if (token == null){
			return null;
		}
		return token;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		if (token == tokenType.NUMBER || token == tokenType.REGISTER) {
			return token + "(" + value + ")";
		}
		return token + "";
	}
	
}
