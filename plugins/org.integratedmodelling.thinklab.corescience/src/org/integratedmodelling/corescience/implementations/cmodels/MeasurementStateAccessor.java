package org.integratedmodelling.corescience.implementations.cmodels;

import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.literals.IRandomValue;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public class MeasurementStateAccessor implements IStateAccessor {

	private boolean isConstant = false;
	private double value = 0.0;
	private int index = 0;
	private IDataSource<?> ds = null;
	private IRandomValue inlineRandom = null;

	public MeasurementStateAccessor(double value) {
		this.isConstant = true;
		this.value = value;
	}
	
	public MeasurementStateAccessor(IDataSource<?> src) {
		this.ds = src;
	}
	
	public MeasurementStateAccessor(IRandomValue inlineRandom) {
		this.inlineRandom  = inlineRandom;
		isConstant = true;
	}

	@Override
	public boolean notifyDependencyObservable(IObservation o, IConcept observable, String formalName)
			throws ThinklabException {
		// we don't need anything
		return false;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation, IConcept observable,
			int register, IConcept stateType) throws ThinklabException {
		// won't be called
	}

	@Override
	public Object getValue(Object[] registers) {
		return isConstant ? (inlineRandom == null ? value : inlineRandom) : getNextValue(registers);
	}

	private Object getNextValue(Object[] registers) {
		return ds.getValue(index++, registers);
	}

	@Override
	public boolean isConstant() {
		return isConstant;
	}
	
	@Override
	public String toString() {
		return "[MeasurementAccessor]";
	}

}
