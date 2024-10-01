
public class StringHandler {

	private String document;
	private int index = 0;
	
	StringHandler(String s){
		document = s;
	}
	
	char peek(int i) {//Looks ahead i amount of bytes and returns that byte
		String foundString = "";
		if (this.isDone() == false) {
			foundString = document.substring(index + i,index + i + 1);
			char foundChar = foundString.charAt(0);
			return foundChar;
		}
		return ' ';
	}
	
	String peekString(int i) {//Returns a string of the current byte to i amount of bytes ahead
		String foundString = "";
		if(index + i + 1 >= document.length()) {
			foundString = document.substring(index, document.length());
		} else {
			foundString = document.substring(index, index + i + 1);
		}		
		return foundString;
		
	}
	
	char getChar() {//Returns the current char and moves one byte ahead
		String foundString = document.substring(index, index + 1);
		char foundChar = foundString.charAt(0);
		index += 1;
		return foundChar;
		
	}
	
	void swallow(int i) {//Passes over the current byte
		index = index + i;
	}
	
	boolean isDone() {//Checks if the current byte is the end of the file
		String currentChar = document.substring(index);
		if (currentChar == "") {
			return true;
		} else {
			return false;
		}
	}
	
	String remainder() {//Returns the rest of the file from the current index
		return document.substring(index);
	}
	
	
}
