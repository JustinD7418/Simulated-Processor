import java.util.LinkedList;
import java.util.Optional;

public class TokenHandler {

	private LinkedList<Token> tokenList;
	
	public TokenHandler(LinkedList<Token> tokens) {
		 tokenList = tokens;//Accepts a list of tokens
	}
	
	Token peek(int i) {
		Token foundToken = null;
		if(moreTokens() == true && i < tokenList.size()) {//Only peeks if there are still tokens in the list
			foundToken = tokenList.get(i);
		}
		return foundToken;
	}
	
	boolean moreTokens() {
		if(tokenList == null || tokenList.size() == 0) {//Checks if the current token does not exist
			return false;
		} else {
			return true;
		}
	}
	
	Optional<Object> matchAndRemove(Token.tokenType type) {
		if(moreTokens() == true) {
			if(tokenList.get(0).getTokenType() == type) {
				Token foundToken = tokenList.removeFirst();
				return Optional.of(foundToken);
			}
		}
		return Optional.empty();
	}
	
}
