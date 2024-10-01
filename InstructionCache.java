
public class InstructionCache {
	
	private Word[] cache;
	private Word firstAddress;
	
	InstructionCache(){
		cache = new Word[8];
		for(int i = 0; i < 8; i++) {
			cache[i] = new Word(0);
		}
		firstAddress = new Word(1024);
	}
	
	public Word readCache(Word target) {
		if(target.isEqual(firstAddress)) {
			return getWord(0);
		} else {
			Word addressOffset = new Word(0);
			addressOffset.copy(firstAddress);
			for(int i = 1; i < 8; i++) {
				addressOffset.increment();
				if(target.isEqual(addressOffset)) {
					return getWord(i);
				}
			}
			return null;
			
		}
		
	}
	
	public Word getWord(int i) {
		return cache[i];
	}
	
	public Word getAddress() {
		return firstAddress;
	}
	
	public void setAddress(Word newWord) {
		firstAddress.copy(newWord);
	}
	
	public void setCache(Word newWord, int position) {
		cache[position].copy(newWord);
	}

}
