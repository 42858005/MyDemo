# activiti-demo
## Springboot2.1.6
## activiti6.0
## mybatis

三者集成需要注意几个问题：
1. springboot启动类注解@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
2. 需要在mybatis里面排除javax.persistence，这和activiti里面自带的jpa冲突
  排除方法：我这里用了tk.mybatis插件，在其dependence标签内加入 <exclusions><exclusion><groupId>javax.persistence</groupId>     <artifactId>persistence-api</artifactId></exclusion></exclusions>
