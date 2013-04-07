package fengfei.forest.slice.example;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.equalizer.LongEqualizer;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.plotter.HashPlotter;

public class AccuracyRouterExample extends BaseRouterExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccuracyRouter<Long> router = new AccuracyRouter<>(new LongEqualizer());
		setupGroup(router);
		router.setPlotter(new HashPlotter());
		router.setOverflowType(OverflowType.Last);
		// //System.out.println(router);
		//
		System.out.println(router.locate(1l));
		System.out.println(router.locate(2l));
		System.out.println(router.locate(1l));
		System.out.println(router.locate(2l));
		System.out.println(router.locate(1l, Function.Read));
		System.out.println(router.locate(2l, Function.Read));
		System.out.println(router.locate(1l, Function.Read));
		System.out.println(router.locate(2l, Function.Read));
		System.out.println(router.locate(1l, Function.Write));
		System.out.println(router.locate(2l, Function.Write));
		System.out.println(router.locate(11l, Function.Write));
		System.out.println(router.locate(11l, Function.Write));
	}

	private static void setupGroup(AccuracyRouter<Long> router) {
		int ip = 2;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 3; j++) {
				String name = "192.168.1." + (ip++) + ":8002";
				Resource resource = new Resource(name);
				SliceResource sliceResource = new SliceResource(resource);
				sliceResource.setFunction(j == 0 ? Function.Write
						: Function.Read);
				sliceResource.addParams(extraInfo(ip));
				router.register(Long.valueOf(i), String.valueOf(i),
						sliceResource);
			}
		}
	}

}
