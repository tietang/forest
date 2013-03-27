package fengfei.forest.slice.example;

import java.util.HashMap;
import java.util.Map;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.SliceResource;
import fengfei.forest.slice.SliceResource.Function;
import fengfei.forest.slice.SelectType;
import fengfei.forest.slice.impl.AccuracyRouter;
import fengfei.forest.slice.impl.LongEqualizer;

public class AccuracyRouterExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccuracyRouter<Long> router = new AccuracyRouter<>(new LongEqualizer());
		setupGroup(router);
		router.setSelectType(SelectType.Hash);
		router.setOverflowType(OverflowType.Last);
		System.out.println(router);
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
				SliceResource resource = new SliceResource(name);
				resource.setFunction(j == 0 ? Function.Write : Function.Read);
				resource.addExtraInfo(extraInfo());
				router.register(Long.valueOf(i), resource);
			}

		}
	}

	private static Map<String, String> extraInfo() {
		Map<String, String> extraInfo = new HashMap<String, String>();
		extraInfo.put("info1", "info1 value");
		extraInfo.put("info2", "info2 value");
		extraInfo.put("user", "user");
		extraInfo.put("password", "pwd");
		return extraInfo;
	}

}
