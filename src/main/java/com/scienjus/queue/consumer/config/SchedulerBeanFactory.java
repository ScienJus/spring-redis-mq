package com.scienjus.queue.consumer.config;

import com.scienjus.queue.consumer.Consumer;
import com.scienjus.queue.consumer.annotation.OnMessage;
import com.scienjus.queue.consumer.domain.ConsumeHandlerMethod;
import com.scienjus.queue.consumer.worker.ConsumeWorker;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * @author ScienJus
 * @date 2015/12/8.
 */
public class SchedulerBeanFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Consumer consumer;

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void init() {
        //获得所有消费者Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(com.scienjus.queue.consumer.annotation.Consumer.class);
        //获得Bean Factory
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            String name = entry.getKey();
            Object bean = entry.getValue();
            Class clazz = applicationContext.getType(name);
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(OnMessage.class)) {
                    OnMessage onMessage = method.getAnnotation(OnMessage.class);
                    String topic = onMessage.value();
                    ConsumeHandlerMethod consumeHandlerMethod = new ConsumeHandlerMethod(topic, method, bean);
                    ConsumeWorker worker = new ConsumeWorker(consumeHandlerMethod, consumer);

                    //注册JobDetail
                    String jobDetailBeanName = buildJobDetailBeanName(consumeHandlerMethod);
                    registerJobDetail(beanFactory, jobDetailBeanName, worker);

                    //注册Trigger
                    registerTrigger(beanFactory, buildTriggerBeanName(consumeHandlerMethod), jobDetailBeanName);
                }
            }
        }
        //注册Scheduler
        registerScheduler(beanFactory);
    }

    private String buildJobDetailBeanName(ConsumeHandlerMethod method) {
        return method.getTopic().concat("JobDetail");
    }

    private String buildTriggerBeanName(ConsumeHandlerMethod method) {
        return method.getTopic().concat("Trigger");
    }

    private void registerJobDetail(DefaultListableBeanFactory beanFactory, String beanName, ConsumeWorker worker) {
        BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.rootBeanDefinition(MethodInvokingJobDetailFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("targetObject", worker);
        beanDefinitionBuilder.addPropertyValue("targetMethod", "invoke");
        beanDefinitionBuilder.addPropertyValue("concurrent", true);
        beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerTrigger(DefaultListableBeanFactory beanFactory, String beanName, String jobDetailBeanName) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SimpleTriggerFactoryBean.class);
        beanDefinitionBuilder.addPropertyReference("jobDetail", jobDetailBeanName);
        beanDefinitionBuilder.addPropertyValue("startDelay", 1000); //可自定义但是没什么必要(?)
        beanDefinitionBuilder.addPropertyValue("repeatInterval", 2000); //可自定义
        beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    private void registerScheduler(DefaultListableBeanFactory beanFactory) {
        Collection<Trigger> triggers = beanFactory.getBeansOfType(Trigger.class).values();
        BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.rootBeanDefinition(SchedulerFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("triggers", triggers);
        beanFactory.registerBeanDefinition("consumerScheduler", beanDefinitionBuilder.getBeanDefinition());
    }
}
