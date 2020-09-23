
## 【实现延时业务处理】

### 技术说明

   Spring boot、Zookeeper、DelayQueue

### 功能说明

	1、适用于延时处理某一项业务，如活动前的提醒、活动结束后延时自动处理一些业务、延时订单等等一此延时处理的业务;
	2、解决传统通过轮询的方式实现限时处理的业务，传统的方式有两大缺点：
		a、轮询消耗资源，做了很多的无用功;
		b、处理不及时，有时间差、不精确等问题。
	3、时间精准，可实现延时处理;
	4、可避免应用集群化等问题;
	5、对外开放消息队列的查询、添加、删除操作，可以实时的对消息队列进行更新处理。
	
### 设计思想
    
    1、通过zookeeper注册监听事件，当延时队列当中某消息到期后，处理完业务，触发监听事件，使各节点重新初始化延时队列的数据;
    2、通过java DelayQueue实现延时处理;

### 使用配置


##### 1、引入插件依赖;

		<dependency>
        	<groupId>com.myh.delayed</groupId>
        	<artifactId>myh-delayed-core</artifactId>
        	<version>1.0.0</version>
        </dependency>

##### 2、配置DelayedConfig和ZookeeperConfig

	@Configuration
    public class DelayedConfig {
    
        @Resource(name = "queueZkClient")
        private ZkClient zkClient;
    
        private static final String zkListenerKey = "/myh_lock001";
    
        @Autowired
        private IDlyProcessorService dlyProcessorService;
    
        @Bean("delayedQueue")
        public InitDelayedQueue delayedQueue() {
            InitDelayedQueue config = new InitDelayedQueue();
            config.setDlyProcessorService(dlyProcessorService);//处理消息到期的业务类接口;
            config.setZkClient(zkClient);//建立zkclient连接的对象;
            config.setZkListenerKey(zkListenerKey);//当消息队列发生变量，zk监听的节点名称,请以"/"为前缀;
            return config;
        }
    
    }
    
    -----------------------------------------------------------------------------------------------------------------
    
    @Configuration
    @Slf4j
    public class ZookeeperConfig implements InitializingBean {
    
        private String zkAddress;
    
        @Override
        public void afterPropertiesSet() throws Exception {
            zkAddress = "192.168.1.1:2181";
            log.info(">>>lock zookeeper msg：" + zkAddress);
        }
    
        @Bean(name = "queueZkClient")
        public ZkClient getZkClient() {
            ZkClient zkClient = new ZkClient(zkAddress, 30000, 10000);
            return zkClient;
        }
    
    }



##### 3、消息到期，处理相关的业务，请自行定义实现类，继承接口：IDlyProcessorService,

	@Service
    @Slf4j
    public class DlyProcessorService implements IDlyProcessorService {
    
        private DelayedQueueHandle delayedQueueHandle;
    
        private List<DelayedObject> delayedObjectList = new ArrayList<>();
    
        public DlyProcessorService() {
            DelayedObject dq = new DelayedObject();
            dq.setDelayTime(30);
            dq.setId(100);
    
            DelayedObject dq2 = new DelayedObject();
            dq2.setDelayTime(40);
            dq2.setId(101);
            delayedObjectList.add(dq);
            delayedObjectList.add(dq2);
        }
    
        /**
         * 当延时队列消息到期，处理相关业务<br>
         * 注意：
         * 如果服务是集群部署，要处理的业务记得加锁，保证多个节点只有一个能正常执行;相反，不用加锁;
         *
         * @param id 消息主键ID;
         */
        @Override
        public void process(long id) {
    
            log.info(">>>  消息到期，处理业务数据 start：{}", id);
    
            if (id == 100) {
                delayedObjectList.remove(0);
            } else {
                delayedObjectList.remove(0);
            }
    
            log.info(">>>  消息到期，处理业务数据 end：{}", id);
        }
    
        /**
         * 应用于以下情况：
         * 1、当消息到期，会触发监听重新初始化延时列表数据(解决服务集群部署，保证各节点延时队列数据一致。)
         * 2、当项目启动，初始化延时队列数据;
         */
        @Override
        public void bootInit() {
    
            log.info(">>> 初始化延时队列;");
    
            //延时订单示例：计算延长时长：
            // 下单时间(2020-10-01 00:00:00)  延时时长(20m)  当前时间(2020-10-01 10:00:00)  最后需要延长时长;
            // 最后需要延长时长 = 延长时间-(当前时间 - 下单时间);
    
            for (int i = 0; i < delayedObjectList.size(); i++) {
                delayedQueueHandle.addDelayQueue(delayedObjectList.get(i));//这是单条消息队列添加,如果需要初始化多条，遍历循环即可。
            }
    
        }
    }


### 开放api说明:

##### 通过DelayedQueueHandle对象，可以添加延时队列、查询缓存中所有的延时队列、移除缓存中的延时队列。
	
	
	    /**
         * 往列列里面添加队列消息;
         *
         * @param delayedObject
         */
        public static void addDelayQueue(DelayedObject delayedObject);
        
        /**
         * 查询所有的延时队列消息;
         */
        public static List<DelayedQueueVo> selDelayQueue();
        
        /**
         * 从延时队列当中移除指定的延时队列对象;
         */
        public static boolean removeDelayQueue(DelayedObject delayedObject);
        
        /**
         * 清除所有延时列表数据;
         */
        public static void clearAllDelayQueue();
        
        
	
	
