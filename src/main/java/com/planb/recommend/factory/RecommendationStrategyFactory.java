package com.planb.recommend.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.planb.config.BaseUnCheckedException;
import com.planb.constant.EngineConstant;
import com.planb.constant.ErrorConstant;
import com.planb.recommend.stratege.RecommendationStrategy;
import com.planb.recommend.stratege.impl.AIRecommendationStrategy;
import com.planb.recommend.stratege.impl.ContentBasedStrategy;
import com.planb.recommend.stratege.impl.CollaborativeFilteringStrategy;
import com.planb.recommend.stratege.impl.PositionAndExposureStrategy;
import com.planb.recommend.stratege.impl.SVDStrategy;

import javax.annotation.PostConstruct;

/**
 * 推荐策略工厂类，负责根据不同的推荐引擎类型创建相应的推荐策略实例。
 * 该工厂类使用了策略模式（Strategy Pattern），通过映射引擎类型到对应的推荐策略供应商，
 * 实现了动态选择和创建推荐策略的目的。
 */
@Component
@SuppressWarnings("all")
public class RecommendationStrategyFactory {

    /**
     * 应用上下文，用于获取Bean实例。
     */
    private final ApplicationContext applicationContext;

    /**
     * 存储推荐策略供应商的映射，键为引擎类型，值为策略供应商的函数式接口。
     */
    private Map<String, Supplier<RecommendationStrategy>> strategyMap;

    /**
     * 构造函数，初始化应用上下文。
     *
     * @param applicationContext 应用上下文，用于后续获取Bean实例。
     */
    @Autowired
    public RecommendationStrategyFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 初始化方法，在所有依赖注入完成后调用。
     * 该方法负责填充strategyMap，将引擎类型与对应的推荐策略供应商映射起来。
     */
    @Autowired
    @PostConstruct
    public void initialize() {
        strategyMap = new HashMap<>();
        strategyMap.put(EngineConstant.CONTENT, () -> applicationContext.getBean(ContentBasedStrategy.class));
        strategyMap.put(EngineConstant.COORDINATED_FILTERING, () -> applicationContext.getBean(CollaborativeFilteringStrategy.class));
        strategyMap.put(EngineConstant.SVD, () -> applicationContext.getBean(SVDStrategy.class));
        strategyMap.put(EngineConstant.POSITION_AND_EXPOSURE, () -> applicationContext.getBean(PositionAndExposureStrategy.class));
        strategyMap.put(EngineConstant.AI, () -> applicationContext.getBean(AIRecommendationStrategy.class));
    }

    /**
     * 根据引擎类型创建并返回推荐策略实例。
     * 如果给定的引擎类型不在映射中，则抛出异常。
     *
     * @param engineType 推荐引擎的类型。
     * @return 对应引擎类型的推荐策略实例。
     * @throws BaseUnCheckedException 如果引擎类型未知，则抛出此异常。
     */
    public RecommendationStrategy createStrategy(String engineType) {
        Supplier<RecommendationStrategy> supplier = strategyMap.get(engineType);
        if (supplier == null) {
            throw new BaseUnCheckedException(ErrorConstant.UNKNOWN_ENGINE);
        }
        return supplier.get();
    }
}
