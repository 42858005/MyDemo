# activiti-demo
## Springboot2.1.6
## activiti6.0
## mybatis

三者集成需要注意几个问题：
1. springboot启动类注解@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
2. 需要在mybatis里面排除javax.persistence，这和activiti里面自带的jpa冲突。排除方法：我这里用了tk.mybatis插件，在其dependence标签内加入<br>
 `<exclusions>`<br>
    `<exclusion>`<br>
        `<groupId>`<br>
            `javax.persistence`<br>
         `</groupId>`     <br>
         `<artifactId>`<br>
              `persistence-api`<br>
         `</artifactId>`<br>
    `</exclusion>`<br>
  `</exclusions>`<br>

ps:无前端，可以通过postman等工具进行测试