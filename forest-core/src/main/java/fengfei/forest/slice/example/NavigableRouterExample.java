package fengfei.forest.slice.example;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.impl.LongEqualizer;
import fengfei.forest.slice.impl.NavigableRouter;

public class NavigableRouterExample extends BaseRouterExample{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NavigableRouter<Long> router = new NavigableRouter<>();
		router.setSelectType(SelectType.Hash);
		router.setOverflowType(OverflowType.Last);
		router.setEqualizer(new LongEqualizer());
		setupGroup(router);
		//System.out.println(router);
		System.out.println(router.locate(101l));
		System.out.println(router.locate(2l));
		System.out.println(router.locate(2635l));
		System.out.println(router.locate(20000l));
		System.out.println(router.locate(12l, Function.Read));
		System.out.println(router.locate(2896l, Function.Read));
		System.out.println(router.locate(19l, Function.Read));
		System.out.println(router.locate(24642l, Function.Read));
		System.out.println(router.locate(9912l, Function.Write));
		System.out.println(router.locate(9720l, Function.Write));
		System.out.println(router.locate(9701l, Function.Write));
		System.out.println(router.locate(11000l, Function.Write));
	}

	private static void setupGroup(NavigableRouter<Long> router) {
		int ip = 2;
		int sliceSize = 10;
		for (int i = 0; i < sliceSize; i++) {
			Long sliceId = Long.valueOf((i + 1) * 1980);
			for (int j = 0; j < 6; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);

				SliceResource sliceResource = new SliceResource(resource);
				sliceResource.setFunction(j == 0 ? Function.Write : Function.Read);
				sliceResource.addParams(extraInfo(ip));
				router.register(sliceId, String.valueOf(i), sliceResource);
			}
		}
	}

 
}
