package ch.obermuhlner.libgdx.planetbrowser.util;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class DisposableContainer implements Disposable {

	private final Array<Disposable> disposables = new Array<Disposable>();
	
	public void add(Disposable disposable) {
		disposables.add(disposable);
	}
	
	@Override
	public void dispose() {
		for (int i = 0; i < disposables.size; i++) {
			disposables.get(i).dispose();
		}
		disposables.clear();
	}

}
