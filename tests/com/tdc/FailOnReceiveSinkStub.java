package com.tdc;

import static org.junit.Assert.fail;

@SuppressWarnings("javadoc")
public class FailOnReceiveSinkStub<T> implements Sink<T> {
	private boolean hasSpace;

	public FailOnReceiveSinkStub(boolean hasSpace) {
		this.hasSpace = hasSpace;
	}

	@Override
	public void receive(T thing) throws CashOverloadException, DisabledException {
		fail();
	}

	@Override
	public boolean hasSpace() {
		return hasSpace;
	}
}
