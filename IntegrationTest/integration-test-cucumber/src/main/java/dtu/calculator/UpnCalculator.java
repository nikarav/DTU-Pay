package dtu.calculator;

import java.util.ArrayList;
import java.util.List;

public class UpnCalculator {
	
	List<Integer> stack = new ArrayList<>();

	public void input(Integer int1) {
		stack.add(int1);
		
	}

	public void add() {
		stack.add(stack.get(stack.size()-1) + stack.get(stack.size()-2));
		
	}

	public int display() {
		return stack.get(stack.size() -1);
	}

}
