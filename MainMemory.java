
public class MainMemory {
	
	public static Word[] memory;
	
	MainMemory() {
		memory = new Word[1024];
	}
	
	public static Word read(Word address) throws Exception {
		if(memory == null) {
			throw new Exception("Memory not Found");
		}
		if(address.getUnsigned() >= 1024) {
			throw new Exception("Address out of bounds");
		}
		if(memory[(int) address.getUnsigned()] == null) {
			memory[(int) address.getUnsigned()] = new Word(0);
		}
		return memory[(int) address.getUnsigned()];
	}
	
	public static void write(Word address, Word value) throws Exception {
		if(memory == null) {
			throw new Exception("Memory not found");
		}
		if(address.getUnsigned() >= 1024) {
			throw new Exception("Address out of bounds");
		}
		int memIndex = (int) address.getUnsigned();
		
		if(memory[memIndex] == null) {
			memory[memIndex] = new Word(0);
		}
		
		memory[memIndex] = new Word((int) value.getUnsigned());
	}
	
	public static void load(String[] data) throws Exception {
		if(memory == null) {
			throw new Exception("Memory not found");
		}
		
		int length = data.length;
		String bitValue = "";
		Bit newBit;
		for(int i = 0; i < length; i++) {
			if(memory[i] == null) {
				memory[i] = new Word(0);
			}
			if (data[i] == null) {
				data[i] = "00000000000000000000000000000000";
			}
			for(int j = data[i].length() - 1; j >= 0; j--) {
				newBit = new Bit();
				bitValue = data[i].substring(j, j+1);
				if(bitValue.equals("0")) {
					newBit = new Bit();
				} else if (bitValue.equals("1")) {
					newBit = new Bit(true);
				} else {
					throw new Exception("Invalid character");
				}
				memory[i].setBit(data[i].length() + (31 - data[i].length()) - (data[i].length() - (j+1)), newBit);
			}
		}
	}
	
	public static void clear() throws Exception {
		memory = new Word[1024];
	}

	
}
