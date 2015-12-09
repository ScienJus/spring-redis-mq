# spring-redis-mq

基于Spring和Redis的分布式消息队列（MessageQueue）

### 使用方法

**创建项目**

由于这个库还没有提交到Maven的中央仓库，所以需要手动将其导入到你的私人仓库中。首先`fork`源码到本地后使用`mvn package`打包。

然后添加到本地仓库：

```
mvn install:install-file  
-DgroupId=com.scienjus
-DartifactId=spring-redis-mq
-Dversion=1.0-SNAPSHOT
-Dpackaging=jar  
-Dfile=/path/to/jar/spring-authorization-manager.jar
```

所有依赖 Jar：

```
<properties>
  <spring.version>4.1.8.RELEASE</spring.version>
  <jedis.version>2.7.3</jedis.version>
  <aspectj.version>1.8.7</aspectj.version>
  <quartz.version>2.2.1</quartz.version>
</properties>

<dependencies>
  <dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>${jedis.version}</version>
  </dependency>

  <!-- For quartz -->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>${quartz.version}</version>
  </dependency>

  <!--For aop-->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>${spring.version}</version>
  </dependency>

  <dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>${aspectj.version}</version>
  </dependency>
</dependencies>
```

**配置Spring Bean**

配置Jedis客户端：

```
@Bean
public JedisPool jedisPool() {
    return new JedisPool("127.0.0.1", 6379);
}
```

配置消费者：

```
@Bean
public Consumer consumer() {
    RedisConsumer consumer = new RedisConsumer();
    consumer.setJedisPool(jedisPool());
    return consumer;
}
```

配置生产者：

```
@Bean
public Producer producer() {
    RedisProducer producer = new RedisProducer();
    producer.setJedisPool(jedisPool());
    return producer;
}
```

配置消费者定时扫描任务（仅当使用注解驱动的消费者时才需要配置）：

```
@Bean(initMethod = "init")
public SchedulerBeanFactory schedulerBeanFactory() {
    SchedulerBeanFactory schedulerBeanFactory = new SchedulerBeanFactory();
    schedulerBeanFactory.setConsumer(consumer());
    return schedulerBeanFactory;
}
```

注意一定要将`initMethod`设为`init`方法。

配置生产者自动推送任务（仅当使用注解驱动的生产者时才需要配置）：

```
@Bean
public MessageHandler messageHandler() {
    MessageHandler messageHandler = new MessageHandler();
    messageHandler.setProducer(producer());
    return messageHandler;
}
```

**创建生产者实例**

方法1：注入`producer`，调用`sendMessage`方法：

```
@Component
public class SayHelloProducer {

    @Autowired
    private Producer producer;

    public void sayHello(String name) {
        producer.sendMessage("say_hello", name);
    }
}
```

方法2：使用`@Topic`注解，`retrun`需要发送的对象（需要配置`messageHandler`）：

```
@Producer
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Topic("new_user")  //添加新的用户后，将其发送到消息队列
    public User insert(User user) {
        this.userDao.insert(user);
        return user;
    }
}
```

**创建消费者实例**

方法1：注入`consumer`，调用`getMessage`方法（需要自己开线程循环获取）：

```
@Component
public class SayHelloConsumer {

    @Autowired
    private Consumer consumer;

    public void sayHello() {
        String name;
        while ((name = consumer.getMessage("say_hello")) != null) {
            System.out.println("Hello ! " + name + " !");
        }
    }
}
```

方法2：为类添加`@Consumer`注解，为对应的方法添加`@OnMessage`注解（主要配置`schedulerBeanFactory`）：

```
@Consumer
public class SayHelloConsumer {

    @OnMessage("say_hello")
    public void onSayHello(String name) {
        System.out.println("Hello ! " + name + " !");
    }
}
```

**消费者的重试机制**

当`@OnMessage`消费者方法的返回值类型为`boolean`类型，并且执行的结果为`false`时，系统认定此消息消费失败。



如果设置了方法的重试次数，系统会将这个消息重新插入到消息队列中（顺序排在最后）。

当该消息的失败次数大于重试次数后，系统会抛弃掉这条消息。

通过`schedulerBeanFactory`的`defaultMaxRetryTimes`属性可以设置全局的重试次数。

通过`@OnMessage`的`maxRetryTimes`属性可以设置每一个方法的重试次数。

一个简单的例子：

```
@Consumer
public class SayHelloConsumer {

    @OnMessage(value = "new_user", maxRetryTimes = 3) //如果邮件发送失败，会尝试重新发送3次。
    public boolean onNewUser(User user) {
        try {
            //发送邮件
            MailSender.sendWelcomeMail(user.getEmail(), user.getNickname());
            //发送成功，任务完成，返回true
            retrun true;
        } catch (Exception e) {
            //发送失败，尝试重试，返回false
            retrun false;
        }
    }
}
```

当然，如果一个消费方法永远不会失败（或是失败后不需要重试），可以直接设置为`void`方法。

### 待办事项

- [ ] 生产者发送消息失败的处理
- [x] 消费者任务失败的处理（设置最大失败次数，重新插入队列）
- [ ] 监控页面

### 帮助

联系方式：xie_enlong@foxmail.com

一个简单的[Demo][1]

[1]: https://github.com/ScienJus/spring-redis-mq-demo
