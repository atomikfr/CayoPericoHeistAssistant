package jmodmenu.memory;

public abstract class RefPtr {

	protected Ptr base;
	
	public RefPtr(Ptr base) {
		this.base = base.copy();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + base;
	}
	
}
