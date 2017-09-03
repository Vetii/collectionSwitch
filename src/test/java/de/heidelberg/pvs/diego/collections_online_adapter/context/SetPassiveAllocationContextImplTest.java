package de.heidelberg.pvs.diego.collections_online_adapter.context;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.heidelberg.pvs.diego.collections_online_adapter.context.impl.SetAllocationContextImpl;
import de.heidelberg.pvs.diego.collections_online_adapter.optimizers.sets.SetAllocationOptimizer;
import de.heidelberg.pvs.diego.collections_online_adapter.optimizers.sets.SetPassiveOptimizer;
import jlibs.core.lang.RuntimeUtil;

public class SetPassiveAllocationContextImplTest {
	
	private static final int SAMPLE = 1;
	private static final int WINDOW_SIZE = 10;
	private SetAllocationOptimizer optimizer;
	private SetAllocationContextImpl context;

	@Before
	public void setup() {
		
		optimizer = new SetPassiveOptimizer(WINDOW_SIZE);
		assertNotNull(optimizer);
		
		context = new SetAllocationContextImpl(optimizer, SAMPLE);
		assertNotNull(context);
		
		optimizer.setContext(context);
		
	}
	
	
	@Test
	public void sanityTest() throws Exception {

		// Optimizer
		SetAllocationOptimizer optimizer = new SetPassiveOptimizer(10);
		
		// Context
		SetAllocationContext context = new SetAllocationContextImpl(optimizer);
		Assert.assertNotNull(context);
		
		optimizer.setContext(context);
		
		Set<Integer> set = context.createSet();
		Assert.assertNotNull(set);
		
	}
	
	@Test
	public void testWarmupToAdaptive() throws Exception {
		
		int n = 7;
		for(int i = 0; i < WINDOW_SIZE * SAMPLE; i++) {
			
			Set<Integer> set = context.createSet();
			
			for(int j = 0; j < n; j++) {
				set.add(j);
			}
			
			set = null;
		}
		
		RuntimeUtil.gc();
		RuntimeUtil.gc();
		Thread.sleep(1000);
		
		AllocationContextState state = context.getAllocationContextState();
		Assert.assertEquals(AllocationContextState.ADAPTIVE, state);

		int analyzedSize = context.getAnalyzedSize();
		Assert.assertEquals(n, analyzedSize);
		
	}
	

}