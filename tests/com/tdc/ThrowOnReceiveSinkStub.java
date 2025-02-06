package com.tdc;

@SuppressWarnings("javadoc")
public class ThrowOnReceiveSinkStub<T> implements Sink<T> {
	private boolean hasSpace;

	public ThrowOnReceiveSinkStub(boolean hasSpace) {
		this.hasSpace = hasSpace;
	}

	@Override
	public void receive(T thing) throws CashOverloadException, DisabledException {
		throw new CashOverloadException();
	}

	@Override
	public boolean hasSpace() {
		return hasSpace;
	}
}
