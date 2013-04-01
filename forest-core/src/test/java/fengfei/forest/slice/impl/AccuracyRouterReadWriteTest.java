package fengfei.forest.slice.impl;

import org.junit.Before;

import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;

public class AccuracyRouterReadWriteTest extends AccuracyRouterEqualityTest {

//	AccuracyRouter<Long> router = new AccuracyRouter<>();
	int size = 2;

	@Before
	public void setUp() throws Exception {
		isReadWrite = false;
		int ip = 2;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);
				//
				Long sliceId = Long.valueOf(i);
				SliceResource sliceResource = new SliceResource(sliceId, resource);
				sliceResource.addParams(extraInfo("192.168.1." + ip, 8002));
				if (j == 0) {
					sliceResource.setFunction(Function.Write);
				} else {
					sliceResource.setFunction(Function.Read);
				}
				router.register(sliceId, String.valueOf(i), sliceResource);
			}
			//System.out.println(router);
		}
//		setRouter(router);
	}
}
