package dd.kms.maxmlian.impl;

import java.util.ArrayList;
import java.util.List;

class ReusableElementsCollection<T>
{
	private final List<T>	data					= new ArrayList<>();
	private int				numNonReusableElements;

	T pollReusableElement() {
		if (numNonReusableElements == data.size()) {
			return null;
		}
		return data.get(numNonReusableElements++);
	}

	T addElementForLaterReuse(T element) {
		assert numNonReusableElements == data.size() : "This method is meant to be called if no reusable element is available anymore";
		data.add(element);
		numNonReusableElements++;
		return element;
	}

	void makeElementsReusable() {
		numNonReusableElements = 0;
	}
}
