
## 【实现延时业务处理】

### 功能说明

	1、适用于延时处理某一项业务，如活动前的提醒、活动结束后延时自动处理一些业务、延时订单等等一此延时处理的业务;
	2、解决传统通过轮询的方式实现限时处理的业务，传统的方式有两大缺点：
		a、轮询消耗资源，做了很多的无用功;
		b、处理不及时，有时间差、不精确等问题。
	3、时间精准，可实现延时处理;
	4、可避免应用集群化等问题;
	5、对外开放消息队列的查询、添加、删除操作，可以实时的对消息队列进行更新处理。

### 使用配置


##### 1、引入插件依赖;

		<dependency>
			<groupId>com.myh.plugin</groupId>
			<artifactId>spring-boot-delayed</artifactId>
			<version>1.0.1</version>
		</dependency>


##### 2、消息到期，处理相关的业务，请自行定义实现类，继承接口：IDlyProcessorService,

	@Resource(name = "delayedQueue")
    private DelayedQueueConfig delayedQueue;


	/**
     * 消息到期，要处理业务;<br>
     * 要处理的业务记得加锁哟，解决分布式幂等性问题;
     * 
     * @param id 消息主键ID;
     */
    @Override
    public void process(long id) {
    	 Lock lockQueue = LockFactory.getLock();
    	 try {
    	 	lockQueue.lock();
        	log.info(">>>  消息到期，处理业务数据;");
    	 } finally {
            lockQueue.unlock();
        }
    }


	/**
     * 项目启动，初始化延时队列;
     */
    @Override
    public void boot() {
        log.info(">>> 当消息到期，会触发监听;项目启动，初始化延时队列;");
        delayedQueue.addDelayQueue(dq);//这是单条消息队列添加,如果需要初始化多条，遍历循环即可。
    }



##### 3、应用端：向spirng注入DelayedQueueConfig的@Bean对象;

	@Bean("delayedQueue")
	public DelayedQueueConfig delayedQueue(){
	    DelayedQueueConfig config = new DelayedQueueConfig();
	    config.setDlyProcessorService(dlyProcessorService);//处理消息到期的业务类接口;
	    config.setZkClient(zkClient);//建立zkclient连接的对象;
	    config.setZkListenerKey(zkListenerKey);//当消息队列发生变量，zk监听的节点名称,请以"/"为前缀;
	    return config;
	}


##### 备注说明：如果有多个延时队列对象要监听，可以向spring注入多个不同名的delayedQueueConfig对象即可。



### 开放api说明:


##### 通过DelayedQueueConfig 对象，可以添加延时队列、查询缓存中所有的延时队列、移除缓存中的延时队列。
	
	
	/**
     * 添加延时消息队列;
     */
	public void addDelayQueue(DelayedObject delayedObject);
	
	/**
     * 查询延时队列消息内容;
     */
    public List<DelayedQueueVo> selDelayQueue()
	
	/**
     * 从延时队列当中移除指定的延时队列对象;
     */
    public boolean removeDelayQueue(DelayedObject delayedObject)
	
	
