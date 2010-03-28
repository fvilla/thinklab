package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Measurement;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.modelling.data.adapters.MVELAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

import clojure.lang.IFn;

@InstanceImplementation(concept="modeltypes:DynamicMeasurement")
public class DynamicMeasurement extends Measurement {

	public Object code = null;
	String lang = "clojure";
	
	@Override
	public IStateAccessor getAccessor() {
		if (lang.equals("clojure"))
			return new ClojureAccessor((IFn)code, false);
		else
			return new MVELAccessor((String)code, false);
	}


	@Override
	public IStateAccessor getMediator(IndirectObservation observation)
			throws ThinklabException {
		if (lang.equals("clojure"))
			return new ClojureAccessor((IFn)code, true);
		else
			return new MVELAccessor((String)code, true);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Measurement#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		
		super.initialize(i);
		IValue cd = i.get("modeltypes:hasStateFunction");
		if (cd != null) {
			this.code = cd.toString();
			this.lang = "MVEL";
		}
		IValue lng = i.get("modeltypes:hasExpressionLanguage");
		if (lng != null)
			this.lang = lng.toString().toLowerCase();
	}

}
