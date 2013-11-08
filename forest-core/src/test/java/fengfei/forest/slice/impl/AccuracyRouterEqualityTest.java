package fengfei.forest.slice.impl;

import java.util.*;

import fengfei.forest.slice.Router;
import fengfei.forest.slice.equalizer.LongTailEqualizer;
import org.junit.Before;
import org.junit.Test;

import fengfei.forest.slice.OverflowType;
import fengfei.forest.slice.Resource;
import fengfei.forest.slice.SliceResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccuracyRouterEqualityTest extends AbstractRouterTest {

    AccuracyRouter<Long> router = new AccuracyRouter<>();
    int size = 60;

    @Before
    public void setUp() throws Exception {
        isReadWrite = true;
        router.setOverflowType(OverflowType.Exception);
        int ip = 2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 3; j++) {
                String name = "192.168.1." + (ip++) + ":8002";
                Resource resource = new Resource(name);
                Long sliceId = Long.valueOf(i);
                SliceResource sliceResource = new SliceResource(sliceId, resource);
                sliceResource.addParams(extraInfo("192.168.1." + ip, 8002));
                router.register(Long.valueOf(i), String.valueOf(i), sliceResource);
            }
        }
    }

    Random random = new Random();

    @Test
    public void testLocateKeyFunction() {
        for (int i = 0; i < size; i++) {
            testLocateKeyFunction(router, i);
        }
    }

    @Test
    public void testLocateKeyFunctionForException() {
        router.setOverflowType(OverflowType.Exception);
        int id = Math.abs(random.nextInt() % size) + size;
        testLocateKeyFunctionForException(router, id);
        //
        router.setOverflowType(OverflowType.Last);
        id = Math.abs(random.nextInt() % size) + size;
        testLocateKeyFunctionForException(router, id);
        //
        router.setOverflowType(OverflowType.First);
        id = Math.abs(random.nextInt() % size) + size;
        testLocateKeyFunctionForException(router, id);
        //
    }

    @Test
    public void testLocateKey() {
        for (int i = 0; i < size; i++) {
            testLocateKey(router, i);
        }
    }

    @Test
    public void testGroupLocateKeyFunction() {
        AccuracyRouter<Long> router = new AccuracyRouter<>(new LongTailEqualizer(1));
        router.setOverflowType(OverflowType.Exception);
        int ip = 2;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 3; j++) {
                String name = "192.168.1." + (ip++) + ":8002";
                Resource resource = new Resource(name);
                Long sliceId = Long.valueOf(i);
                SliceResource sliceResource = new SliceResource(sliceId, resource);
                sliceResource.addParams(extraInfo("192.168.1." + ip, 8002));
                router.register(Long.valueOf(i), String.valueOf(i), sliceResource);
            }

        }
        Long[] ids = new Long[180];
        for (int i = 0; i < 180; i++) {
            ids[i] = new Long(i);
        }
        //


        Map<SliceResource, List<Long>> resources = router.groupLocate(SliceResource.Function.Read,
                Arrays.asList(ids));
        assertNotNull(resources);
        System.out.println(resources);
        Set<Map.Entry<SliceResource, List<Long>>> entries = resources.entrySet();
        for (Map.Entry<SliceResource, List<Long>> entry : entries) {
            SliceResource resource = entry.getKey();
            List<Long> keys = entry.getValue();
            assertNotNull(keys);
            assertEquals(18, keys.size());
        }
    }

    public void setRouter(AccuracyRouter<Long> router) {
        this.router = router;
    }
}
