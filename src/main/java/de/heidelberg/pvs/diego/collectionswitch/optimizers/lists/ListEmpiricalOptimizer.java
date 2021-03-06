package de.heidelberg.pvs.diego.collectionswitch.optimizers.lists;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.collections.api.block.predicate.primitive.ObjectDoublePredicate;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;

import de.heidelberg.pvs.diego.collectionswitch.context.CollectionTypeEnum;
import de.heidelberg.pvs.diego.collectionswitch.context.ListAllocationContext;
import de.heidelberg.pvs.diego.collectionswitch.context.ListCollectionType;
import de.heidelberg.pvs.diego.collectionswitch.context.SetCollectionType;
import de.heidelberg.pvs.diego.collectionswitch.manager.PerformanceGoal;
import de.heidelberg.pvs.diego.collectionswitch.manager.PerformanceGoal.PerformanceDimension;
import de.heidelberg.pvs.diego.collectionswitch.monitors.lists.ListActiveFullMonitor;
import de.heidelberg.pvs.diego.collectionswitch.monitors.lists.ListMetrics;
import de.heidelberg.pvs.diego.collectionswitch.optimizers.sets.SetEmpiricalPerformanceEvaluator;

public class ListEmpiricalOptimizer implements ListAllocationOptimizer {

	private List<ListMetrics> collectionsState;

	private ListAllocationContext context;
	private ListCollectionType defaultType;

	private int finishedRatio;

	private ListEmpiricalPerformanceEvaluator evaluator;
	private PerformanceGoal goal;

	public ListEmpiricalOptimizer(ListEmpiricalPerformanceEvaluator evaluator, ListCollectionType defaultType, PerformanceGoal goal,
			int windowSize, double finishedRatio) {
		this.collectionsState = new ArrayList<ListMetrics>(windowSize);
		this.defaultType = defaultType;
		this.evaluator = evaluator;
		this.goal = goal;
		
		if (finishedRatio == 0.0) {
			this.finishedRatio = 0;
		} else if(finishedRatio > 1) {
			this.finishedRatio = windowSize;
		} else {
			this.finishedRatio = (int) (windowSize / finishedRatio);

		}

	}

	@Override
	public <E> List<E> createMonitor(List<E> list) {
		ListMetrics state = new ListMetrics(new WeakReference<List<E>>(list));
		collectionsState.add(state);
		return new ListActiveFullMonitor<E>(list, state);
	}

	@SuppressWarnings("serial")
	@Override
	public void analyzeAndOptimize() {

		int amountFinishedCollections = 0;
		for (ListMetrics metric : collectionsState) {
			if (metric.hasCollectionFinished())
				amountFinishedCollections++;
		}

		// Only analyze it when
		if (amountFinishedCollections >= finishedRatio) {

			// Get candidates from the major performance goal
			MutableObjectDoubleMap<ListCollectionType> majorCandidates = getCandidates(
					goal.majorDimension, goal.minImprovement);
			
			MutableObjectDoubleMap<ListCollectionType> bestOptions;

			// FIXME: This should be implemented in a better way
			if(goal.maxPenalty > 0) {
			
				// Get candidates that fulfill the minor performance goal
				MutableObjectDoubleMap<ListCollectionType> minorCandidates = getCandidates(
						goal.minorDimension, goal.maxPenalty);
	
				bestOptions = majorCandidates
						.select(new ObjectDoublePredicate<ListCollectionType>() {
							@Override
							public boolean accept(ListCollectionType key, double value) {
								return minorCandidates.containsKey(key);
							}
						});
			
			} else {
				bestOptions = majorCandidates;	
			}
			
			// Get the top implementation - Finding the minimum value
			// FIXME: Find a better implementation for this
			double min = Double.MAX_VALUE;
			ListCollectionType champion = defaultType;
			for (ListCollectionType type : bestOptions.keySet()) {
				double perf = bestOptions.get(type);
				if (perf < min) {
					champion = type;
					min = perf;
				}
			}

			context.updateCollectionType(champion);

			// Reset
			collectionsState.clear();
		}

	}

	private MutableObjectDoubleMap<ListCollectionType> getCandidates(PerformanceDimension performanceDimension,
			double factor) {

		// Gets the performance prediction for each instance
		MutableObjectDoubleMap<ListCollectionType> majorPerformance = evaluator.predictPerformance(collectionsState,
				performanceDimension);

		// Gets the default performance
		double defaultPerformance = majorPerformance.get(defaultType);

		// Selects only the implementations with better performance
		@SuppressWarnings("serial")
		MutableObjectDoubleMap<ListCollectionType> candidates = majorPerformance
				.select(new ObjectDoublePredicate<ListCollectionType>() {
					@Override
					public boolean accept(ListCollectionType object, double value) {
						return defaultPerformance / value > factor;
					}
				});

		return candidates;
	}

	@Override
	public void setContext(ListAllocationContext context) {
		this.context = context;

	}

}
