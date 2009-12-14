package org.integratedmodelling.modelling.data.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.clojure.ClojureInterpreter;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.NameGenerator;
import org.integratedmodelling.utils.Pair;

public class ClojureAccessor implements IStateAccessor {

	String clojureCode = null;
	int[] prmOrder = null;
	Object[] parameters;
	HashMap<IConcept, String> obsToName = new HashMap<IConcept, String>();
	ArrayList<Pair<String,Integer>> parmList = new ArrayList<Pair<String,Integer>>();
	boolean isMediator;
	String namespace = NameGenerator.newName("clj");
	
	public ClojureAccessor(String code, boolean isMediator) {
		
		/*
		 * TODO compile a proxy and eval once, then use that
		 */
		clojureCode = code;
		
		this.isMediator = isMediator;
	}

	@Override
	public Object getValue(Object[] registers) {
		
		HashMap<String, Object> parms = new HashMap<String, Object>();
		
		for (int i = 0; i < parmList.size(); i++) {
			parms.put(parmList.get(i).getFirst(),
					registers[parmList.get(i).getSecond()]);
		}
		
		try {
			return new ClojureInterpreter().evalRaw(clojureCode, namespace, parms);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	public boolean isConstant() {
		return false;
	}


	@Override
	public boolean notifyDependencyObservable(IObservation o,
			IConcept observable, String formalName) throws ThinklabException {
		// TODO Auto-generated method stub
		if (!(o instanceof Topology))
			obsToName.put(observable, formalName);
		return true;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation,
			IConcept observable, int register, IConcept stateType)
			throws ThinklabException {
		// TODO Auto-generated method stub
		if (!(observation instanceof Topology))
			parmList.add(new Pair<String, Integer>(obsToName.get(observable), register));
		
	}
	
	
}
