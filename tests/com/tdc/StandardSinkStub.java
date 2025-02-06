package com.tdc;

@SuppressWarnings("javadoc")
public class StandardSinkStub<T> implements Sink<T> {
	private boolean hasSpace;

	public StandardSinkStub(boolean hasSpace) {
		this.hasSpace = hasSpace;
	}

	@Override
	public void receive(T thing) throws CashOverloadException, DisabledException {}

	@Override
	public boolean hasSpace() {
		return hasSpace;
	}
}
