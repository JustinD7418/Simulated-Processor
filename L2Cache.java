
public class L2Cache {
	
	private Word[] cache1;
	private Word firstAddress1;
	private Word[] cache2;
	private Word firstAddress2;
	private Word[] cache3;
	private Word firstAddress3;
	private Word[] cache4;
	private Word firstAddress4;
	private Word[] hitBlock;
	private Word hitAddress;

	L2Cache(){
		cache1 = new Word[8];
		for(int i = 0; i < 8; i++) {
			cache1[i] = new Word(0);
		}
		firstAddress1 = new Word(1024);
		
		cache2 = new Word[8];
		for(int i = 0; i < 8; i++) {
			cache2[i] = new Word(0);
		}
		firstAddress2 = new Word(1024);
		
		cache3 = new Word[8];
		for(int i = 0; i < 8; i++) {
			cache3[i] = new Word(0);
		}
		firstAddress3 = new Word(1024);
		
		cache4 = new Word[8];
		for(int i = 0; i < 8; i++) {
			cache4[i] = new Word(0);
		}
		firstAddress4 = new Word(1024);
		hitBlock = new Word[8];
		for(int i = 0; i < 8; i++) {
			hitBlock[i] = new Word(0);
		}
		hitAddress = new Word(0);
	}
	
	public Word readCache(Word target) throws Exception {
		if(target.isEqual(firstAddress1)) {
			return getWord(1, 0);
		} else {
			Word addressOffset = new Word(0);
			addressOffset.copy(firstAddress1);
			for(int i = 1; i < 8; i++) {
				addressOffset.increment();
				if(target.isEqual(addressOffset)) {
					hitBlock = cache1;
					hitAddress = firstAddress1;
					return getWord(1, i);
				}
			}
			
			addressOffset.increment();
			
			if(target.isEqual(firstAddress2)) {
				return getWord(2, 0);
			} else {
				for(int i = 1; i < 8; i++) {
					addressOffset.increment();
					if(target.isEqual(addressOffset)) {
						hitBlock = cache2;
						hitAddress = firstAddress2;
						return getWord(2, i);
					}
				}
			}
			
			addressOffset.increment();
			
			if(target.isEqual(firstAddress3)) {
				return getWord(3, 0);
			} else {
				for(int i = 1; i < 8; i++) {
					addressOffset.increment();
					if(target.isEqual(addressOffset)) {
						hitBlock = cache3;
						hitAddress = firstAddress3;
						return getWord(3, i);
					}
				}
			}
			
			addressOffset.increment();
			
			if(target.isEqual(firstAddress4)) {
				return getWord(4, 0);
			} else {
				for(int i = 1; i < 8; i++) {
					addressOffset.increment();
					if(target.isEqual(addressOffset)) {
						hitBlock = cache4;
						hitAddress = firstAddress4;
						return getWord(4, i);
					}
				}
			}
			return null;
		}
	}
	
	public Word getWord(int cacheNum, int i) throws Exception {
		if(cacheNum == 1) {
			return cache1[i];
		} else if(cacheNum == 2){
			return cache2[i];
		} else if(cacheNum == 3) {
			return cache3[i];
		} else if(cacheNum == 4) {
			return cache4[i];
		} else {
			throw new Exception("Invalid cache number");
		}
	}
	
	public Word getAddress(int cacheNum) throws Exception {
		if(cacheNum == 1) {
			return firstAddress1;
		} else if(cacheNum == 2){
			return firstAddress2;
		} else if(cacheNum == 3) {
			return firstAddress3;
		} else if(cacheNum == 4) {
			return firstAddress4;
		} else {
			throw new Exception("Invalid cache number");
		}
	}
	
	public void setAddress(int cacheNum, Word newWord) throws Exception {
		if(cacheNum == 1) {
			firstAddress1.copy(newWord);
		} else if(cacheNum == 2){
			firstAddress2.copy(newWord);
		} else if(cacheNum == 3) {
			firstAddress3.copy(newWord);
		} else if(cacheNum == 4) {
			firstAddress4.copy(newWord);
		} else {
			throw new Exception("Invalid cache number");
		}
	}
	
	public void setCache(int cacheNum, Word newWord, int position) throws Exception {
		if(cacheNum == 1) {
			cache1[position].copy(newWord);
		} else if(cacheNum == 2){
			cache2[position].copy(newWord);
		} else if(cacheNum == 3) {
			cache3[position].copy(newWord);
		} else if(cacheNum == 4) {
			cache4[position].copy(newWord);
		} else {
			throw new Exception("Invalid cache number");
		}
	}
	
	public Word[] getHitBlock() {
		return hitBlock;
	}
	
	public Word getHitAddress() {
		return hitAddress;
	}
	
}
