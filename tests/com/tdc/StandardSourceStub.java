package com.tdc;

@SuppressWarnings("javadoc")
public class StandardSourceStub<T> implements Source<T>, PassiveSource<T> {
	private T thing = null;
	private Sink<T> sink;
	public boolean rejected = false;

	public StandardSourceStub(Sink<T> sink) {
		this.sink = sink;
	}

	public void receive(T thing) throws DisabledException, NoCashAvailableException, CashOverloadException {
		this.thing = thing;
		emit();
	}

	@Override
	public void emit() throws DisabledException, NoCashAvailableException, CashOverloadException {
		sink.receive(thing);
	}

	@Override
	public void reject(T cash) throws DisabledException, CashOverloadException {
		rejected = true;
	}
}
