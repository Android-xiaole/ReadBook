package com.jj.comics.util.eventbus;



import com.jj.comics.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import java.util.HashMap;

/**
 * @link https://www.jianshu.com/p/67b495c7b56a (关于MyEventBusIndex的使用说明)
 */
public class EventBusHelper {

    // EventBus 索引类
    private static final SubscriberInfoIndex SUBSCRIBE_INDEX = new MyEventBusIndex();

    // 这个类是否需要注册 EventBus
    private static final HashMap<String, Boolean> SUBSCRIBE_EVENT = new HashMap<>();

    // 不允许被外部实例化
    private EventBusHelper() {}

    /**
     * 初始化 EventBus
     */
    public static void init() {
        EventBus.builder()
                .ignoreGeneratedIndex(false) // 使用 Apt 插件
                /*
                    添加索引类，该项目我只生成了comic下面的EventBusIndex
                    如果别的module也要使用，需要在对应module配置EventBus的annotationProcessor
                    初始化的时候add进来就可以了，到时候这个类就需要放在基础module下面重新定义一个add方法
                 */
                .addIndex(SUBSCRIBE_INDEX)
                .installDefaultEventBus(); // 作为默认配置
    }

    /**
     * 注册 EventBus,这种方法注册就不用捕获EventBusException异常了
     * 鉴于baseactivity里面使用boolean值控制EventBus的注册方式使用的地方过多，暂时不使用该注册方法
     */
    public static void register(Object subscriber) {
        if (canSubscribeEvent(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    /**
     * 反注册 EventBus
     */
    public static void unregister(Object subscriber) {
        if (canSubscribeEvent(subscriber) && EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    /**
     * 判断是否使用了 EventBus 注解
     *
     * @param subscriber                 被订阅的类
     */
    private static boolean canSubscribeEvent(Object subscriber) {
        Class<?> clazz = subscriber.getClass();
        // 这个 Class 类型有没有遍历过
        Boolean result = SUBSCRIBE_EVENT.get(clazz.getName());
        if (result != null) {
            // 有的话直接返回结果
            return result;
        }

        // 没有的话进行遍历
        while (clazz != null) {
            // 如果索引集合中有这个 Class 类型的订阅信息，则这个类型的对象都需要注册 EventBus
            if (SUBSCRIBE_INDEX.getSubscriberInfo(clazz) != null) {
                // 这个类需要注册 EventBus
                result = true;
                clazz = null;
            } else {
                String clazzName = clazz.getName();
                // 跳过系统类（忽略 java. javax. android. androidx. 等开头包名的类）
                if (clazzName.startsWith("java") || clazzName.startsWith("android")) {
                    clazz = null;
                } else {
                    // 往上查找
                    clazz = clazz.getSuperclass();
                }
            }
        }
        // 这个类不需要注册 EventBus
        if (result == null) {
            result = false;
        }
        SUBSCRIBE_EVENT.put(subscriber.getClass().getName(), result);
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(EventBusHelper helper) {
        // 占位，只为了能生成 MyEventBusIndex 索引类
        // 如果项目中已经有用到 @Subscribe 去注解方法，这个方法可以直接删除
    }
}
