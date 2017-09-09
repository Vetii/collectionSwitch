package de.heidelberg.pvs.diego.collections_online_adapter.optimizers.sets;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import de.heidelberg.pvs.diego.collections_online_adapter.context.impl.SetCollectionType;

public class SetPerformanceModel {
	
	private final SetCollectionType type;
	
	private final PolynomialFunction contains;
	private final PolynomialFunction populate;
	private final PolynomialFunction iterate;
	
	public SetPerformanceModel(SetCollectionType type, double[] contains, double[] populate, double[] iterate) {
		super();
		this.type = type;
		this.contains = new PolynomialFunction(contains);
		this.populate = new PolynomialFunction(populate);
		this.iterate = new PolynomialFunction(iterate);
	}
	
	public double calculatePerformance(int size, int nPopulate, int nContains, int cIterate) {
		
		return nPopulate * populate.value(size) + 
				nContains * contains.value(size) +
				cIterate * iterate.value(size);
	}
	
	public SetCollectionType getType() {
		return type;
	}
	
}